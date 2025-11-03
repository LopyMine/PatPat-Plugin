package net.lopymine.patpat.plugin;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.api.PatPatAPI;
import net.lopymine.patpat.plugin.packet.handler.PatPacketHandler;

import org.jetbrains.annotations.Nullable;

public final class PatPatAPIImpl implements PatPatAPI {

	@Override
	public void patEntity(LivingEntity pattedEntity, @Nullable Player whoPatted) {
		PatPacketHandler.showPatPacket(pattedEntity, whoPatted, false);
	}

}
