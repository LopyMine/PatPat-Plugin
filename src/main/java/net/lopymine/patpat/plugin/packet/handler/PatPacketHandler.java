package net.lopymine.patpat.plugin.packet.handler;

import com.google.common.io.ByteArrayDataInput;
import lombok.experimental.ExtensionMethod;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.util.BoundingBox;
import ru.nik51.patpat.plugin.api.event.PatPacketReceiveEvent;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.entity.*;
import net.lopymine.patpat.plugin.ratelimit.RateLimitManager;
import net.lopymine.patpat.plugin.config.*;
import net.lopymine.patpat.plugin.extension.ByteArrayDataExtension;
import net.lopymine.patpat.plugin.packet.*;
import net.lopymine.patpat.plugin.util.StringUtils;
import net.lopymine.patpat.plugin.util.UuidUtils;

import java.util.*;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(ByteArrayDataExtension.class)
public class PatPacketHandler implements IPacketHandler {

	private static final String PATPAT_C2S_PACKET_ID_V2 = StringUtils.modId("pat_entity_c2s_packet_v2");
	private static final String PATPAT_S2C_PACKET_ID_V2 = StringUtils.modId("pat_entity_s2c_packet_v2");

	private static final Set<IPatPacket> PAT_PACKET_HANDLERS = new LinkedHashSet<>();
	private static final Function<Player, Double> GET_INTERACT_DISTANCE = getInteractDistanceFunction();

	private static final double CREATIVE_INTERACT_DISTANCE = 3.1;
	private static final double SURVIVOR_INTERACT_DISTANCE = 5.1;
	private static final double PAT_VISIBILITY_RADIUS = Bukkit.getServer().getViewDistance() * 16D;


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

	public PatPacketHandler() {
		// MUST be in order from newer to older
		// Otherwise it will be handled in the wrong way, for example,
		// 1) 1.2.0(Client Version) >= 1.0.0(PatPacketV1)?
		// -> Use 1.0.0 packets (wrong)
		// 2) 1.2.0(Client Version) >= 1.2.0(PatPacketV2)?
		// -> Use 1.2.0 packets, you will say, but we are already using 1.0.0 packets because of the wrong order

		PAT_PACKET_HANDLERS.clear();
		PAT_PACKET_HANDLERS.add(new PatPacketV2());
		PAT_PACKET_HANDLERS.add(new PatPacketV1());
	}

	@Override
	public void handle(IPatPlayer sender, ByteArrayDataInput buf) {
		Player senderPlayer = sender.getPlayer();
		if (!this.canHandle(senderPlayer)) {
			return;
		}

		if (senderPlayer.getGameMode() == GameMode.SPECTATOR || senderPlayer.isDead()) {
			return;
		}

		IPatPacket senderPacketHandler = sender.getPatPacketHandler();
		if (senderPacketHandler == null) {
			PatLogger.debug("Not found packet handler for player with PatPat version: %s", sender.getVersion());
			return;
		}

		Entity pattedEntity = senderPacketHandler.getPattedEntity(sender, buf);
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

		Location senderLocation = senderPlayer.getEyeLocation();
		double senderX = senderLocation.getX();
		double senderY = senderLocation.getY();
		double senderZ = senderLocation.getZ();
		BoundingBox entityBox = livingEntity.getBoundingBox();
		double interactDistance = GET_INTERACT_DISTANCE.apply(senderPlayer);

		double dx = Math.min(
				Math.abs(entityBox.getMaxX() - senderX),
				Math.abs(entityBox.getMinX() - senderX)
		);
		if (dx > interactDistance) {
			return;
		}

		double dz = Math.min(
				Math.abs(entityBox.getMaxZ() - senderZ),
				Math.abs(entityBox.getMinZ() - senderZ)
		);
		if (dz > interactDistance) {
			return;
		}

		double dy = Math.min(
				Math.abs(entityBox.getMaxY() - senderY),
				Math.abs(entityBox.getMinY() - senderY)
		);
		if (dy > interactDistance) {
			return;
		}

		if (PatPatConfig.getInstance().isApi()) {
			PatPacketReceiveEvent patPacketReceiveEvent = new PatPacketReceiveEvent(senderPlayer, livingEntity);
			Bukkit.getServer().getPluginManager().callEvent(patPacketReceiveEvent);

			if (patPacketReceiveEvent.isCancelled()) {
				return;
			}
		}

		showPatPacket(livingEntity, senderPlayer, false);
	}

	public static void showPatPacket(LivingEntity pattedEntity, @Nullable Player whoPatted, boolean commandInitial) {
		PatPatPlugin plugin = PatPatPlugin.getInstance();
		UUID senderUuid = whoPatted != null ? whoPatted.getUniqueId() : UuidUtils.ZERO;
		List<IPatPlayer> nearbyPlayers = new ArrayList<>();
		for (Entity entity : pattedEntity.getNearbyEntities(PAT_VISIBILITY_RADIUS, PAT_VISIBILITY_RADIUS, PAT_VISIBILITY_RADIUS)) {
			if (!(entity instanceof Player player) || (entity.getUniqueId().equals(senderUuid) && !commandInitial)) {
				continue;
			}
			nearbyPlayers.add(PatPlayerFactory.of(player));
		}

		if (pattedEntity instanceof Player player) {
			nearbyPlayers.add(PatPlayerFactory.of(player));
		}

		Map<String, PatPacket> packets = new HashMap<>();
		nearbyPlayers.forEach(player -> {
			IPatPacket packetHandler = player.getPatPacketHandler();
			if (packetHandler == null) {
				return;
			}
			PatPacket packet = packets.computeIfAbsent(
					packetHandler.getPacketHandlerId(),
					s -> packetHandler.getPacket(pattedEntity, whoPatted)
			);
			PatLogger.debug("Sending out pat packet to %s with id %s and data %s", player.getName(), packet.channel(), Arrays.toString(packet.bytes()));
			player.sendPluginMessage(plugin, packet.channel(), packet.bytes());
		});
	}

	@Nullable
	public static IPatPacket getPacketHandler(IPatPlayer player) {
		for (IPatPacket packetHandler : PAT_PACKET_HANDLERS) {
			if (packetHandler.canHandle(player)) {
				return packetHandler;
			}
		}
		return null;
	}

	private boolean canHandle(Player sender) {
		return checkPermission(sender)
				&& checkRateLimit(sender)
				&& checkListMode(sender.getUniqueId());
	}

	private boolean checkPermission(Player sender) {
		PermissionConfig permissionRestriction = PatPatConfig.getInstance().getPermissionRestrictions();
		return !permissionRestriction.isEnabled() || sender.hasPermission(permissionRestriction.getPermissionForPat());
	}

	private boolean checkRateLimit(Player sender) {
		RateLimitConfig ratelimitConfig = PatPatConfig.getInstance().getRateLimit();
		return !ratelimitConfig.isEnabled() || sender.hasPermission(ratelimitConfig.getPermissionBypass()) || RateLimitManager.canPat(sender.getUniqueId());
	}

	private boolean checkListMode(UUID senderUuid) {
		return switch (PatPatConfig.getInstance().getListMode()) {
			case DISABLED -> true;
			case WHITELIST -> PlayerListConfig.getInstance().containsUuid(senderUuid);
			case BLACKLIST -> !PlayerListConfig.getInstance().containsUuid(senderUuid);
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
