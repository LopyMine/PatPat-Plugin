package net.lopymine.patpat.plugin.api.event;

import lombok.*;
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
	/**
	 * Returns the player who performed the patting action
	 *
	 * @return the player who patted, or null if action was performed by non-player source
	 */
	private final @Nullable Player whoPatted;

	/**
	 * Returns the entity that was patted
	 *
	 * @return the living entity that received the patting action
	 */
	private final LivingEntity pattedEntity;

	@Getter(AccessLevel.NONE)
	private boolean cancel;

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	@SuppressWarnings({"unusal", "java:S4144"}) // Paper? use this method
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
