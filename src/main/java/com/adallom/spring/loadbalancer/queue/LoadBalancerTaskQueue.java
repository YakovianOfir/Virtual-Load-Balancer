package com.adallom.spring.loadbalancer.queue;


import com.adallom.spring.loadbalancer.definitions.LoadBalancerTask;
import com.adallom.spring.loadbalancer.definitions.LoadBalancerTaskType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.currentThread;

@Slf4j
public class LoadBalancerTaskQueue
{
    // region Fields

    @Getter
    private final LoadBalancerTaskType taskType;

    @Getter
    private final BlockingQueue<LoadBalancerTask> taskQueue;

    // endregion

    public LoadBalancerTaskQueue(LoadBalancerTaskType taskType)
    {
        this.taskType = taskType;

        log.info("[LB-Queue] (TID: [{}]): Creating an LB queue. [Type: ({})]", currentThread().getId(), taskType);

        this.taskQueue = new LinkedBlockingQueue<>();

        log.info("[LB-Queue] (TID: [{}]): Initialized. (OK) [Type: ({})]", currentThread().getId(),taskType);
    }

    public void put(LoadBalancerTask loadBalancerTask) throws InterruptedException
    {
        log.info("[LB-Queue] (TID: [{}]): Inserting an LB task. ({})", currentThread().getId(), loadBalancerTask);

        taskQueue.put(loadBalancerTask);
    }

    public LoadBalancerTask take() throws InterruptedException
    {
        var loadBalancerTask = taskQueue.take();

        log.info("[LB-Queue] (TID: [{}]): Popping an LB task. ({})", currentThread().getId(), loadBalancerTask);

        return loadBalancerTask;
    }
}
