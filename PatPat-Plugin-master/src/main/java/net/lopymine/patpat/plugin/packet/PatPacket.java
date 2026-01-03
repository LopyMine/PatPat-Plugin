package net.lopymine.patpat.plugin.packet;

import java.util.Arrays;
import java.util.Objects;

public record PatPacket(byte[] bytes, String channel) {

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PatPacket packet = (PatPacket) o;
		return Objects.equals(this.channel, packet.channel) && Arrays.equals(this.bytes, packet.bytes);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(channel);
		result = 31 * result + Arrays.hashCode(bytes);
		return result;
	}

	@Override
	public String toString() {
		return "PatPacket{" +
				"bytes=" + Arrays.toString(bytes) +
				", channel=" + channel +
				'}';
	}

}
