package com.syleiman.comicsviewer.Common.Collections;

import com.syleiman.comicsviewer.Common.FuncInterfaces.IFuncOneArg;

import java.util.LinkedList;

/**
 * Thread-safe stack with spilling in case of oversizing
 * @param <E>
 */
public class ConcurrentSpillingStack<E>
{
    private final LinkedList<E> internalList;

    private final int maxLen;
    private int len;

    private static final Object monitor = new Object();

    /**
     * Create collection
     * @param maxLen if size of collection will be greater that this value old values will be spilled
     */
    public ConcurrentSpillingStack(int maxLen)
    {
        this.internalList = new LinkedList<>();

        this.maxLen = maxLen;
        len = 0;
    }

    /**
     * Push @newItem in a head of stack
     * @param newItem item to push
     * @return spilled item or null if last item was not spilled
     */
    public synchronized E push(E newItem)
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
     * Pop first element from head
     * @return element or null if stack is empty
     */
    public synchronized E pop()
    {
        if(len==0)
            return null;

        E result = internalList.removeFirst();
        len--;

        return result;
    }

    /**
     * Remove item from list by condition
     */
    public synchronized void remove(IFuncOneArg<E, Boolean> condition)
    {
        if(len==0)
            return;

        for(E item : internalList)
            if(condition.process(item))
                internalList.remove(item);
    }
}