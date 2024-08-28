package net.lopymine.patpat.plugin.patPat.packet.handler;

import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;

public interface PacketHandler {

	void handle(Player sender, ByteBuf buf);

	String getPacketID();

}
