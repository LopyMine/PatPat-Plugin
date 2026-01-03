package net.lopymine.patpat.plugin.packet.handler;

import com.google.common.io.ByteArrayDataInput;
import net.lopymine.patpat.plugin.entity.PatPlayer;

public interface IPacketHandler {

    void handle(PatPlayer sender, ByteArrayDataInput buf);

    String getIncomingPacketId();

    String getOutgoingPacketId();
}
