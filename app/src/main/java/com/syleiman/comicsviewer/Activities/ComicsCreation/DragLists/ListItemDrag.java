package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.graphics.drawable.Drawable;

import com.syleiman.comicsviewer.Common.Helpers.BitmapDarkRate;

/**
 * Item for page's sorting ListView
 */
public class ListItemDrag
{
    private final int id;
    public int getId() { return id; }

    /**
     * Is page visible
     */
    private boolean isVisibile;
    public boolean getIsVisibile() { return isVisibile; }
    public void setIsVisibile(boolean value) { isVisibile=value; }

    /**
     * Title (cutted if nessesary)
     */
    private final String itemString;
    public String getItemString() { return itemString; }

    /**
     * Title (uncutted)
     */
    private final String itemLongString;
    public String getItemLongString() { return itemLongString; }

    /**
     * Full path to image
     */
    private final String fullPathToImageFile;
    public String getFullPathToImageFile() { return fullPathToImageFile; }

    public ListItemDrag(
            int id,
            String shortTitle,
            String longTitle,
            boolean isVisibile,
            String fullPathToImageFile)
    {
        this.id =id;
        this.itemString = shortTitle;
        this.itemLongString = longTitle;
        this.isVisibile = isVisibile;
        this.fullPathToImageFile = fullPathToImageFile;
    }
}
