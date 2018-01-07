package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ListView;

import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;

import java.util.List;

/**
 * Listener for dragging events for item in list
 */
public class ListItemOnDragListener implements View.OnDragListener
{
    private ListItemDrag _item;

    private final int _dragColor;
    private int _normalColor;

    private IActionOneArgs<ListItemDragingInfo> onDrag;

    ListItemOnDragListener(ListItemDrag item, int dragColor, IActionOneArgs<ListItemDragingInfo> onDrag)
    {
        _item = item;
        _dragColor = dragColor;
        this.onDrag = onDrag;
    }

    @Override
    public boolean onDrag(View v, DragEvent event)
    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d("Action", "Item ACTION_DRAG_STARTED: " + "\n");
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d("Action", "Item ACTION_DRAG_ENTERED: " + "\n");
                _normalColor =((ColorDrawable)v.getBackground()).getColor();
                v.setBackgroundColor(_dragColor);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d("Action", "Item ACTION_DRAG_EXITED: " + "\n");
                v.setBackgroundColor(_normalColor);
                break;
            case DragEvent.ACTION_DROP:
                Log.d("Action", "Item ACTION_DROP: " + "\n");

                PassObjectDrag passObj = (PassObjectDrag)event.getLocalState();
                View view = passObj.getView();
                ListItemDrag passedItem = passObj.getItem();
                List<ListItemDrag> srcList = passObj.getSrcList();
                ListView oldParent = (ListView)view.getParent();
                ListDragAdapter srcAdapter = (ListDragAdapter)(oldParent.getAdapter());

                ListView newParent = (ListView)v.getParent();
                ListDragAdapter destAdapter = (ListDragAdapter)(newParent.getAdapter());
                List<ListItemDrag> destList = destAdapter.getList();

                int removeLocation = srcList.indexOf(passedItem);
                int insertLocation = destList.indexOf(_item);

                if(removeLocation != insertLocation)
                    onDrag.process(new ListItemDragingInfo(passedItem, srcList, destList, srcAdapter, destAdapter, insertLocation));

                v.setBackgroundColor(_normalColor);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d("Action", "Item ACTION_DRAG_ENDED: " + "\n");
                v.setBackgroundColor(_normalColor);
            default:
                break;
        }

        return true;
    }
}