package net.lopymine.patpat.plugin.packet.handler;

import com.google.common.io.ByteArrayDataInput;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.command.ratelimit.RateLimitManager;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.config.option.ListMode;
import net.lopymine.patpat.plugin.entity.PatPlayer;
import net.lopymine.patpat.plugin.extension.ByteArrayDataExtension;
import net.lopymine.patpat.plugin.packet.IPatPacket;
import net.lopymine.patpat.plugin.packet.PatPacket;
import net.lopymine.patpat.plugin.packet.PatPacketV1;
import net.lopymine.patpat.plugin.packet.PatPacketV2;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@ExtensionMethod(ByteArrayDataExtension.class)
public class PatPacketHandler implements IPacketHandler {

    private static final String PATPAT_C2S_PACKET_ID_V2 = StringUtils.modId("pat_entity_c2s_packet_v2");
    private static final String PATPAT_S2C_PACKET_ID_V2 = StringUtils.modId("pat_entity_s2c_packet_v2");

    // Immutable -> thread-safe
    private static final List<IPatPacket> PAT_PACKET_HANDLERS =
            List.of(new PatPacketV2(), new PatPacketV1());

    private static final double CREATIVE_INTERACT_DISTANCE = 3.1;
    private static final double SURVIVOR_INTERACT_DISTANCE = 5.1;
    private static final Function<Player, Double> GET_INTERACT_DISTANCE = getInteractDistanceFunction();

    private final double patVisibilityRadius = Bukkit.getServer().getViewDistance() * 16D;

    private static Function<Player, Double> getInteractDistanceFunction() {
        try {
            Attribute attribute = Attribute.PLAYER_ENTITY_INTERACTION_RANGE;
            return player -> {
                AttributeInstance attr = player.getAttribute(attribute);
                if (attr == null) {
                    return player.getGameMode() == GameMode.CREATIVE ? CREATIVE_INTERACT_DISTANCE : SURVIVOR_INTERACT_DISTANCE;
                }
                return attr.getValue() + 0.1;
            };
        } catch (NoSuchFieldError e) {
            return player -> player.getGameMode() == GameMode.CREATIVE ? CREATIVE_INTERACT_DISTANCE : SURVIVOR_INTERACT_DISTANCE;
        }
    }

    @Override
    public void handle(PatPlayer sender, ByteArrayDataInput buf) {
        final PatPatPlugin plugin = PatPatPlugin.getInstance();
        final Player senderPlayer = sender.getPlayer();

        // ВАЖНО: даже если listener уже запланировал на scheduler игрока,
        // оставляем "guard" на случай вызова из другого места.
        senderPlayer.getScheduler().run(plugin, st -> handleOnSenderThread(sender, senderPlayer, buf), () -> {});
    }

