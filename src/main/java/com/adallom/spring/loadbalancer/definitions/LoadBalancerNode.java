package com.adallom.spring.loadbalancer.definitions;

import com.adallom.spring.loadbalancer.model.Node;

public final class LoadBalancerNode
{
    // region Fields

    private final Node node;

    // endregion

    public LoadBalancerNode(Node node)
    {
        this.node = node;
    }

    public String name()
    {
        return node.getNodeName();
    }

    public LoadBalancerTaskType type()
    {
        return new LoadBalancerTaskType(node.getTaskType());
    }

    @Override
    public String toString()
    {
        return
            String.format("[LB-N]: (N: [%s]) | (T: [%s])",
                name(), type().get());
    }
}
