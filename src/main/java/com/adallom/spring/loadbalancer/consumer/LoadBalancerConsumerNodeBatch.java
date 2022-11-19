package com.adallom.spring.loadbalancer.consumer;


import com.adallom.spring.loadbalancer.definitions.LoadBalancerNode;
import com.adallom.spring.loadbalancer.definitions.LoadBalancerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.currentThread;

@Slf4j
public class LoadBalancerConsumerNodeBatch
{
    // region Fields

    private final Map<LoadBalancerNode, LoadBalancerConsumerNodeThread> nodeBatch = new HashMap<>();

    // endregion

    public LoadBalancerConsumerNodeBatch(List<LoadBalancerNode> loadBalancerNodes)
    {
        log.info("[Consumer-Node-Batch] (TID: [{}]): Creating LB Consumer Node threads. ({})", currentThread().getId(), loadBalancerNodes.size());

        createLoadBalancerNodeBatch(loadBalancerNodes);

        log.info("[Consumer-Node-Batch] (TID: [{}]): Firing LB Consumer Node threads. ({})", currentThread().getId(), loadBalancerNodes.size());

        startLoadBalancerNodeBatch();

        log.info("[Consumer-Node-Batch] (TID: [{}]): Initialized. (OK).", currentThread().getId());
    }

    private void createLoadBalancerNodeBatch(List<LoadBalancerNode> loadBalancerNodes)
    {
        for (var loadBalancerNode : loadBalancerNodes)
        {
            log.info("[Consumer-Node-Batch] (TID: [{}]): Creating an LB Consumer thread. [Node: ({})]", currentThread().getId(), loadBalancerNode);

            var loadBalancerConsumerNodeThread = createLoadBalancerConsumerNodeThread(loadBalancerNode);

            log.info("[Consumer-Node-Batch] (TID: [{}]): Initialized an LB Consumer thread. [Node: ({})]", currentThread().getId(), loadBalancerNode);

            nodeBatch.put(loadBalancerNode, loadBalancerConsumerNodeThread);
        }
    }

    private LoadBalancerConsumerNodeThread createLoadBalancerConsumerNodeThread(LoadBalancerNode loadBalancerNode)
    {
        log.info("[Consumer-Node-Batch] (TID: [{}]): Creating an LB Consumer Node thread. [Node: ({})]", currentThread().getId(), loadBalancerNode);

        var consumerNodeThread = new LoadBalancerConsumerNodeThread(loadBalancerNode);

        log.info("[Consumer-Node-Batch] (TID: [{}]): Initialized. (OK). (C-TID: [{}])", currentThread().getId(), consumerNodeThread.getId());

        return consumerNodeThread;
    }

    private void startLoadBalancerNodeBatch()
    {
        for (var nodeBatchEntry : nodeBatch.entrySet())
        {
            log.info("[Consumer-Node-Batch] (TID: [{}]): Firing an LB Consumer Node thread. [Node: ({})]", currentThread().getId(), nodeBatchEntry.getKey());

            nodeBatchEntry.getValue().start();

            log.info("[Consumer-Node-Batch] (TID: [{}]): Started an LB Consumer thread. [Node: ({})] (C-TID: ({}))", currentThread().getId(), nodeBatchEntry.getKey(), nodeBatchEntry.getValue().getId());
        }
    }

    public void publish(LoadBalancerNode node, LoadBalancerTask task) throws InterruptedException
    {
        log.info("[Consumer-Node-Batch] (TID: [{}]): Publishing Task -> ({}) to Node -> ({})", currentThread().getId(), task, node);

        nodeBatch.get(node).publish(task);
    }

    public void shutdown()
    {
        for (var nodeBatchEntry : nodeBatch.entrySet())
        {
            log.info("[Consumer-Node-Batch] (TID: [{}]): Signaling an LB Consumer Node thread shutdown. [Node: ({})]", currentThread().getId(), nodeBatchEntry.getKey());

            nodeBatchEntry.getValue().shutdown();

            log.info("[Consumer-Node-Batch] (TID: [{}]): Signaled an LB Consumer Node thread shutdown. (OK) [Node: ({})]", currentThread().getId(), nodeBatchEntry.getKey());
        }
    }

    public void joinTermination() throws InterruptedException
    {
        for (var nodeBatchEntry : nodeBatch.entrySet())
        {
            log.info("[Consumer-Node-Batch] (TID: [{}]): Joining an LB Consumer Node thread. [Node: ({})]", currentThread().getId(), nodeBatchEntry.getKey());

            nodeBatchEntry.getValue().wait();

            log.info("[Consumer-Node-Batch] (TID: [{}]): LB Consumer Node thread exited gracefully. (OK) [Node: ({})]", currentThread().getId(), nodeBatchEntry.getKey());
        }
    }

}
