package net.lopymine.patpat.plugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.entity.PatPlayerFactory;
import net.lopymine.patpat.plugin.packet.handler.HelloPacketHandler;

public class PatPatPlayerEventHandler implements Listener {

	public static void register() {
		PatPatPlugin plugin = PatPatPlugin.getInstance();
		plugin.getServer().getPluginManager().registerEvents(new PatPatPlayerEventHandler(), plugin);
	}

	@EventHandler
	public void channelRegister(PlayerRegisterChannelEvent event) {
		String channel = event.getChannel();
		if(HelloPacketHandler.HELLO_PATPAT_PLAYER_S2C_PACKET.equals(channel)){
			PatLogger.debug("Channel %s is registered, sending hello packet", channel);
			HelloPacketHandler.sendHelloPacket(event.getPlayer());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PatPlayerFactory.register(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PatPlayerFactory.unregister(player);
	}

}
