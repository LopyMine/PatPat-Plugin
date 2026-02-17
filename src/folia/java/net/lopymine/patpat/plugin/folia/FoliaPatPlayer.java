package net.lopymine.patpat.plugin.folia;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.entity.IPatPlayer;
import net.lopymine.patpat.plugin.packet.IPatPacket;
import net.lopymine.patpat.plugin.packet.PatPacketV1;
import net.lopymine.patpat.plugin.packet.handler.PatPacketHandler;

import java.util.*;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused") // Reflection call
@Getter
public class FoliaPatPlayer implements IPatPlayer {

	private static final Map<UUID, FoliaPatPlayer> PAT_PLAYERS = new HashMap<>();

	private final UUID uuid;
	private Version version = PatPacketV1.PAT_PACKET_V1_VERSION;
	@Nullable
	private IPatPacket patPacketHandler;

	public FoliaPatPlayer(UUID uuid) {
		this(uuid, PatPacketV1.PAT_PACKET_V1_VERSION);
	}

	public FoliaPatPlayer(UUID uuid, Version version) {
		this.uuid = uuid;
		this.setVersion(version);
	}


	public static FoliaPatPlayer of(@NotNull Player player) {
		return PAT_PLAYERS.computeIfAbsent(player.getUniqueId(), FoliaPatPlayer::new);
	}

	@CanIgnoreReturnValue
	public static FoliaPatPlayer register(@NotNull Player player) {
		FoliaPatPlayer patPlayer = new FoliaPatPlayer(player.getUniqueId(), PatPacketV1.PAT_PACKET_V1_VERSION);
		PAT_PLAYERS.put(player.getUniqueId(), patPlayer);
		return patPlayer;
	}

	public static void unregister(@NotNull Player player) {
		unregister(player.getUniqueId());
	}

	public static void unregister(@NotNull UUID uuid) {
		PAT_PLAYERS.remove(uuid);
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
		return runOnPlayer(Player::getName);
	}

	@Override
	public Location getLocation() {
		return runOnPlayer(Player::getLocation);
	}

	@Override
	public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message) {
		runOnPlayer(player -> {
			player.sendPluginMessage(source, channel, message);
			return true;
		});
	}

	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(this.uuid);
	}

	@Override
	public UUID getUniqueId() {
		return runOnPlayer(Player::getUniqueId);
	}

	@Override
	public World getWorld() {
		return runOnPlayer(Player::getWorld);
	}

	private <T> T runOnPlayer(Function<Player, T> consumer) {
		Player player = Bukkit.getPlayer(this.uuid);
		if (player != null) {
			return consumer.apply(player);
		}
		return null;
	}
}
