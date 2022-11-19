package com.adallom.spring.loadbalancer.synchronization;

public class SynchronizedThread extends Thread
{
    // region Fields

    protected ManualResetEvent quitEvent;

    // endregion

    public SynchronizedThread()
    {
        this.quitEvent = new ManualResetEvent();
    }

    public void shutdown()
    {
        quitEvent.set();
    }
}
