package net.lopymine.patpat.plugin.ratelimit;

import org.bukkit.plugin.java.JavaPlugin;

public interface IRateLimitTask {

	void stop();

	void start(JavaPlugin plugin, long periodInTicks, Runnable runnable);

}
