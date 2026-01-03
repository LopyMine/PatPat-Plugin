package net.lopymine.patpat.plugin.packet.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.packet.handler.HelloPacketHandler;
import net.lopymine.patpat.plugin.packet.handler.PatPacketHandler;
import net.lopymine.patpat.plugin.packet.handler.PatPacketOldHandler;
import net.lopymine.patpat.plugin.packet.listener.PatPatPacketListener;

public class PatPatPacketManager {

    private PatPatPacketManager() {
        throw new IllegalStateException("Manager class");
    }

    @SuppressWarnings("deprecation")
    public static void register() {
        PatPatPlugin plugin = PatPatPlugin.getInstance();

        // Create Main Listener for PatPat Packets
        PatPatPacketListener listener = new PatPatPacketListener();

        Messenger messenger = plugin.getServer().getMessenger();

        // Register Packet Handlers
        listener.registerPacket(new PatPacketHandler(), messenger);
        listener.registerPacket(new PatPacketOldHandler(), messenger);
        listener.registerPacket(new HelloPacketHandler(), messenger);

        // Send hello packet for online players (/reload confirm) - Folia-safe
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getScheduler().run(plugin, st -> HelloPacketHandler.sendHelloPacket(player), () -> {});
        }
    }
}
