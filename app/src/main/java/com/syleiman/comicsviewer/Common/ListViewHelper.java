package com.syleiman.comicsviewer.Common;

import android.view.View;
import android.widget.ListView;

public class ListViewHelper
{
    /**
     * Invalidate item on listView with some index
     * @param itemIndex
     * @param list
     */
    public static void invalidateListItem(int itemIndex, ListView list)
    {
        int start = list.getFirstVisiblePosition();
        for(int i=start, j=list.getLastVisiblePosition();i<=j;i++)
            if(i==itemIndex)
            {
                View view = list.getChildAt(i-start);
                list.getAdapter().getView(i, view, list);           // Re-setDiskItems control
                break;
            }
    }

}
