package net.lopymine.patpat.plugin.entity;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.packet.*;
import net.lopymine.patpat.plugin.packet.handler.PatPacketHandler;

import java.util.*;
import org.jetbrains.annotations.*;
@Getter
public class PatPlayer implements IPatPlayer {

	private static final Map<Player, PatPlayer> PAT_PLAYERS = new HashMap<>();

	private final Player player;
	private Version version = PatPacketV1.PAT_PACKET_V1_VERSION;
	@Nullable
	private IPatPacket patPacketHandler;

	public PatPlayer(Player player) {
		this(player, PatPacketV1.PAT_PACKET_V1_VERSION);
	}

	public PatPlayer(Player player, Version version) {
		this.player = player;
		this.setVersion(version);
	}


	public static PatPlayer of(@NotNull Player player) {
		return PAT_PLAYERS.computeIfAbsent(player, PatPlayer::new);
	}

	@CanIgnoreReturnValue
	public static PatPlayer register(@NotNull Player player) {
		PatPlayer patPlayer = new PatPlayer(player, PatPacketV1.PAT_PACKET_V1_VERSION);
		PAT_PLAYERS.put(player, patPlayer);
		return patPlayer;
	}

	public static void unregister(@NotNull Player player) {
		PAT_PLAYERS.remove(player);
	}

	@Override
	public void setVersion(Version version) {
		this.version = version;
		updatePatPacketHandler();
	}

	private void updatePatPacketHandler() {
		this.patPacketHandler = PatPacketHandler.getPacketHandler(this);
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message) {
		player.sendPluginMessage(source, channel, message);
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public World getWorld() {
		return player.getWorld();
	}
}
