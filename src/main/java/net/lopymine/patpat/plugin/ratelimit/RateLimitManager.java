package net.lopymine.patpat.plugin.ratelimit;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.RateLimitConfig;
import net.lopymine.patpat.plugin.util.FoliaUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RateLimitManager {

	private RateLimitManager() {
		throw new IllegalStateException("Manager class");
	}

	private static final AtomicReference<IRateLimitTask> taskRef = new AtomicReference<>();

	private static final Map<UUID, Integer> uuidToPat = new ConcurrentHashMap<>();

	public static synchronized void init() {
		if (taskRef.get() != null) {
			return;
		}
		if (FoliaUtils.IS_FOLIA) {
			try {
				taskRef.set((IRateLimitTask) Class.forName("net.lopymine.patpat.plugin.folia.FoliaRateLimitTask").getConstructor().newInstance());
			} catch (InstantiationException e) {
				PatLogger.error("InstantiationException", e);
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				PatLogger.error("Illegal access exception", e);
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				PatLogger.error("InvocationTargetException", e);
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				PatLogger.error("Method not found!", e);
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				PatLogger.error("Class not found!", e);
				throw new RuntimeException(e);
			}
			return;
		}
		taskRef.set(new BukkitRateLimitTask());
		reloadTask();
	}

	public static int getAvailablePats(UUID uuid) {
		return uuidToPat.getOrDefault(uuid, PatPatConfig.getInstance().getRateLimit().getTokenLimit());
	}

	public static boolean canPat(UUID uuid) {
		RateLimitConfig config = PatPatConfig.getInstance().getRateLimit();
		if (!config.isEnabled()) {
			return true;
		}

		boolean[] allowed = new boolean[1];
		uuidToPat.compute(uuid, (k, current) -> {
			if (current == null) {
				current = config.getTokenLimit();
			}
			if (current > 0) {
				allowed[0] = true;
				return current - 1;
			}
			return current;
		});
		return allowed[0];
	}

	public static void addPats(int token) {
		int tokenLimit = PatPatConfig.getInstance().getRateLimit().getTokenLimit();
		for (Map.Entry<UUID, Integer> entry : uuidToPat.entrySet()) {
			uuidToPat.computeIfPresent(entry.getKey(), (k, v) -> {
				int newValue = v + token;
				return newValue > tokenLimit ? null : newValue;
			});
		}
	}

	public static synchronized void reloadTask() {
		init();
		IRateLimitTask current = taskRef.get();
		current.stop();
		RateLimitConfig config = PatPatConfig.getInstance().getRateLimit();
		if (!config.isEnabled()) {
			return;
		}
		Time configInterval = config.getTokenInterval();
		long period = configInterval.getValue() * configInterval.getUnit().getMultiplier() * 20L;
		current.start(
				PatPatPlugin.getInstance(),
				period,
				() -> RateLimitManager.addPats(config.getTokenIncrement())
		);
	}
}
