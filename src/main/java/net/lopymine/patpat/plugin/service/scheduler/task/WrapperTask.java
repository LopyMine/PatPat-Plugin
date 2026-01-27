package net.lopymine.patpat.plugin.service.scheduler.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.lopymine.patpat.plugin.service.scheduler.task.impl.BukkitTask;
import net.lopymine.patpat.plugin.service.scheduler.task.impl.FoliaTask;

public interface WrapperTask {

    void cancel();

    boolean isCancelled();

    boolean isRepeatingTask();

    boolean isCurrentlyRunning();

    static WrapperTask forFolia(ScheduledTask task) {
        if (task == null) {
            throw new IllegalArgumentException();
        }
        return new FoliaTask(task);
    }

    static WrapperTask forBukkit(org.bukkit.scheduler.BukkitTask task, boolean isRepeating) {
        if (task == null) {
            throw new IllegalArgumentException();
        }
        return new BukkitTask(task, isRepeating);
    }
}