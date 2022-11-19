package com.adallom.spring.loadbalancer.producer;


import com.adallom.spring.loadbalancer.definitions.LoadBalancerTask;
import com.adallom.spring.loadbalancer.model.LoadBalancerSettings;
import com.adallom.spring.loadbalancer.model.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

@Slf4j
public class LoadBalancerTaskIterator implements Iterator<LoadBalancerTask>
{
    // region Fields

    private final List<LoadBalancerTask> taskList;
    private final ListIterator<LoadBalancerTask> taskIterator;

    // endregion

    public LoadBalancerTaskIterator(LoadBalancerSettings settings)
    {
        log.info("[Task-Iterator] (TID: [{}]): Observing LB Tasks in settings ({}).", currentThread().getId(), settings);
        var loadBalancerTaskSettings = settings.getTasks();

        log.info("[Task-Iterator] (TID: [{}]): Iterating ({}) formal LB tasks in total.", currentThread().getId(), loadBalancerTaskSettings.size());
        this.taskList = createFormalLoadBalancerTasks(loadBalancerTaskSettings);

        log.info("[Task-Iterator] (TID: [{}]): Initialized. (OK). ({})", currentThread().getId(), currentThread().getName());
        this.taskIterator = taskList.listIterator();
    }

    private static List<LoadBalancerTask> createFormalLoadBalancerTasks(List<Task> loadBalancerTaskSettings)
    {
        return
            loadBalancerTaskSettings.stream()
            .map(t -> new LoadBalancerTask(t))
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasNext()
    {
        return taskIterator.hasNext();
    }

    @Override
    public LoadBalancerTask next()
    {
        return taskIterator.next();
    }

    public Double nextTimeStamp()
    {
        if (taskIterator.hasPrevious())
        {
            var prev = taskIterator.previous();
            var next = taskIterator.next();

            return next.timeStamp() - prev.timeStamp();
        }
        else
        {
            var next = taskIterator.next();
            var prev = taskIterator.previous();

            return next.timeStamp() - prev.timeStamp();
        }
    }
}
