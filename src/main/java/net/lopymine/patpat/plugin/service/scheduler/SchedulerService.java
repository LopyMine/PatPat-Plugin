package net.lopymine.patpat.plugin.service.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.service.scheduler.task.WrapperTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class SchedulerService {
    private static final long TICKS_TO_MILLIS = 50L;
    private static boolean isFolia;
    private static PatPatPlugin plugin;

    public static void init(PatPatPlugin plugin) {
        SchedulerService.plugin = plugin;
        SchedulerService.isFolia = detectFolia();
    }

    private static boolean detectFolia() {
        return classExists("io.papermc.paper.threadedregions.RegionizedServer")
                || classExists("io.canvasmc.canvas.server.ThreadedServer");
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static WrapperTask runRepeatingGlobal(Runnable runnable, long delayTicks, long periodTicks) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    plugin,
                    scheduledTask -> runnable.run(),
                    delayTicks,
                    periodTicks
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                    plugin,
                    runnable,
                    delayTicks,
                    periodTicks
            );
            return WrapperTask.forBukkit(task, true);
        }
    }

    public static WrapperTask runLaterGlobal(Runnable runnable, long delayTicks) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(
                    plugin,
                    scheduledTask -> runnable.run(),
                    delayTicks
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    runnable,
                    delayTicks
            );
            return WrapperTask.forBukkit(task, false);
        }
    }

    public static WrapperTask runOnceGlobal(Runnable runnable) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getGlobalRegionScheduler().run(
                    plugin,
                    scheduledTask -> runnable.run()
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
            return WrapperTask.forBukkit(task, false);
        }
    }

    public static WrapperTask runRepeatingRegion(Location loc, Runnable runnable, long delayTicks, long periodTicks) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getRegionScheduler().runAtFixedRate(
                    plugin,
                    loc,
                    scheduledTask -> runnable.run(),
                    delayTicks,
                    periodTicks
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                    plugin,
                    runnable,
                    delayTicks,
                    periodTicks
            );
            return WrapperTask.forBukkit(task, true);
        }
    }

    public static WrapperTask runLaterRegion(Location loc, Runnable runnable, long delayTicks) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getRegionScheduler().runDelayed(
                    plugin,
                    loc,
                    scheduledTask -> runnable.run(),
                    delayTicks
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    runnable,
                    delayTicks
            );
            return WrapperTask.forBukkit(task, false);
        }
    }

    public static WrapperTask runOnceRegion(Location loc, Runnable runnable) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getRegionScheduler().run(
                    plugin,
                    loc,
                    scheduledTask -> runnable.run()
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
            return WrapperTask.forBukkit(task, false);
        }
    }

    public static WrapperTask runRepeatingAsync(Runnable runnable, long delayTicks, long periodTicks) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getAsyncScheduler().runAtFixedRate(
                    plugin,
                    scheduledTask -> runnable.run(),
                    delayTicks * TICKS_TO_MILLIS,
                    periodTicks * TICKS_TO_MILLIS,
                    TimeUnit.MILLISECONDS
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    runnable,
                    delayTicks,
                    periodTicks
            );
            return WrapperTask.forBukkit(task, true);
        }
    }

    public static WrapperTask runLaterAsync(Runnable runnable, long delayTicks) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getAsyncScheduler().runDelayed(
                    plugin,
                    scheduledTask -> runnable.run(),
                    delayTicks * TICKS_TO_MILLIS,
                    TimeUnit.MILLISECONDS
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(
                    plugin,
                    runnable,
                    delayTicks
            );
            return WrapperTask.forBukkit(task, false);
        }
    }

    public static WrapperTask runOnceAsync(Runnable runnable) {
        if (isFolia) {
            ScheduledTask task = Bukkit.getAsyncScheduler().runNow(
                    plugin,
                    scheduledTask -> runnable.run()
            );
            return WrapperTask.forFolia(task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
            return WrapperTask.forBukkit(task, false);
        }
    }
}