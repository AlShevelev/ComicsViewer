package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import java.util.List;

/**
 * Information about draging from listener
 */
public class ListItemDragingInfo
{
    private final ListItemDrag _draggedItem;
    public ListItemDrag getDraggedItem() { return _draggedItem; }

    /**
     * Drag from this list
     */
    private final List<ListItemDrag> _sourceList;
    public List<ListItemDrag> getSourceList() { return _sourceList; }

    /**
     * Drag to this list
     */
    private final List<ListItemDrag> _destinationList;
    public List<ListItemDrag> getDestinationList() { return _destinationList; }

    /**
     * Drag from this agapter
     */
    private final ListDragAdapter _sourceAdapter;
    public ListDragAdapter getSourceAdapter() { return _sourceAdapter; }

    /**
     * Drag to this adapter
     */
    private final ListDragAdapter _destinationAdapter;
    public ListDragAdapter getDestinationAdapter() { return _destinationAdapter; }

    /**
     * Index of item in target list
     */
    private final int _destinationLocation;
    public int getDestinationLocation() { return _destinationLocation; }

    public ListItemDragingInfo(
            ListItemDrag draggedItem,
            List<ListItemDrag> sourceList,
            List<ListItemDrag> destinationList,
            ListDragAdapter sourceAdapter,
            ListDragAdapter destinationAdapter,
            int destinationLocation)
    {
        _draggedItem = draggedItem;
        _sourceList = sourceList;
        _destinationList = destinationList;
        _sourceAdapter = sourceAdapter;
        _destinationAdapter = destinationAdapter;
        _destinationLocation = destinationLocation;
    }
}
