package com.adallom.spring.loadbalancer.definitions;

import com.adallom.spring.loadbalancer.model.Task;

public final class LoadBalancerTask
{
    // region Fields

    private final Task task;

    // endregion

    public LoadBalancerTask(Task task)
    {
        this.task = task;
    }

    public String name()
    {
        return task.getTaskName();
    }

    public LoadBalancerTaskType type()
    {
        return new LoadBalancerTaskType(task.getTaskType());
    }

    public Double timeStamp()
    {
        return task.getTs();
    }

    public Double duration()
    {
        return task.getDuration();
    }

    @Override
    public String toString()
    {
        return
            String.format("[LB-T]: (N: [%s]) | (T: [%s]) | (TS: [%f]) | (D: [%f])",
                name(), type().get(), timeStamp(), duration());
    }
}
