package com.syleiman.comicsviewer.Common.Collections;

import com.syleiman.comicsviewer.Common.FuncInterfaces.IFuncOneArg;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Queue with spilling in case of oversizing
 * @param <E>
 */
public class SpillingQueue<E>
{
    protected final LinkedList<E> internalList;

    private final int maxLen;
    private int len;

    /**
     * Create collection
     * @param maxLen if size of collection will be greater that this value old values will be spilled
     */
    public SpillingQueue(int maxLen)
    {
        this.internalList = new LinkedList<>();

        this.maxLen = maxLen;
        len = 0;
    }

    /**
     * Push @newItem in a head of queue
     * @param newItem item to push
     * @return spilled item or null if last item was not spilled
     */
    public E push(E newItem)
    {
        E spilledItem = null;

        if(len==maxLen)
            spilledItem = internalList.removeLast();
        else
            len++;

        internalList.addFirst(newItem);

        return spilledItem;
    }

    /**
     * Get first item by condition
     * @param condition
     * @return item or null if not found
     */
    public E get(IFuncOneArg<E, Boolean> condition)
    {
        if(len==0)
            return null;

        for (E item : internalList)
            if (condition.process(item))
                return item;

        return null;
    }

    /**
     * Get first item by condition and move it into head of list
     * @param condition
     * @return item or null if not found
     */
    public E getAndMoveToHead(IFuncOneArg<E, Boolean> condition)
    {
        if(len==0)
            return null;

        E result=null;

        int index=0;
        for (E item : internalList)
        {
            if (condition.process(item))
            {
                result = item;
                break;
            }
            index++;
        }

        if(result!=null)
        {
            internalList.remove(index);
            internalList.addFirst(result);
        }

        return result;
    }
}