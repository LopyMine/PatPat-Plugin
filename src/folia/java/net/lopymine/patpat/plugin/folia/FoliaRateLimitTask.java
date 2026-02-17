package net.lopymine.patpat.plugin.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.java.JavaPlugin;

import net.lopymine.patpat.plugin.ratelimit.IRateLimitTask;

@SuppressWarnings("unused") // Reflection call
public class FoliaRateLimitTask implements IRateLimitTask {

	private static ScheduledTask task;

	@Override
	public void stop() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	@Override
	public void start(JavaPlugin plugin, long periodInTicks, Runnable runnable) {
		task = plugin.getServer()
				.getGlobalRegionScheduler()
				.runAtFixedRate(
						plugin,
						scheduledTask -> runnable.run(),
						1L,
						periodInTicks
				);
	}

}