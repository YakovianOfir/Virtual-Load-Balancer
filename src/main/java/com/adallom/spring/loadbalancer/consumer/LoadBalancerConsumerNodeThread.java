package com.adallom.spring.loadbalancer.consumer;


import com.adallom.spring.loadbalancer.definitions.LoadBalancerNode;
import com.adallom.spring.loadbalancer.definitions.LoadBalancerTask;
import com.adallom.spring.loadbalancer.queue.LoadBalancerTaskQueue;
import com.adallom.spring.loadbalancer.synchronization.SynchronizedThread;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadBalancerConsumerNodeThread extends SynchronizedThread
{
    // region Fields

    private final LoadBalancerNode node;

    private final LoadBalancerTaskQueue taskQueue;

    // endregion

    public LoadBalancerConsumerNodeThread(LoadBalancerNode loadBalancerNode)
    {
        log.info("[Consumer-Node-Thread] (TID: [{}]): Acquiring the responsibility of an LB Node. ({})", currentThread().getId(), loadBalancerNode);

        this.node = loadBalancerNode;

        log.info("[Consumer-Node-Thread] (TID: [{}]): Creating an underlying LB Task queue. ({})", currentThread().getId(), loadBalancerNode);

        this.taskQueue = new LoadBalancerTaskQueue(loadBalancerNode.type());

        log.info("[Consumer-Node-Thread] (TID: [{}]): Initialized. (OK). ({})", currentThread().getId(), loadBalancerNode);
    }

    public void publish(LoadBalancerTask loadBalancerTask) throws InterruptedException
    {
        log.info("[Consumer-Node-Thread] (TID: [{}]): Inserting an LB Task. ({})", currentThread().getId(), loadBalancerTask);

        taskQueue.put(loadBalancerTask);
    }

    @Override
    public void run()
    {
        while (!quitEvent.signaled())
        {
            try
            {
                processTask(taskQueue.take());
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void processTask(LoadBalancerTask task) throws InterruptedException
    {
        populateFormalProcessingMessage(task);

        log.info("[Consumer-Node-Thread] (TID: [{}]): Processing task -> ({}). [Duration: [({}s)]", currentThread().getId(), task, task.duration());

        if (quitEvent.waitOne(task.duration().longValue()))
        {
            log.warn("[Consumer-Node-Thread] (TID: [{}]): Quit event has been raised. Aborting", currentThread().getId());

            return;
        }

        log.info("[Consumer-Node-Thread] (TID: [{}]): Processed task -> ({})", currentThread().getId(), task);
    }

    private void populateFormalProcessingMessage(LoadBalancerTask task)
    {
        System.out.println(
            String.format("Executed task=\'%s\' type=\'%s\' on node=\'%s\' started at t=\'%f\'",
                task.name(), task.type().get(), node.name(), task.timeStamp()));
    }
}
