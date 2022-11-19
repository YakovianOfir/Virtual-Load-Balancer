package com.adallom.spring.loadbalancer.consumer;


import com.adallom.spring.loadbalancer.definitions.LoadBalancerNode;
import com.adallom.spring.loadbalancer.definitions.LoadBalancerTaskType;
import com.adallom.spring.loadbalancer.model.LoadBalancerSettings;
import com.adallom.spring.loadbalancer.model.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

@Slf4j
public class LoadBalancerNodeIterator implements Iterator<LoadBalancerNode>
{
    // region Fields

    private Integer listIndex = 0;

    @Getter
    private final List<LoadBalancerNode> nodeList;

    // endregion

    public LoadBalancerNodeIterator(LoadBalancerSettings settings, LoadBalancerTaskType taskType)
    {
        log.info("[Node-Iterator] (TID: [{}]): Observing LB Nodes in settings ({}).", currentThread().getId(), settings);

        var loadBalancerNodeSettings = settings.getNodes();

        log.info("[Node-Iterator] (TID: [{}]): Iterating ({}) formal LB nodes of type ({}).", currentThread().getId(), taskType);

        this.nodeList = createFormalLoadBalancerNodesOfType(loadBalancerNodeSettings, taskType);

        log.info("[Node-Iterator] (TID: [{}]): Initialized. (OK). ({})", currentThread().getId(), currentThread().getName());
    }

    private List<LoadBalancerNode> createFormalLoadBalancerNodesOfType(List<Node> loadBalancerNodeSettings, LoadBalancerTaskType taskType)
    {
        return
            loadBalancerNodeSettings.stream()
            .filter(n -> n.getTaskType().equals(taskType.get()))
            .map(n -> new LoadBalancerNode(n))
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasNext()
    {
        return true;
    }

    @Override
    public LoadBalancerNode next()
    {
        var nextNode =  nodeList.get(listIndex++ % nodeList.size());

        log.info("[Node-Iterator] (TID: [{}]): Resolved next node (Round-Robin). ({})", currentThread().getId(), nextNode);

        return nextNode;
    }
}
