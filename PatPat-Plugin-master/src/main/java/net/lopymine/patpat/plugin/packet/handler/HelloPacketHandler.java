package net.lopymine.patpat.plugin.packet.handler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.entity.PatPlayer;
import net.lopymine.patpat.plugin.extension.ByteArrayDataExtension;
import net.lopymine.patpat.plugin.packet.PatPacketV2;
import net.lopymine.patpat.plugin.util.StringUtils;

@ExtensionMethod(ByteArrayDataExtension.class)
public class HelloPacketHandler implements IPacketHandler {

    public static final String HELLO_PATPAT_SERVER_C2S_PACKET =
            StringUtils.modId("hello_patpat_server_c2s_packet");

    public static final String HELLO_PATPAT_PLAYER_S2C_PACKET =
            StringUtils.modId("hello_patpat_player_s2c_packet");

    @Override
    public void handle(PatPlayer sender, ByteArrayDataInput buf) {
        PatLogger.debug("Received hello packet from %s", sender.getName());
        this.readVersion(sender, buf);
    }

    private void readVersion(PatPlayer sender, ByteArrayDataInput buf) {
        Version version = Version.INVALID;

        try {
            int major = buf.readUnsignedByte();
            int minor = buf.readUnsignedByte();
            int patch = buf.readUnsignedByte();
            version = new Version(major, minor, patch);
        } catch (Exception e) {
            PatLogger.error("Failed to parse packet version from hello packet!", e);
        }

        if (version.isInvalid()) {
            // Since v2 packet version we started sending hello packets
            version = PatPacketV2.PAT_PACKET_V2_VERSION;
        }

        sender.setVersion(version);
        PatLogger.debug("Player PatPat version: %s", sender.getVersion());
    }

    public static void sendHelloPacket(PatPlayer patPlayer) {
        sendHelloPacket(patPlayer.getPlayer());
    }

    /**
     * Folia-safe: выполняем отправку через EntityScheduler игрока.
     */
    public static void sendHelloPacket(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        player.getScheduler().run(
                PatPatPlugin.getInstance(),
                (task) -> {
                    try {
                        Version serverVersion = Version.CURRENT_PLUGIN_VERSION;
                        ByteArrayDataOutput output = ByteStreams.newDataOutput();
                        output.writeByte(serverVersion.major());
                        output.writeByte(serverVersion.minor());
                        output.writeByte(serverVersion.patch());

                        player.sendPluginMessage(
                                PatPatPlugin.getInstance(),
                                HELLO_PATPAT_PLAYER_S2C_PACKET,
                                output.toByteArray()
                        );
                    } catch (Exception e) {
                        PatLogger.error("Failed to send server hello packet version to %s:"
                                .formatted(player.getName()), e);
                    }
                },
                () -> {
                    // retired callback: игрок мог выйти до выполнения
                }
        );
    }

    @Override
    public String getIncomingPacketId() {
        return HELLO_PATPAT_SERVER_C2S_PACKET;
    }

    @Override
    public String getOutgoingPacketId() {
        return HELLO_PATPAT_PLAYER_S2C_PACKET;
    }
}
