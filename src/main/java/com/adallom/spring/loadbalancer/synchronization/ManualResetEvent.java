package com.adallom.spring.loadbalancer.synchronization;

public final class ManualResetEvent
{
    // region Fields

    private final Object event;
    private volatile boolean signaled;

    // endregion

    public ManualResetEvent()
    {
        this.event = new Object();

        reset();
    }

    public void waitOne() throws InterruptedException
    {
        synchronized (event)
        {
            while (!signaled)
            {
                event.wait();
            }
        }
    }

    public boolean waitOne(long milliseconds) throws InterruptedException
    {
        synchronized (event)
        {
            if (signaled)
            {
                return true;
            }

            event.wait(milliseconds);

            return signaled;
        }
    }

    public void set()
    {
        synchronized (event)
        {
            signaled = true;

            event.notifyAll();
        }
    }

    public void reset()
    {
        signaled = false;
    }

    public boolean signaled()
    {
        return signaled;
    }
}
