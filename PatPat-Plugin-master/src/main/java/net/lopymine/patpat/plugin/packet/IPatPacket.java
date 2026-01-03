package net.lopymine.patpat.plugin.packet;

import com.google.common.io.ByteArrayDataInput;
import org.bukkit.entity.Entity;

import net.lopymine.patpat.plugin.entity.PatPlayer;

import org.jetbrains.annotations.Nullable;

public interface IPatPacket {

	boolean canHandle(PatPlayer player);

	@Nullable
	Entity getPattedEntity(PatPlayer player, ByteArrayDataInput buf);

	PatPacket getPacket(Entity pattedEntity, Entity whoPattedEntity);

	String getPacketHandlerId();

}
