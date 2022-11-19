package com.adallom.spring.loadbalancer.mediator;


import com.adallom.spring.loadbalancer.consumer.LoadBalancerConsumerScheduler;
import com.adallom.spring.loadbalancer.definitions.LoadBalancerTask;
import com.adallom.spring.loadbalancer.definitions.LoadBalancerTaskType;
import com.adallom.spring.loadbalancer.model.LoadBalancerSettings;
import com.adallom.spring.loadbalancer.settings.LoadBalancerSettingsProvider;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

@Slf4j
@Component
public class LoadBalancerMediator
{
    // region Fields

    private final Map<LoadBalancerTaskType, LoadBalancerConsumerScheduler> consumerSchedulerTable = new HashMap<>();

    // endregion

    @Autowired
    public LoadBalancerMediator(LoadBalancerSettingsProvider settingsProvider)
    {
        log.info("[LB-Mediator] (TID: [{}]): Creating an LB Producer-Consumer table.", currentThread().getId());

        createLoadBalancerProducerConsumerTable(settingsProvider.get());

        log.info("[LB-Mediator] (TID: [{}]): Initialized. (OK).", currentThread().getId());
    }

    public void ingestTask(LoadBalancerTask loadBalancerTask) throws InterruptedException
    {
        log.info("[LB-Mediator] (TID: [{}]): Ingesting an LB Task. ({})", currentThread().getId(), loadBalancerTask);

        consumerSchedulerTable.get(loadBalancerTask.type()).publish(loadBalancerTask);
    }

    private Set<LoadBalancerTaskType> analyzeDistinctTaskTypes(LoadBalancerSettings settings)
    {
        return
            settings.getNodes().stream()
            .map(n -> new LoadBalancerTaskType(n.getTaskType()))
            .collect(Collectors.toSet());
    }

    private void createLoadBalancerProducerConsumerTable(LoadBalancerSettings settings)
    {
        var taskTypes = analyzeDistinctTaskTypes(settings);

        for (var taskType : taskTypes)
        {
            log.info("[LB-Mediator] (TID: [{}]): Creating an LB Producer-Consumer table entry. [Type: ({})]", currentThread().getId(), taskType.get());

            consumerSchedulerTable.put(taskType, new LoadBalancerConsumerScheduler(settings, taskType));

            log.info("[LB-Mediator] (TID: [{}]): Created an LB Producer-Consumer table entry. [Type: ({})]", currentThread().getId(), taskType.get());
        }
    }

    public void shutdown()
    {
        for (var schedulerTableEntry : consumerSchedulerTable.entrySet())
        {
            log.info("[LB-Mediator] (TID: [{}]): Signaling an LB Consumer Scheduler shutdown. [Type: ({})]", currentThread().getId(), schedulerTableEntry.getKey().get());

            schedulerTableEntry.getValue().shutdown();

            log.info("[Consumer-Node-Batch] (TID: [{}]): Signaled an LB Consumer Scheduler shutdown. (OK) [Type: ({})]", currentThread().getId(), schedulerTableEntry.getKey().get());
        }
    }

    @PreDestroy
    private void loadBalancerMediatorTermination() throws InterruptedException
    {
        for (var schedulerTableEntry : consumerSchedulerTable.entrySet())
        {
            log.info("[LB-Mediator] (TID: [{}]): Joining an LB Consumer Scheduler termination. [Type: ({})]", currentThread().getId(), schedulerTableEntry.getKey().get());

            schedulerTableEntry.getValue().joinTermination();

            log.info("[Consumer-Node-Batch] (TID: [{}]): LB Consumer Scheduler exited gracefully. (OK) [Type: ({})]", currentThread().getId(), schedulerTableEntry.getKey().get());
        }
    }
}
