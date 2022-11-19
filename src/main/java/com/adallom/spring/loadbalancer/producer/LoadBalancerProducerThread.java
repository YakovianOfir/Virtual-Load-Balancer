package com.adallom.spring.loadbalancer.producer;


import com.adallom.spring.loadbalancer.definitions.LoadBalancerTask;
import com.adallom.spring.loadbalancer.mediator.LoadBalancerMediator;
import com.adallom.spring.loadbalancer.model.LoadBalancerSettings;
import com.adallom.spring.loadbalancer.synchronization.SynchronizedThread;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadBalancerProducerThread extends SynchronizedThread
{
    // region Fields

    private LoadBalancerTaskIterator taskIterator;

    private LoadBalancerMediator loadBalancerMediator;

    // endregion

    public LoadBalancerProducerThread(LoadBalancerSettings settings, LoadBalancerMediator loadBalancerMediator)
    {
        log.info("[Producer-Thread] (TID: [{}]): Creating an LB Tasks iterator.", currentThread().getId());

        this.taskIterator = new LoadBalancerTaskIterator(settings);

        log.info("[Producer-Thread] (TID: [{}]): Initialized. (OK). ({})", currentThread().getId(), currentThread().getName());

        this.loadBalancerMediator = loadBalancerMediator;
    }

    @Override
    public void run()
    {
        while (!quitEvent.signaled() && taskIterator.hasNext())
        {
            try
            {
                ingestTask(taskIterator.next(), taskIterator.nextTimeStamp());
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void ingestTask(LoadBalancerTask loadBalancerTask, Double waitTimeout) throws InterruptedException
    {
        log.info("[Producer-Thread] (TID: [{}]): Waiting ({}s) before ingesting task -> ({})", currentThread().getId(), waitTimeout, loadBalancerTask);

        if (waitTimeout != 0D && quitEvent.waitOne(waitTimeout.longValue()))
        {
            log.warn("[Producer-Thread] (TID: [{}]): Quit event has been raised. Aborting", currentThread().getId());

            return;
        }

        log.info("[Producer-Thread] (TID: [{}]): Ingesting task -> ({})", currentThread().getId(), loadBalancerTask);

        loadBalancerMediator.ingestTask(loadBalancerTask);

        log.info("[Producer-Thread] (TID: [{}]): Ingested task -> ({}). (OK)", currentThread().getId(), loadBalancerTask);
    }
}
