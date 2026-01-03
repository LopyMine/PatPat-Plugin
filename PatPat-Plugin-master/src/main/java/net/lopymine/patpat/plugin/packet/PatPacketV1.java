package net.lopymine.patpat.plugin.packet;

import com.google.common.io.*;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.config.Version;
import net.lopymine.patpat.plugin.entity.PatPlayer;
import net.lopymine.patpat.plugin.extension.ByteArrayDataExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import org.jetbrains.annotations.Nullable;

@ExtensionMethod(ByteArrayDataExtension.class)
public class PatPacketV1 implements IPatPacket {

	public static final Version PAT_PACKET_V1_VERSION = new Version(1, 0, 0);
	private static final String PACKET_ID = StringUtils.modId("pat_entity_s2c_packet");

	@Override
	public boolean canHandle(PatPlayer player) {
		return player.getVersion().isGreaterOrEqualThan(PAT_PACKET_V1_VERSION);
	}

	@Override
	public @Nullable Entity getPattedEntity(PatPlayer player, ByteArrayDataInput buf) {
		try {
			return Bukkit.getServer().getEntity(buf.readUuid());
		} catch (IllegalStateException e) {
			PatLogger.debug("Failed to read patted entity from packet", e);
			return null;
		}
	}

	@Override
	public PatPacket getPacket(Entity pattedEntity, Entity whoPattedEntity) {
		ByteArrayDataOutput buf = ByteStreams.newDataOutput();
		buf.writeUuid(pattedEntity.getUniqueId());
		buf.writeUuid(whoPattedEntity.getUniqueId());
		return new PatPacket(buf.toByteArray(), PACKET_ID);
	}

	@Override
	public String getPacketHandlerId() {
		return "PatPacketV1";
	}

}
