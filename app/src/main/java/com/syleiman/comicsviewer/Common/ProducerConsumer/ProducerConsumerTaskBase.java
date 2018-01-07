package com.syleiman.comicsviewer.Common.ProducerConsumer;

/**
 * Base class for task
 */
public abstract class ProducerConsumerTaskBase
{
    /**
     * Id of task
     */
    private final int id;
    public int getId() { return id; }

    protected final ProducerConsumerTaskKinds kind;
    public ProducerConsumerTaskKinds getKind() { return kind; }

    protected ProducerConsumerTaskBase(int id)
    {
        this(id, ProducerConsumerTaskKinds.Normal);
    }

    protected ProducerConsumerTaskBase(int id, ProducerConsumerTaskKinds kind)
    {
        this.id = id;
        this.kind = kind;
    }
}
