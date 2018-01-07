package com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails;

import android.graphics.drawable.Drawable;

import com.syleiman.comicsviewer.Common.ProducerConsumer.ProducerConsumerTaskProcessingResultBase;

/**
 * Thumbnail calculation result
 */
public class ThumbnailTaskResult extends ProducerConsumerTaskProcessingResultBase
{
    private final ThumbnailListIds listIds;
    public ThumbnailListIds getListIds() { return listIds; }

    /**
     * Calculated image
     */
    private final Drawable pageImage;
    public Drawable getPageImage() { return pageImage; }

    public ThumbnailTaskResult(int id, ThumbnailListIds listIds, Drawable pageImage)
    {
        super(id);
        this.pageImage = pageImage;
        this.listIds = listIds;
    }
}
