package net.lopymine.patpat.plugin.packet.listener;

import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.*;

import net.lopymine.patpat.plugin.*;
import net.lopymine.patpat.plugin.entity.*;
import net.lopymine.patpat.plugin.packet.handler.*;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class PatPatPacketListener implements PluginMessageListener {

	private final Map<String, IPacketHandler> handlers = new HashMap<>();

	@Override
	public void onPluginMessageReceived(@NotNull String s, @NotNull Player sender, byte[] bytes) {
		if(!isPatPatPacket(s)){
			return;
		}
		IPatPlayer patPlayer = PatPlayerFactory.of(sender);
		IPacketHandler packetHandler = this.handlers.get(s);
		PatLogger.debug("Received packet with id %s from %s with data %s".formatted(s, sender.getName(), Arrays.toString(bytes)));
		if (packetHandler == null) {
			return;
		}
		PatLogger.debug("Handling packet with " + packetHandler.getClass() + " from packet id " + packetHandler.getIncomingPacketId());
		packetHandler.handle(patPlayer, ByteStreams.newDataInput(bytes));
	}

	public void registerPacket(IPacketHandler handler, Messenger messenger) {
		this.handlers.put(handler.getIncomingPacketId(), handler);

		messenger.registerIncomingPluginChannel(PatPatPlugin.getInstance(), handler.getIncomingPacketId(), this);
		messenger.registerOutgoingPluginChannel(PatPatPlugin.getInstance(), handler.getOutgoingPacketId());
	}

	public boolean isPatPatPacket(String channel){
		return channel.startsWith(PatPatPlugin.MOD_ID);
	}
}
