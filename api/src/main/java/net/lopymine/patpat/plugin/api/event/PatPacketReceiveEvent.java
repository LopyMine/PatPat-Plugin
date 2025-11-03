package net.lopymine.patpat.plugin.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
* This event is fired when the server receive pat packet from player
*/
@Getter
@RequiredArgsConstructor
public class PatPacketReceiveEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final @Nullable Player whoPatted;
	private final LivingEntity pattedEntity;

	private boolean cancel;

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	@SuppressWarnings({"unusal", "java:S4144"})
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
