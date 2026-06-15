package net.lopymine.patpat.plugin.packet;

import com.google.common.io.*;
import lombok.experimental.ExtensionMethod;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.entity.IPatPlayer;
import net.lopymine.patpat.plugin.extension.ByteArrayDataExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import org.jetbrains.annotations.Nullable;

@ExtensionMethod(ByteArrayDataExtension.class)
public class PatPacketV2 implements IPatPacket {

	public static final Version PAT_PACKET_V2_VERSION = new Version(1, 2, 0);
	private static final String PACKET_ID = StringUtils.modId("pat_entity_s2c_packet_v2");

	private static final double INTERACTION_RANGE_BUFFER = 5.0;

	@Override
	public boolean canHandle(IPatPlayer player) {
		return player.getVersion().isGreaterOrEqualThan(PAT_PACKET_V2_VERSION);
	}

	@Override
	public @Nullable Entity getPattedEntity(IPatPlayer sender, ByteArrayDataInput buf) {
		try {
			int entityId = buf.readVarInt();
			Player player = sender.getPlayer();
			
			if (player.getEntityId() == entityId) {
				return player;
			}
			
			double radius = 4.5 + INTERACTION_RANGE_BUFFER; // default radius
			var attr = player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE);
			if (attr != null) {
				radius = attr.getValue() + INTERACTION_RANGE_BUFFER;
			}
			
			for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
				if (e.getEntityId() == entityId) {
					return e;
				}
			}
		} catch (IllegalArgumentException e) {
			PatLogger.warn("Failed to parse entityId from incoming packet from player %s[%s]! Ignoring packet.".formatted(sender.getName(), sender.getUniqueId()), e);
		}
		return null;
	}

	@Override
	public PatPacket getPacket(Entity pattedEntity, @Nullable Entity whoPattedEntity) {
		ByteArrayDataOutput buf = ByteStreams.newDataOutput();
		buf.writeVarInt(pattedEntity.getEntityId());
		buf.writeVarInt(whoPattedEntity != null ? whoPattedEntity.getEntityId() : Integer.MIN_VALUE);
		return new PatPacket(buf.toByteArray(), PACKET_ID);
	}

	@Override
	public String getPacketHandlerId() {
		return "PatPacketV2";
	}
}
