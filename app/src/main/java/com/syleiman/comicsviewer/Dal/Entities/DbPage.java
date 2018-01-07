package com.syleiman.comicsviewer.Dal.Entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.syleiman.comicsviewer.Dal.Dto.Page;

/**
 * One page of comics
 */
@Table(name = "Page")
public class DbPage extends Model
{
    @Column(name = "FileName")
    public String fileName;

    @Column(name = "SortingOrder")
    public int order;

    @Column(name = "Comics")
    public DbComics comics;

    @Column(name = "IsLeftTopCornerDark")
    public boolean isLeftTopCornerDark;

    @Column(name = "IsLeftBottomCornerDark")
    public boolean isLeftBottomCornerDark;

    @Column(name = "IsRightTopCornerDark")
    public boolean isRightTopCornerDark;

    @Column(name = "IsRightBottomCornerDark")
    public boolean isRightBottomCornerDark;

    public DbPage()
    {
    }

    public DbPage(Page page, DbComics comics)
    {
        this.comics = comics;
        fileName = page.fileName;
        order = page.order;

        isLeftBottomCornerDark = page.isLeftBottomCornerDark;
        isLeftTopCornerDark = page.isLeftTopCornerDark;
        isRightBottomCornerDark = page.isRightBottomCornerDark;
        isRightTopCornerDark = page.isRightTopCornerDark;
    }
}
