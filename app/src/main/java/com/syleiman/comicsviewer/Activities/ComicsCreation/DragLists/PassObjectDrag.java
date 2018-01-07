package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.view.View;

import java.util.List;

/**
 * Objects passed in Drag and Drop operation
 */
public class PassObjectDrag
{
    private View _view;
    public View getView() { return _view; }

    private ListItemDrag _item;
    public ListItemDrag getItem() { return _item; }

    private List<ListItemDrag> _srcList;
    public List<ListItemDrag> getSrcList() { return _srcList; }

    PassObjectDrag(View v, ListItemDrag i, List<ListItemDrag> s)
    {
        _view = v;
        _item = i;
        _srcList = s;
    }
}
