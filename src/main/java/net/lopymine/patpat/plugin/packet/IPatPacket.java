package net.lopymine.patpat.plugin.packet;

import com.google.common.io.ByteArrayDataInput;
import org.bukkit.entity.Entity;

import net.lopymine.patpat.plugin.entity.IPatPlayer;

import org.jetbrains.annotations.Nullable;

public interface IPatPacket {

	boolean canHandle(IPatPlayer player);

	@Nullable
	Entity getPattedEntity(IPatPlayer player, ByteArrayDataInput buf);

	PatPacket getPacket(Entity pattedEntity, @Nullable Entity whoPattedEntity);

	String getPacketHandlerId();

}