    private void handleOnSenderThread(PatPlayer sender, Player senderPlayer, ByteArrayDataInput buf) {
        if (!this.canHandle(senderPlayer)) {
            return;
        }

        if (senderPlayer.getGameMode() == GameMode.SPECTATOR || senderPlayer.isDead()) {
            return;
        }

        final IPatPacket senderPacketHandler = sender.getPatPacketHandler();
        if (senderPacketHandler == null) {
            PatLogger.debug("Not found packet handler for player with PatPat version: %s", sender.getVersion());
            return;
        }

        final Entity pattedEntity = senderPacketHandler.getPattedEntity(sender, buf);
        if (!(pattedEntity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (livingEntity.isInvisible()) {
            return;
        }

        if (pattedEntity.equals(senderPlayer)) {
            return;
        }

        if (senderPlayer.getWorld() != livingEntity.getWorld()) {
            return;
        }

        // Проверка дистанции делается на потоке sender'а: это чтение sender state.
        final BoundingBox entityBox = livingEntity.getBoundingBox();
        final double interactDistance = GET_INTERACT_DISTANCE.apply(senderPlayer);

        final var senderEye = senderPlayer.getEyeLocation();
        final double senderX = senderEye.getX();
        final double senderY = senderEye.getY();
        final double senderZ = senderEye.getZ();

        final double dx = Math.min(Math.abs(entityBox.getMaxX() - senderX), Math.abs(entityBox.getMinX() - senderX));
        if (dx > interactDistance) return;

        final double dz = Math.min(Math.abs(entityBox.getMaxZ() - senderZ), Math.abs(entityBox.getMinZ() - senderZ));
        if (dz > interactDistance) return;

        final double dy = Math.min(Math.abs(entityBox.getMaxY() - senderY), Math.abs(entityBox.getMinY() - senderY));
        if (dy > interactDistance) return;

        // Дальше всё, что связано с миром вокруг цели (nearbyEntities) — выполняем на scheduler цели.
        livingEntity.getScheduler().run(
                PatPatPlugin.getInstance(),
                st -> handleOnTargetThread(sender, senderPlayer, livingEntity),
                () -> {}
        );
    }

    private void handleOnTargetThread(PatPlayer sender, Player senderPlayer, LivingEntity target) {
        final PatPatPlugin plugin = PatPatPlugin.getInstance();

        // Кешируем пакеты по версии (id обработчика) на время рассылки
        final Map<String, PatPacket> packetsByHandlerId = new ConcurrentHashMap<>();
        final UUID senderUuid = sender.getUniqueId();

        // Поиск ближайших игроков выполняем на потоке цели (это важно для Folia).
        final List<Player> recipients = target.getNearbyEntities(patVisibilityRadius, patVisibilityRadius, patVisibilityRadius)
                .stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .toList();

        // Если цель — игрок, добавляем его (getNearbyEntities может не включать самого себя)
        if (target instanceof Player targetPlayer) {
            // Не дублируем
            if (recipients.stream().noneMatch(p -> p.getUniqueId().equals(targetPlayer.getUniqueId()))) {
                // Создадим новый список только если надо
                List<Player> tmp = new ArrayList<>(recipients);
                tmp.add(targetPlayer);
                sendToPlayers(tmp, senderPlayer, senderUuid, target, packetsByHandlerId);
                return;
            }
        }

        sendToPlayers(recipients, senderPlayer, senderUuid, target, packetsByHandlerId);
    }

    private void sendToPlayers(List<Player> recipients,
                               Player senderPlayer,
                               UUID senderUuid,
                               LivingEntity target,
                               Map<String, PatPacket> packetsByHandlerId) {

        final PatPatPlugin plugin = PatPatPlugin.getInstance();

        for (Player receiver : recipients) {
            if (receiver.getUniqueId().equals(senderUuid)) {
                continue;
            }

            final PatPlayer patReceiver = PatPlayer.of(receiver);
            final IPatPacket packetHandler = patReceiver.getPatPacketHandler();
            if (packetHandler == null) {
                continue;
            }

            final PatPacket packet = packetsByHandlerId.computeIfAbsent(
                    packetHandler.getPacketHandlerId(),
                    id -> packetHandler.getPacket(target, senderPlayer)
            );

            // Отправка делается в контексте получателя (EntityScheduler) — safest для Folia.
            receiver.getScheduler().run(plugin, st -> {
                PatLogger.debug("Sending out pat packet to %s with id %s and data %s",
                        receiver.getName(), packet.channel(), Arrays.toString(packet.bytes()));
                receiver.sendPluginMessage(plugin, packet.channel(), packet.bytes());
            }, () -> {});
        }
    }

    public static IPatPacket getPacketHandler(PatPlayer player) {
        for (IPatPacket packetHandler : PAT_PACKET_HANDLERS) {
            if (packetHandler.canHandle(player)) {
                return packetHandler;
            }
        }
        return null;
    }

    private boolean canHandle(Player sender) {
        UUID senderUuid = sender.getUniqueId();
        if (!sender.hasPermission(PatPatConfig.getInstance().getRateLimit().getPermissionBypass())
                && !RateLimitManager.canPat(senderUuid)) {
            return false;
        }

        Set<UUID> uuids = PlayerListConfig.getInstance().getUuids();
        ListMode listMode = PatPatConfig.getInstance().getListMode();

        return switch (listMode) {
            case DISABLED -> true;
            case WHITELIST -> uuids.contains(senderUuid);
            case BLACKLIST -> !uuids.contains(senderUuid);
        };
    }

    @Override
    public String getIncomingPacketId() {
        return PATPAT_C2S_PACKET_ID_V2;
    }

    @Override
    public String getOutgoingPacketId() {
        return PATPAT_S2C_PACKET_ID_V2;
    }
}
