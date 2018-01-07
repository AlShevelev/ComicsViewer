package com.syleiman.comicsviewer.Common.ProducerConsumer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.syleiman.comicsviewer.Common.Collections.ConcurrentSpillingStack;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IFuncOneArg;

/**
 * Base class for Producer/Consumer pattern. One consumer runing in background thread
 */
public abstract class ProducerConsumerBase
{
    private ConcurrentSpillingStack<ProducerConsumerTaskBase> tasks;

    private Handler uiHandler;              // for pass message to uiThread

    private Thread workingThread;              // for pass message to uiThread

    private static final Object monitor = new Object();             // for thread-safe

    private class Job implements Runnable
    {
        /**
         * Main working method (runs in background thread)
         */
        @Override
        public void run()
        {
            while(true)
            {
                ProducerConsumerTaskBase taskToProcess=null;
                try
                {
                    taskToProcess = tasks.pop();

                    if (taskToProcess == null)             // Queue is empty
                        synchronized (monitor)
                        {
                            monitor.wait();
                        }
                    else
                    {
                        if (taskToProcess.kind == ProducerConsumerTaskKinds.Stop)
                        {
                            Object data = ((StopTask)taskToProcess).getData();
                            uiHandler.sendMessage(Message.obtain(null, MessagesCodes.StopThread, data));     // Inform UI thread about stop processing
                            break;
                        } else
                        {
                            ProducerConsumerTaskProcessingResultBase result = processTask(taskToProcess);
                            if (result != null)
                                uiHandler.sendMessage(Message.obtain(null, MessagesCodes.TaskCompleted, result));   // Inform UI thread about complete task
                            else
                                uiHandler.sendMessage(Message.obtain(null, MessagesCodes.TaskError, taskToProcess));
                        }
                    }
                }
                catch (Exception ex)
                {
                    Log.e("ProducerConsumerBase", ex.getMessage(), ex);
                    if(taskToProcess!=null)
                        uiHandler.sendMessage(Message.obtain(null, MessagesCodes.TaskError, taskToProcess));
                }
            }
        }
    }

    /**
     *
     */
    public ProducerConsumerBase(int maxQueueLen)
    {
        this.tasks = new ConcurrentSpillingStack<>(maxQueueLen);

        uiHandler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg) { return handleUIMessage(msg);  }
        });

        workingThread=new Thread(new Job());
    }

    /**
     * Start processing tasks (runs in UI thread)
     */
    public void start()
    {
        workingThread.start();
    }

    /**
     * Stop processing tasks  (runs in UI thread)
     */
    public void stop(Object dataToReturn)
    {
        Thread.State threadState=workingThread.getState();
        if(threadState == Thread.State.NEW || threadState == Thread.State.TERMINATED)
            onStopProcessing(dataToReturn);

        processAsync(new StopTask(dataToReturn));
    }

    /**
     * Add task in processing queue  (runs in UI thread)
     */
    public void processAsync(ProducerConsumerTaskBase task)
    {
        ProducerConsumerTaskBase spilledTask = tasks.push(task);

        synchronized (monitor)
        {
            monitor.notifyAll();
        }

        if(spilledTask!=null)
            onTaskSpilled(spilledTask);
    }

    /**
     * Process task and return result (runs in UI thread)
     * @return result of task or null in case of error
     */
    public ProducerConsumerTaskProcessingResultBase processSync(ProducerConsumerTaskBase task)
    {
        try
        {
            return processTask(task);
        }
        catch (Exception ex)
        {
            Log.e("ProducerConsumerBase", ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Add task in processing queue  (runs in UI thread)
     */
    public void removeTask(IFuncOneArg<ProducerConsumerTaskBase, Boolean> condition)
    {
        tasks.remove(condition);
    }

    /**
     * Processing callback messages from working thread (runs in UI thread)
     * @param msg message from working thread
     * @return
     */
    private boolean handleUIMessage(Message msg)
    {
        switch (msg.what)
        {
            case MessagesCodes.StopThread : onStopProcessing(msg.obj); break;
            case MessagesCodes.TaskCompleted : onTaskProcessed((ProducerConsumerTaskProcessingResultBase) msg.obj); break;
            case MessagesCodes.TaskError : onTaskError((ProducerConsumerTaskBase) msg.obj); break;
        }
        return true;
    }

    /**
     * Method processing tasks (runs in background thread)
     * @param task
     * @return result of calculation or null in case of error
     * @throws InterruptedException
     */
    protected abstract ProducerConsumerTaskProcessingResultBase processTask(ProducerConsumerTaskBase task) throws InterruptedException;

    /**
     * Method called when processing stoped  (runs in UI thread)
     */
    protected abstract void onStopProcessing(Object data);

    /**
     * Method called when processing stoped (runs in UI thread)
     */
    protected abstract void onTaskProcessed(ProducerConsumerTaskProcessingResultBase result);

    /**
     * Method called when there was an error while task calculation (runs in UI thread)
     */
    protected abstract void onTaskError(ProducerConsumerTaskBase task);

    /**
     * Method called when task was spilled from processing queue (runs in UI thread)
     */
    protected abstract void onTaskSpilled(ProducerConsumerTaskBase task);
}