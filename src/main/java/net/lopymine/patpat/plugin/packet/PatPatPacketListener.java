package net.lopymine.patpat.plugin.packet;

import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import net.lopymine.patpat.plugin.packet.handler.PacketHandler;

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
		packetHandler.handle(player, ByteStreams.newDataInput(bytes));
	}

	public void registerPacket(PacketHandler handler) {
		this.handlers.put(handler.getPacketID(), handler);
	}
}
