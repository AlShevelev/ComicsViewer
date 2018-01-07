package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails.IThumbnailControl;
import com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails.ThumbnailListIds;

/**
 *
 */
public class ListThumbnailHolder implements IThumbnailControl
{
    private final int id;
    private final ThumbnailListIds listId;
    private final String fullSourceFileName;
    private final ImageView pageImage;

    public ListThumbnailHolder(int id, ThumbnailListIds listId, String fullSourceFileName, ImageView pageImage)
    {
        this.id = id;
        this.listId = listId;
        this.fullSourceFileName = fullSourceFileName;
        this.pageImage = pageImage;
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public ThumbnailListIds getListId()
    {
        return listId;
    }

    @Override
    public String getFullSourceFileName()
    {
        return fullSourceFileName;
    }

    @Override
    public int getImageHachCode()
    {
        return pageImage.hashCode();
    }

    @Override
    public void setThumbnail(Drawable thumbnail)
    {
        pageImage.setImageDrawable(thumbnail);
    }
}
