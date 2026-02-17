package net.lopymine.patpat.plugin.ratelimit;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.RateLimitConfig;
import net.lopymine.patpat.plugin.util.FoliaUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class RateLimitManager {

	private RateLimitManager() {
		throw new IllegalStateException("Manager class");
	}

	private static IRateLimitTask task;

	private static final Map<UUID, Integer> uuidToPat = new HashMap<>();

	public static void init() {
		if (task != null) {
			return;
		}
		if(FoliaUtils.IS_FOLIA){
			try {
				task = (IRateLimitTask) Class.forName("net.lopymine.patpat.plugin.folia.FoliaRateLimitTask").getConstructor().newInstance();
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
		task = new BukkitRateLimitTask();
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
		int availablePats = uuidToPat.getOrDefault(
				uuid,
				config.getTokenLimit()
		) - 1;
		if (availablePats < 0) {
			return false;
		}
		uuidToPat.put(uuid, availablePats);
		return true;
	}

	public static void addPats(int token) {
		int tokenLimit = PatPatConfig.getInstance().getRateLimit().getTokenLimit();
		for (Iterator<Map.Entry<UUID, Integer>> it = uuidToPat.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<UUID, Integer> entry = it.next();
			int value = entry.getValue() + token;
			if (value > tokenLimit) {
				it.remove();
				continue;
			}
			uuidToPat.put(entry.getKey(), value);
		}
	}

	public static void reloadTask() {
		init();
		task.stop();
		RateLimitConfig config = PatPatConfig.getInstance().getRateLimit();
		if (!config.isEnabled()) {
			return;
		}
		Time configInterval = config.getTokenInterval();
		long period = configInterval.getValue() * configInterval.getUnit().getMultiplier() * 20L;
		task.start(
				PatPatPlugin.getInstance(),
				period,
				() -> RateLimitManager.addPats(config.getTokenIncrement())
		);
	}

}
