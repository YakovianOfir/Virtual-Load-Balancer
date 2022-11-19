package com.adallom.spring.loadbalancer.consumer;


import com.adallom.spring.loadbalancer.definitions.LoadBalancerTask;
import com.adallom.spring.loadbalancer.definitions.LoadBalancerTaskType;
import com.adallom.spring.loadbalancer.model.LoadBalancerSettings;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.currentThread;

@Slf4j
public class LoadBalancerConsumerScheduler
{
    // region Fields

    private final LoadBalancerTaskType taskType;
    private final LoadBalancerNodeIterator nodeIterator;
    private final LoadBalancerConsumerNodeBatch nodeBatch;

    // endregion

    public LoadBalancerConsumerScheduler(LoadBalancerSettings settings, LoadBalancerTaskType taskType)
    {
        this.taskType = taskType;

        log.info("[Consumer-Scheduler] (TID: [{}]): Creating an LB Nodes iterator. [Type: ({})]", currentThread().getId(), taskType);

        this.nodeIterator = new LoadBalancerNodeIterator(settings, taskType);

        log.info("[Consumer-Scheduler] (TID: [{}]): Creating an LB Nodes batch. [Type: ({})]", currentThread().getId(), taskType);

        this.nodeBatch = new LoadBalancerConsumerNodeBatch(nodeIterator.getNodeList());

        log.info("[Consumer-Scheduler] (TID: [{}]): Initialized. (OK). [Type: ({})]", currentThread().getId(), currentThread().getName());
    }

    public void publish(LoadBalancerTask loadBalancerTask) throws InterruptedException
    {
        log.info("[Consumer-Scheduler] (TID: [{}]): Publishing Task -> ({}) to Node batch -> ({})", currentThread().getId(), loadBalancerTask, nodeBatch);

        nodeBatch.publish(nodeIterator.next(), loadBalancerTask);
    }

    public void shutdown()
    {
        log.info("[Consumer-Scheduler] (TID: [{}]): Signaling an LB Consumer Node batch shutdown. [Type: ({})]", currentThread().getId(), taskType.get());

        nodeBatch.shutdown();
    }

    public void joinTermination() throws InterruptedException
    {
        log.info("[Consumer-Scheduler] (TID: [{}]): Joining LB Consumer Node batch termination. [Type: ({})]", currentThread().getId(), taskType.get());

        nodeBatch.joinTermination();
    }
}
