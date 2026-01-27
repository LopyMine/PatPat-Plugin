package net.lopymine.patpat.plugin.service.scheduler.task.impl;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.lopymine.patpat.plugin.service.scheduler.task.WrapperTask;

public class FoliaTask implements WrapperTask {
    private final ScheduledTask task;

    public FoliaTask(ScheduledTask task) {
        this.task = task;
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
        return task.isRepeatingTask();
    }

    @Override
    public boolean isCurrentlyRunning() {
        return task.getExecutionState() == ScheduledTask.ExecutionState.RUNNING
            || task.getExecutionState() == ScheduledTask.ExecutionState.CANCELLED_RUNNING;
    }

    @Override
    public String toString() {
        return "FoliaWrapper{" +
            "task=" + task +
            ", cancelled=" + isCancelled() +
            ", repeating=" + isRepeatingTask() +
            ", running=" + isCurrentlyRunning() +
            '}';
    }
}

