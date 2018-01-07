package com.syleiman.comicsviewer.Activities.Main.Bookshelf;

import android.graphics.Bitmap;

/**
 * One comics info for BookshelfListAdapter
 */
public class BookshelfComicsInfo
{
    private long id;                // id from Db
    public long getId() { return id; };

    private String title;
    public String getTitle() { return title; };

    private Bitmap image;
    public Bitmap getImage() { return image; };

    private boolean privateAndClosed;                // is comics private and password was not entered
    public boolean isPrivateAndClosed() { return privateAndClosed; };

    public BookshelfComicsInfo(long id, String title, Bitmap image, boolean privateAndClosed)
    {
        this.id = id;
        this.title = title;
        this.image = image;
        this.privateAndClosed = privateAndClosed;
    }
}