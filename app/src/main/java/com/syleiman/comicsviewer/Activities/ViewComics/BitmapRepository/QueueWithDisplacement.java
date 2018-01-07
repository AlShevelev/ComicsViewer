package com.syleiman.comicsviewer.Activities.ViewComics.BitmapRepository;

import java.util.LinkedList;
import java.util.Queue;

public class QueueWithDisplacement
{
    private Queue<Integer> queue;
    private int maxLen;

    public QueueWithDisplacement(int maxLen)
    {
        this.queue = new LinkedList<Integer>();
        this.maxLen = maxLen;
    }

    public boolean isExists(Integer value)
    {
        for(Integer valueInList : queue)
            if(valueInList.equals(value))
                return true;
        return false;
    }

    /**
     * Push the value to queue (if not exists yet)
     * @param value
     * @return - displacement value or null
     */
    public Integer push(Integer value)
    {
        if(isExists(value))
            return null;
        else
        {
            if(queue.size()<maxLen)
            {
                queue.add(value);
                return null;
            }
            else
            {
                Integer result=queue.remove();
                queue.add(value);
                return result;
            }
        }
    }
}
