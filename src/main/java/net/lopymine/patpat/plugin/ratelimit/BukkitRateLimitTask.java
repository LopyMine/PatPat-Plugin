package net.lopymine.patpat.plugin.ratelimit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitRateLimitTask implements IRateLimitTask {

	private static BukkitTask task;

	@Override
	public void stop() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	@Override
	public void start(JavaPlugin plugin, long periodInTicks, Runnable runnable) {
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(
				plugin,
				runnable,
				0,
				periodInTicks
		);
	}
}
