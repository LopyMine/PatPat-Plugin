package net.lopymine.patpat.plugin.extension;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class ByteArrayDataExtension {

	private ByteArrayDataExtension() {
		throw new IllegalStateException("Extension class");
	}

	public static <T extends ByteArrayDataInput> UUID readUuid(@NotNull T in) {
		return new UUID(in.readLong(), in.readLong());
	}

	public static <T extends ByteArrayDataOutput> void writeUuid(T out, UUID uuid) {
		out.writeLong(uuid.getMostSignificantBits());
		out.writeLong(uuid.getLeastSignificantBits());
	}

	public static <T extends ByteArrayDataInput> int readVarInt(T buf) throws IllegalArgumentException {
		int i = 0;
		int j = 0;

		byte b;
		do {
			b = buf.readByte();
			i |= (b & 127) << j++ * 7;
			if (j > 5) {
				throw new IllegalArgumentException("VarInt too big");
			}
		} while((b & 128) == 128);

		return i;
	}

	public static <T extends ByteArrayDataOutput> void writeVarInt(T output, int value) {
		while((value & -128) != 0) {
			output.writeByte(value & 127 | 128);
			value >>>= 7;
		}

		output.writeByte(value);
	}

}
