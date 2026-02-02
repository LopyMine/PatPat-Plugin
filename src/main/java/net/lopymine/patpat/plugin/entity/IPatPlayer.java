package net.lopymine.patpat.plugin.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.packet.IPatPacket;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface IPatPlayer {

	void setVersion(Version version);

	Version getVersion();

	IPatPacket getPatPacketHandler();

	Player getPlayer();

	Location getLocation();

	String getName();

	void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message);

	UUID getUniqueId();

	World getWorld();

}
