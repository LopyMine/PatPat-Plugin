package net.lopymine.patpat.plugin.patPat.extension;

import io.netty.buffer.ByteBuf;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class ByteBufExtension {

	public static UUID readUuid(@NotNull ByteBuf buf){
		return new UUID(buf.readLong(), buf.readLong());
	}

	public static void writeUuid(@NotNull ByteBuf buf, UUID uuid){
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}
}
