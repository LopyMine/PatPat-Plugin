package net.lopymine.patpat.plugin.entity;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.extension.PlayerExtension;
import net.lopymine.patpat.plugin.packet.IPatPacket;
import net.lopymine.patpat.plugin.packet.PatPacketV1;
import net.lopymine.patpat.plugin.packet.handler.PatPacketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@ExtensionMethod(PlayerExtension.class)
public class PatPlayer {

    // Folia-safe: thread-safe collection + ключ UUID (не Player)
    private static final Map<UUID, PatPlayer> PAT_PLAYERS = new ConcurrentHashMap<>();

    private final UUID uniqueId;
    private final Player player;

    private volatile Version version = PatPacketV1.PAT_PACKET_V1_VERSION;

    @Nullable
    private volatile IPatPacket patPacketHandler;

    private PatPlayer(@NotNull Player player, @NotNull Version version) {
        this.player = player;
        this.uniqueId = player.getUniqueId();
        this.setVersion(version);
    }

    public static PatPlayer of(@NotNull Player player) {
        return PAT_PLAYERS.computeIfAbsent(player.getUniqueId(), uuid -> new PatPlayer(player, PatPacketV1.PAT_PACKET_V1_VERSION));
    }

    @CanIgnoreReturnValue
    public static PatPlayer register(@NotNull Player player) {
        PatPlayer patPlayer = new PatPlayer(player, PatPacketV1.PAT_PACKET_V1_VERSION);
        PAT_PLAYERS.put(player.getUniqueId(), patPlayer);
        return patPlayer;
    }

    public static void unregister(@NotNull Player player) {
        unregister(player.getUniqueId());
    }

    public static void unregister(@NotNull UUID uuid) {
        PAT_PLAYERS.remove(uuid);
    }

    public void setVersion(@NotNull Version version) {
        this.version = version;
        updatePatPacketHandler();
    }

    private void updatePatPacketHandler() {
        this.patPacketHandler = PatPacketHandler.getPacketHandler(this);
    }

    public String getName() {
        return player.getName();
    }

    public World getWorld() {
        return player.getWorld();
    }

    /**
     * Folia-safe: отправка сообщения игроку через EntityScheduler.
     */
    public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message) {
        player.getScheduler().run(
                PatPatPlugin.getInstance(),
                st -> player.sendPluginMessage(source, channel, message),
                () -> {}
        );
    }

    public void sendPatPatMessage(String message, Object... args) {
        player.getScheduler().run(
                PatPatPlugin.getInstance(),
                st -> this.player.sendPatPatMessage(message, args),
                () -> {}
        );
    }

    public void sendPatPatMessage(ComponentLike message) {
        player.getScheduler().run(
                PatPatPlugin.getInstance(),
                st -> this.player.sendPatPatMessage(message),
                () -> {}
        );
    }
}
