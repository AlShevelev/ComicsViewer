package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ListView;

import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;

import java.util.List;

/**
 * Listener for dragging on area (not into other list)
 */
public class AreaOnDragListener implements View.OnDragListener
{
    private IActionOneArgs<ListItemDragingInfo> _onDrag;

    public AreaOnDragListener(IActionOneArgs<ListItemDragingInfo> onDrag)
    {
        _onDrag = onDrag;
    }

    @Override
    public boolean onDrag(View v, DragEvent event)
    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d("Action", "ACTION_DRAG_STARTED\n");
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d("Action", "ACTION_DRAG_ENTERED:\n");
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d("Action", "ACTION_DRAG_EXITED:\n");
                break;
            case DragEvent.ACTION_DROP:
                Log.d("Action", "ACTION_DROP:\n");

                PassObjectDrag passObj = (PassObjectDrag)event.getLocalState();
                View view = passObj.getView();
                ListItemDrag passedItem = passObj.getItem();
                List<ListItemDrag> srcList = passObj.getSrcList();
                ListView oldParent = (ListView)view.getParent();
                ListDragAdapter srcAdapter = (ListDragAdapter)(oldParent.getAdapter());

                LinearLayoutDrag newParent = (LinearLayoutDrag)v;
                ListDragAdapter destAdapter = (ListDragAdapter)(newParent.getListView().getAdapter());
                List<ListItemDrag> destList = destAdapter.getList();

                if(_onDrag!=null)           // Notify about dragging
                    _onDrag.process(new ListItemDragingInfo(passedItem, srcList, destList, srcAdapter, destAdapter, 0));

                //smooth scroll to bottom
                newParent.getListView().smoothScrollToPosition(destAdapter.getCount()-1);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d("Action", "ACTION_DRAG_ENDED:\n");
            default:
                break;
        }

        return true;
    }
}
