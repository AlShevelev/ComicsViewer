package com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails;

import android.graphics.drawable.Drawable;

public class ThumbnailCalculationResult
{
    private final int id;
    public int getId() { return id; }

    /**
     * Calculated image
     */
    private final Drawable pageImage;
    public Drawable getPageImage() { return pageImage; }

    public ThumbnailCalculationResult(int id, Drawable pageImage)
    {
        this.id = id;
        this.pageImage = pageImage;
    }
}
