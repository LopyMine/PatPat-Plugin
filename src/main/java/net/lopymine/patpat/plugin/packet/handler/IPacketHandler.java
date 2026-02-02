package net.lopymine.patpat.plugin.packet.handler;

import com.google.common.io.ByteArrayDataInput;

import net.lopymine.patpat.plugin.entity.IPatPlayer;

public interface IPacketHandler {

	void handle(IPatPlayer sender, ByteArrayDataInput buf);

	String getIncomingPacketId();

	String getOutgoingPacketId();

}
