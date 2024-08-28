package net.lopymine.patpat.plugin.patPat.packet.handler;

import com.google.common.io.*;
import io.netty.buffer.ByteBuf;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.*;
import net.lopymine.patpat.plugin.patPat.PatPatPlugin;
import net.lopymine.patpat.plugin.patPat.config.options.ListMode;
import net.lopymine.patpat.plugin.patPat.extension.*;
import net.lopymine.patpat.plugin.patPat.packet.PatPatPacketManager;
import java.util.*;
import java.util.stream.Stream;

@ExtensionMethod({ByteBufExtension.class, ByteArrayDataExtension.class})
public class PatPacketHandler implements PacketHandler {

	@Override
	public void handle(Player sender, ByteBuf buf) {
		PatPatPlugin plugin = PatPatPlugin.getInstance();
		if (!this.canHandle(sender, plugin)) {
			return;
		}

		UUID pattedEntityUuid = buf.readUuid();
		Entity pattedEntity = plugin.getServer().getEntity(pattedEntityUuid);
		if (!(pattedEntity instanceof LivingEntity livingEntity)) {
			return;
		}

		if (livingEntity.isInvisible()) {
			return;
		}

		double patVisibilityRadius = plugin.getPatPatConfig().getPatVisibilityRadius();

		List<Player> nearbyPlayers = pattedEntity
				.getNearbyEntities(patVisibilityRadius, patVisibilityRadius, patVisibilityRadius)
				.stream()
				.flatMap((entity) -> {
					if (entity instanceof Player player) {
						return Stream.of(player);
					}
					return Stream.empty();
				}).toList();

		for (Player player : nearbyPlayers) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				continue;
			}

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUuid(pattedEntityUuid);
			out.writeUuid(sender.getUniqueId());

			player.sendPluginMessage(plugin, PatPatPacketManager.PATPAT_S2C_PACKET_ID, out.toByteArray());
		}
	}

	private boolean canHandle(Player sender, PatPatPlugin plugin) {
		Set<UUID> uuids = plugin.getPlayerListConfig().getUuids();
		ListMode listMode = plugin.getPatPatConfig().getListMode();

		return switch (listMode) {
			case DISABLED -> true;
			case WHITELIST -> uuids.contains(sender.getUniqueId());
			case BLACKLIST -> !uuids.contains(sender.getUniqueId());
			case null -> false;
		};
	}

	@Override
	public String getPacketID() {
		return PatPatPacketManager.PATPAT_C2S_PACKET_ID;
	}
}
