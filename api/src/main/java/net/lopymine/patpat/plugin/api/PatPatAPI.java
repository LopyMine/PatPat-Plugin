package net.lopymine.patpat.plugin.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

public interface PatPatAPI {

	/**
	 * Performs a patting action on the specified entity.
	 *
	 * @param pattedEntity the entity that is being patted
	 * @param whoPatted the player who patted the entity, or {@code null} if
	 *                  the pat was performed by plugin
	 */
	void patEntity(LivingEntity pattedEntity, @Nullable Player whoPatted);

}