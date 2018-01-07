package com.syleiman.comicsviewer.Activities.ViewComics;

import java.io.IOException;

/**
 * Provider for feeding 'book' with bitmaps which are used for rendering
 * pages.
 */
public interface IPageProvider
{

    /**
     * Return number of pages available.
     */
    public int getPageCount();

    /**
     * Called once new bitmaps/textures are needed. Width and height are in
     * pixels telling the size it will be drawn on screen and following them
     * ensures that aspect ratio remains. But it's possible to return bitmap
     * of any size though. You should use provided CurlPage for storing page
     * information for requested page number.<br/>
     * <br/>
     * Index is a number between 0 and getBitmapCount() - 1.
     */
    public void updatePage(CurlPage page, int width, int height, int index);
}