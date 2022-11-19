package com.adallom.spring.loadbalancer.definitions;

import java.util.Objects;

public class LoadBalancerTaskType
{
    // region Fields

    private String taskType;

    // endregion

    public LoadBalancerTaskType(String taskType)
    {
        this.taskType = taskType;
    }

    public String get()
    {
        return taskType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        LoadBalancerTaskType that = (LoadBalancerTaskType) o;

        return Objects.equals(taskType, that.taskType);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(taskType);
    }
}
