package net.lopymine.patpat.plugin.command.ratelimit;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.RateLimitConfig;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RateLimitManager {

    private RateLimitManager() {
        throw new IllegalStateException("Manager class");
    }

    private static volatile ScheduledTask task;

    private static final ConcurrentMap<UUID, Integer> uuidToPat = new ConcurrentHashMap<>();

    public static int getAvailablePats(UUID uuid) {
        RateLimitConfig config = PatPatConfig.getInstance().getRateLimit();
        return uuidToPat.getOrDefault(uuid, config.getTokenLimit());
    }

    public static boolean canPat(UUID uuid) {
        RateLimitConfig config = PatPatConfig.getInstance().getRateLimit();
        if (!config.isEnabled()) {
            return true;
        }

        final int tokenLimit = config.getTokenLimit();
        final AtomicBoolean allowed = new AtomicBoolean(false);

        uuidToPat.compute(uuid, (k, oldVal) -> {
            int current = (oldVal == null ? tokenLimit : oldVal);
            if (current <= 0) {
                allowed.set(false);
                return 0;
            }
            allowed.set(true);
            int next = current - 1;
            // Если хотим хранить "полный лимит" как отсутствие ключа, то это касается addPats(), не canPat()
            return next;
        });

        return allowed.get();
    }

    public static void addPats(int token) {
        RateLimitConfig config = PatPatConfig.getInstance().getRateLimit();
        int tokenLimit = config.getTokenLimit();

        uuidToPat.forEach((uuid, value) -> {
            int next = value + token;
            if (next >= tokenLimit) {
                uuidToPat.remove(uuid);
            } else {
                uuidToPat.replace(uuid, value, next);
            }
        });
    }

    public static void reloadTask() {
        ScheduledTask old = task;
        if (old != null) {
            old.cancel(); // Folia cancel API
            task = null;
        }

        RateLimitConfig config = PatPatConfig.getInstance().getRateLimit();
        if (!config.isEnabled()) {
            return;
        }

        Time interval = config.getTokenInterval();
        long periodTicks = interval.getValue() * interval.getUnit().getMultiplier() * 20L +1L;

        PatPatPlugin plugin = PatPatPlugin.getInstance();
        task = org.bukkit.Bukkit.getGlobalRegionScheduler()
                .runAtFixedRate(
                        plugin,
                        st -> RateLimitManager.addPats(config.getTokenIncrement()),
                        1L,
                        periodTicks
                );

    }
}
