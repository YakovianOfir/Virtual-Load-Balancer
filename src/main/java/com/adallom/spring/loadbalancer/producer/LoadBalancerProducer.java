package com.adallom.spring.loadbalancer.producer;


import com.adallom.spring.loadbalancer.mediator.LoadBalancerMediator;
import com.adallom.spring.loadbalancer.settings.LoadBalancerSettingsProvider;
import com.adallom.spring.loadbalancer.synchronization.SynchronizedThread;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.currentThread;

@Slf4j
@Component
public class LoadBalancerProducer
{
    // region Fields

    private final SynchronizedThread producerThread;

    private final LoadBalancerMediator loadBalancerMediator;

    // endregion

    @Autowired
    public LoadBalancerProducer(LoadBalancerSettingsProvider settingsProvider, LoadBalancerMediator loadBalancerMediator)
    {
        this.loadBalancerMediator = loadBalancerMediator;

        log.info("[LB-Producer] (TID: [{}]): Creating an LB Producer thread.", currentThread().getId());

        this.producerThread = new LoadBalancerProducerThread(settingsProvider.get(), loadBalancerMediator);

        log.info("[LB-Producer] (TID: [{}]): Firing the LB Producer thread.", currentThread().getId());

        this.producerThread.start();

        log.info("[LB-Producer] (TID: [{}]): Initialized. (OK). (P-TID: [{}])", currentThread().getId(), producerThread.getId());
    }

    @PreDestroy
    private void loadBalancerProducerTermination() throws InterruptedException
    {
        log.info("[LB-Producer] (TID: [{}]): Signaling LB Producer thread shutdown. (TP-TID: [{}])", currentThread().getId(), producerThread.getId());

        producerThread.shutdown();

        log.info("[LB-Producer] (TID: [{}]): Joining LB Producer thread. (TP-TID: [{}])", currentThread().getId(), producerThread.getId());

        producerThread.wait();

        log.info("[LB-Producer] (TID: [{}]): Signaling LB Mediator shutdown. (Mediator: [{}])", currentThread().getId(), loadBalancerMediator);

        loadBalancerMediator.shutdown();

        log.info("[LB-Producer] (TID: [{}]): Successfully Terminating. (OK)", currentThread().getId());
    }
}
