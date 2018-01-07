package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.syleiman.comicsviewer.Activities.ComicsCreation.ISortPagesActivityItemsEvents;
import com.syleiman.comicsviewer.Common.Structs.Area;
import com.syleiman.comicsviewer.Common.Structs.Point;
import com.syleiman.comicsviewer.Common.Structs.Size;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;

/**
 * Show hint after click list item
 */
public class ListItemOnClickTouchListener implements AdapterView.OnItemClickListener, AdapterView.OnTouchListener
{
        private final ISortPagesActivityItemsEvents events;

    private Point touchPos;         // Touch position in listView

    public ListItemOnClickTouchListener(ISortPagesActivityItemsEvents events)
    {
        this.events = events;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(touchPos==null)
            return;

        Area area=getAreaByTag(view, "zoomIcon", 0, 0);
        if(area.isHit(touchPos))
            events.onZoomItem(position);
        else
        {
            area=getAreaByTag(view, "visibilityIcon", 0, 0);
            if(area.isHit(touchPos))
                events.onSetVisibilityItem(position);
            else
                ToastsHelper.Show(           // Show tag with item's name
                        ((ListItemDrag) (parent.getItemAtPosition(position))).getItemLongString(),
                        ToastsHelper.Position.Bottom);
        }
        touchPos=null;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_UP)
            touchPos=new Point((int)event.getX(), (int)event.getY());

        return false;
    }

    /**
     * Get control area with some tag
     * @param view
     * @param lookedTag
     * @param top
     * @param left
     * @return
     */
    private Area getAreaByTag(View view, String lookedTag, int top, int left)
    {
        left+=view.getLeft();
        top+=view.getTop();

        Object tag= view.getTag();
        if(tag!=null && tag.equals(lookedTag))
            return new Area(new Point(left, top), new Size(view.getWidth(), view.getHeight()));

        if(view instanceof ViewGroup)
        {
            ViewGroup viewGroup = (ViewGroup)view;
            for(int i=0; i<viewGroup.getChildCount(); i++)
            {
                Area area = getAreaByTag(viewGroup.getChildAt(i), lookedTag, top, left);
                if(area!=null)
                    return area;
            }
        }
        return null;
    }
}
