package net.lopymine.patpat.plugin.patPat.packet;

import io.netty.buffer.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import net.lopymine.patpat.plugin.patPat.packet.handler.PacketHandler;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class PatPatPacketListener implements PluginMessageListener {

	private final Map<String, PacketHandler> handlers = new HashMap<>();

	@Override
	public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, byte[] bytes) {
		PacketHandler packetHandler = this.handlers.get(s);
		if (packetHandler == null) {
			return;
		}
		packetHandler.handle(player, Unpooled.copiedBuffer(bytes));
	}

	public void registerPacket(PacketHandler handler) {
		this.handlers.put(handler.getPacketID(), handler);
	}
}
