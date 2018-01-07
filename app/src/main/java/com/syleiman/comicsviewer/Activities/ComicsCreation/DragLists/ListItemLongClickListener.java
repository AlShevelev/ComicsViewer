package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.content.ClipData;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

/**
 * Listener for long click on item (to start drag)
 */
public class ListItemLongClickListener implements AdapterView.OnItemLongClickListener
{
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        ListItemDrag selectedItem = (ListItemDrag)(parent.getItemAtPosition(position));

        ListDragAdapter associatedAdapter = (ListDragAdapter)(parent.getAdapter());
        List<ListItemDrag> associatedList = associatedAdapter.getList();

        PassObjectDrag passObj = new PassObjectDrag(view, selectedItem, associatedList);

        ClipData data = ClipData.newPlainText("", "");          // Start drag on long click
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, passObj, 0);

        return true;
    }
}
