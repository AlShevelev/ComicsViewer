package com.syleiman.comicsviewer.Common.ProducerConsumer;

/**
 * Base class for result of task
 */
public abstract class ProducerConsumerTaskProcessingResultBase
{
    /**
     * TaskBase::id
     */
    private final int id;
    public int getId() { return id; }

    protected ProducerConsumerTaskProcessingResultBase(int id)
    {
        this.id = id;
    }
}
