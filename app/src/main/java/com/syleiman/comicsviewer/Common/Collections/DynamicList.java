package com.syleiman.comicsviewer.Common.Collections;

import com.syleiman.comicsviewer.Common.FuncInterfaces.IFuncOneArg;

import java.util.LinkedList;

/**
 * Simple list with push and extract item operations
  */
public class DynamicList<E>
{
    protected final LinkedList<E> internalList;

    public DynamicList()
    {
        internalList = new LinkedList<>();
    }

    /**
     * Push @newItem in a head of list
     */
    public void push(E newItem)
    {
        internalList.addFirst(newItem);
    }


    /**
     * Get first item by condition and remove it from list
     * @param condition
     * @return item or null if not found
     */
    public E extract(IFuncOneArg<E, Boolean> condition)
    {
        for (E item : internalList)
        {
            if (condition.process(item))
            {
                internalList.remove(item);
                return item;
            }
        }

        return null;            // Not found
    }

    /**
     * Is exists image with such conditions
     * @param condition
     */
    public boolean isExists(IFuncOneArg<E, Boolean> condition)
    {
        for (E item : internalList)
            if (condition.process(item))
                return true;

        return false;
    }
}
