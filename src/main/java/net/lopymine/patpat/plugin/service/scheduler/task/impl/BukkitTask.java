package net.lopymine.patpat.plugin.service.scheduler.task.impl;

import net.lopymine.patpat.plugin.service.scheduler.task.WrapperTask;
import org.bukkit.Bukkit;

public class BukkitTask implements WrapperTask {
    private final org.bukkit.scheduler.BukkitTask task;
    private final boolean isRepeating;

    public BukkitTask(org.bukkit.scheduler.BukkitTask task, boolean isRepeating) {
        this.task = task;
        this.isRepeating = isRepeating;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public boolean isRepeatingTask() {
        return isRepeating;
    }

    @Override
    public boolean isCurrentlyRunning() {
        try {
            return Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "BukkitWrapper{" +
            "taskId=" + task.getTaskId() +
            ", cancelled=" + isCancelled() +
            ", repeating=" + isRepeatingTask() +
            ", running=" + isCurrentlyRunning() +
            '}';
    }
}