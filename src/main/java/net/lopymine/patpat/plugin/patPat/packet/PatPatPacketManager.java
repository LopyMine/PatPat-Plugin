package net.lopymine.patpat.plugin.patPat.packet;

import org.bukkit.plugin.messaging.Messenger;

import net.lopymine.patpat.plugin.patPat.PatPatPlugin;
import net.lopymine.patpat.plugin.patPat.packet.handler.PatPacketHandler;

public class PatPatPacketManager {

	public static final String PATPAT_C2S_PACKET_ID = PatPatPlugin.id("pat_entity_c2s_packet");
	public static final String PATPAT_S2C_PACKET_ID = PatPatPlugin.id("pat_entity_s2c_packet");

	public static void register() {
		// Create Main Listener for PatPat Packets
		PatPatPacketListener listener = new PatPatPacketListener();

		// Listening PatPat Packets Here
		listener.registerPacket(new PatPacketHandler());

		// Registering Main Listener
		PatPatPlugin plugin = PatPatPlugin.getInstance();
		Messenger messenger = plugin.getServer().getMessenger();

		messenger.registerIncomingPluginChannel(plugin, PATPAT_C2S_PACKET_ID, listener);
		messenger.registerOutgoingPluginChannel(plugin, PATPAT_S2C_PACKET_ID); // Need to allow sending s2c messages
	}
}
