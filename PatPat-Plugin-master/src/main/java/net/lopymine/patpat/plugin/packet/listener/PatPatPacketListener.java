package net.lopymine.patpat.plugin.packet.listener;

import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.entity.PatPlayer;
import net.lopymine.patpat.plugin.packet.handler.IPacketHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PatPatPacketListener implements PluginMessageListener {

    private final Map<String, IPacketHandler> handlers = new ConcurrentHashMap<>();

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player sender, byte[] bytes) {
        if (!isPatPatPacket(channel)) {
            return;
        }

        final IPacketHandler packetHandler = this.handlers.get(channel);
        if (packetHandler == null) {
            return;
        }

        // Логи можно оставить тут (они не трогают Bukkit world data)
        PatLogger.debug("Received packet with id %s from %s with data %s"
                .formatted(channel, sender.getName(), Arrays.toString(bytes)));

        // ВАЖНО: переносим обработку в EntityScheduler игрока (Folia-safe для entity операций)
        sender.getScheduler().run(
                PatPatPlugin.getInstance(),
                (task) -> {
                    PatPlayer patPlayer = PatPlayer.of(sender);
                    PatLogger.debug("Handling packet with " + packetHandler.getClass()
                            + " from packet id " + packetHandler.getIncomingPacketId());
                    packetHandler.handle(patPlayer, ByteStreams.newDataInput(bytes));
                },
                () -> {
                    // retired callback: игрок мог выйти/быть удалён до выполнения
                }
        );
    }

    public void registerPacket(@NotNull IPacketHandler handler, @NotNull Messenger messenger) {
        this.handlers.put(handler.getIncomingPacketId(), handler);

        messenger.registerIncomingPluginChannel(PatPatPlugin.getInstance(), handler.getIncomingPacketId(), this);
        messenger.registerOutgoingPluginChannel(PatPatPlugin.getInstance(), handler.getOutgoingPacketId());
    }

    public boolean isPatPatPacket(@NotNull String channel) {
        return channel.startsWith(PatPatPlugin.MOD_ID);
    }
}
