package net.lopymine.patpat.plugin.extension;

import com.google.common.io.*;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class ByteArrayDataExtension {

	private ByteArrayDataExtension() {
		throw new IllegalStateException("Extension class");
	}

	public static UUID readUuid(@NotNull ByteArrayDataInput in) {
		return new UUID(in.readLong(), in.readLong());
	}

	public static <T extends ByteArrayDataOutput> void writeUuid(T out, UUID uuid) {
		out.writeLong(uuid.getMostSignificantBits());
		out.writeLong(uuid.getLeastSignificantBits());
	}
}
