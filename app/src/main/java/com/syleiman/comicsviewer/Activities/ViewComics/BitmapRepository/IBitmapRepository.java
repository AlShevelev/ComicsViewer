package com.syleiman.comicsviewer.Activities.ViewComics.BitmapRepository;

import android.graphics.Bitmap;

/**
 * Created by Syleiman on 26.11.2015.
 */
public interface IBitmapRepository
{
    /**
     *
     * @param index index of page
     * @return
     */
    Bitmap getByIndex(int index, int viewAreaWidth, int viewAreaHeight);

    int getPageCount();
}
