package com.syleiman.comicsviewer.Activities.ViewComics;

import android.graphics.RectF;

import com.syleiman.comicsviewer.Common.Structs.SizeF;

/**
 * Information about renderer view
 */
public class RendererViewInfo
{
    private RectF viewRect = new RectF();       // View rect [units]
    private SizeF viewAreaSize;         // Size of view area [px]

    public RendererViewInfo(RectF viewRect, SizeF viewAreaSize)
    {
        this.viewRect = viewRect;
        this.viewAreaSize = viewAreaSize;
    }

    public RectF getViewRect()
    {
        return viewRect;
    }

    public SizeF getViewAreaSize()
    {
        return viewAreaSize;
    }
}
