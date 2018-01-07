package com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails;

import com.syleiman.comicsviewer.Common.ProducerConsumer.ProducerConsumerTaskBase;

/**
 * Task to calculate thumbnail
 */
public class ThumbnailTask extends ProducerConsumerTaskBase
{
    private final ThumbnailListIds listIds;
    public ThumbnailListIds getListIds() { return listIds; }

    /**
     * Full path to image to calculate
     */
    private final String fullPathToImageFile;
    public String getFullPathToImageFile() { return fullPathToImageFile; }

    public ThumbnailTask(int id, ThumbnailListIds listIds, String fullPathToImageFile)
    {
        super(id);
        this.fullPathToImageFile = fullPathToImageFile;
        this.listIds = listIds;
    }
}
