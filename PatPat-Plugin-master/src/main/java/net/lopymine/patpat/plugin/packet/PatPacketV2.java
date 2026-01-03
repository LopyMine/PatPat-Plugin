package net.lopymine.patpat.plugin.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.entity.PatPlayer;
import net.lopymine.patpat.plugin.extension.ByteArrayDataExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import org.jetbrains.annotations.Nullable;

@ExtensionMethod(ByteArrayDataExtension.class)
public class PatPacketV2 implements IPatPacket {

    public static final Version PAT_PACKET_V2_VERSION = new Version(1, 2, 0);
    private static final String PACKET_ID = StringUtils.modId("pat_entity_s2c_packet_v2");

    // Должно быть >= max reach + небольшой запас
    private static final double LOOKUP_RADIUS = 8.0;

    @Override
    public boolean canHandle(PatPlayer player) {
        return player.getVersion().isGreaterOrEqualThan(PAT_PACKET_V2_VERSION);
    }

    @Override
    public @Nullable Entity getPattedEntity(PatPlayer sender, ByteArrayDataInput buf) {
        try {
            final int entityId = buf.readVarInt();
            final Player player = sender.getPlayer();

            // Сначала быстрый случай: вдруг пакет прислал самого себя
            if (player.getEntityId() == entityId) {
                return player;
            }

            // Folia-safe: nearbyEntities в контексте entity/региона sender'а
            for (Entity e : player.getNearbyEntities(LOOKUP_RADIUS, LOOKUP_RADIUS, LOOKUP_RADIUS)) {
                if (e.getEntityId() == entityId) {
                    return e;
                }
            }

            return null;
        } catch (Exception e) {
            PatLogger.warn(
                    "Failed to parse entityId from incoming packet from player %s[%s]! Ignoring packet."
                            .formatted(sender.getName(), sender.getUniqueId()),
                    e
            );
            return null;
        }
    }

    @Override
    public PatPacket getPacket(Entity pattedEntity, Entity whoPattedEntity) {
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();
        buf.writeVarInt(pattedEntity.getEntityId());
        buf.writeVarInt(whoPattedEntity.getEntityId());
        return new PatPacket(buf.toByteArray(), PACKET_ID);
    }

    @Override
    public String getPacketHandlerId() {
        return "PatPacketV2";
    }
}
