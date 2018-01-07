package com.syleiman.comicsviewer.Common.Threads;

import java.util.concurrent.locks.ReentrantLock;

public class CancelationToken implements ICancelationTokenRead
{
    private boolean isCanceled=false;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public boolean isCanceled()
    {
        boolean isCanceled=false;

        lock.lock();
        try
        {
            isCanceled=this.isCanceled;
        }
        finally
        {
            lock.unlock();
        }

        return isCanceled;
    }

    public void cancel()
    {
        lock.lock();
        try
        {
            this.isCanceled=true;
        }
        finally
        {
            lock.unlock();
        }
    }
}
