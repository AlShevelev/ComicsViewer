package com.syleiman.comicsviewer.Dal.Entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.syleiman.comicsviewer.Dal.Dto.Comics;

import java.util.Date;
import java.util.List;

/**
 * One comics
 */
@Table(name = "Comics")
public class DbComics extends Model
{
    @Column(name = "Name")
    public String name;

    @Column(name = "CoverFilename")
    public String coverFilename;

    @Column(name = "CreationDate")
    public Date creationDate;

    /** Last date/time when comics was viewed */
    @Column(name = "LastViewDate")
    public Date lastViewDate;

    /** Index of last viewed page */
    @Column(name = "LastViewedPageIndex")
    public int lastViewedPageIndex;

    /** Total pages in comics */
    @Column(name = "TotalPages")
    public int totalPages;

    /** true if comics if hidden - it apears only after pasword was enter */
    @Column(name = "IsHidden")
    public boolean isHidden;

    public DbComics()
    {
    }

    public DbComics(Comics comics)
    {
        name = comics.name;
        coverFilename = comics.coverFilename;
        creationDate = comics.creationDate;
        lastViewDate = comics.lastViewDate;
        lastViewedPageIndex = comics.lastViewedPageIndex;
        totalPages = comics.totalPages;
        isHidden = comics.isPrivate;
    }

    /**
     * Get all pages of comics
     */
    public List<DbPage> getPages()
    {
        return getMany(DbPage.class, "Comics");
    }
}