package com.syleiman.comicsviewer.Common.ProducerConsumer;

/**
 * Service task for stoping procssing
 */
class StopTask extends ProducerConsumerTaskBase
{
    private final Object data;
    public Object getData(){return data;}


    public StopTask(Object data)
    {
        super(-1, ProducerConsumerTaskKinds.Stop);

        this.data = data;
    }
}
