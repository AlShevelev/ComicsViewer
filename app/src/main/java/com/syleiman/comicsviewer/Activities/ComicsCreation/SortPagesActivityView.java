package com.syleiman.comicsviewer.Activities.ComicsCreation;

import android.content.Context;
import android.widget.RelativeLayout;

import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.Structs.Size;

/**
 * Contecst view for ComicsCreationActivity
 */
public class SortPagesActivityView extends RelativeLayout
{
    public SortPagesActivityView(Context context, int layoutId)
    {
        super(context);

        inflate(context, layoutId, this);
    }

    public Size getSize()
    {
        return new Size(getWidth(), getHeight());
    }
}
