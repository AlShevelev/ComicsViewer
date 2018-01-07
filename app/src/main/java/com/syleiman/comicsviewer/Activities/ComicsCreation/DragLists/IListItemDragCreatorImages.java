package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.graphics.drawable.Drawable;

/**
 * Interface to working with images
 */
public interface IListItemDragCreatorImages
{
    Drawable getPageImage(String fullBitmapFileName);

    Drawable getStubPageImage();
}
