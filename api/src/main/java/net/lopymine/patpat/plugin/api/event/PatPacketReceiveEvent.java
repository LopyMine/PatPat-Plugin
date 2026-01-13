package net.lopymine.patpat.plugin.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
* This event is fired when the server receive pat packet from player
*/
public class PatPacketReceiveEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final @Nullable Player whoPatted;
	private final LivingEntity pattedEntity;

	/**
	 * Event is fired when the server receive pat packet from player
	 *
	 * @param whoPatted is player who patted, or null if action was performed by non-player source
	 * @param pattedEntity is living entity that received the patting action
	 */
	public PatPacketReceiveEvent(@Nullable Player whoPatted, @NotNull LivingEntity pattedEntity){
		this.whoPatted = whoPatted;
		this.pattedEntity = pattedEntity;
	}

	/**
	 * Returns the player who performed the patting action
	 *
	 * @return the player who patted, or null if action was performed by non-player source
	 */
	@Nullable
	public Player getWhoPatted() {
		return whoPatted;
	}

	/**
	 * Returns the entity that was patted
	 *
	 * @return the living entity that received the patting action
	 */
	@NotNull
	public LivingEntity getPattedEntity() {
		return pattedEntity;
	}




	private boolean cancel;

	@Override
	@NotNull
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	/**
	 * Returns the Handler List
	 *
	 * @return the handler list
	 */
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
