package net.lopymine.patpat.plugin.event;

import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.entity.PatPlayer;
import net.lopymine.patpat.plugin.extension.PlayerExtension;
import net.lopymine.patpat.plugin.packet.handler.HelloPacketHandler;

@ExtensionMethod(PlayerExtension.class)
public class PatPatPlayerEventHandler implements Listener {

    public static void register() {
        PatPatPlugin plugin = PatPatPlugin.getInstance();
        plugin.getServer().getPluginManager().registerEvents(new PatPatPlayerEventHandler(), plugin);
    }

    @EventHandler
    public void channelRegister(PlayerRegisterChannelEvent event) {
        String channel = event.getChannel();
        if (!HelloPacketHandler.HELLO_PATPAT_PLAYER_S2C_PACKET.equals(channel)) {
            return;
        }

        Player player = event.getPlayer();
        PatLogger.debug("Channel %s is registered, sending hello packet", channel);

        player.getScheduler().run(
                PatPatPlugin.getInstance(),
                st -> HelloPacketHandler.sendHelloPacket(player),
                () -> {}
        );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getScheduler().run(
                PatPatPlugin.getInstance(),
                st -> PatPlayer.register(player),
                () -> {}
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // На quit scheduler может быть уже retired, поэтому unregister лучше вызвать напрямую,
        // но если unregister трогает Bukkit API, его надо делать аккуратно.
        player.getScheduler().run(
                PatPatPlugin.getInstance(),
                st -> PatPlayer.unregister(player),
                () -> PatPlayer.unregister(player)
        );
    }
}
