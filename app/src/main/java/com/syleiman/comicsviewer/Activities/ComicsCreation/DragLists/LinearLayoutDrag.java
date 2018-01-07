package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.syleiman.comicsviewer.App;

/**
 * Extends standart LinearLayout to support dragging
 */
public class LinearLayoutDrag extends LinearLayout
{
    private ListView _listView;
    public ListView getListView() { return _listView; }
    public void setListView(ListView lv) { _listView = lv; }

    public LinearLayoutDrag(Context context)
    {
        super(context);
    }

    public LinearLayoutDrag(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LinearLayoutDrag(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


}
