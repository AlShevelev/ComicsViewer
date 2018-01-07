package com.syleiman.comicsviewer.Dal.Dto;

import com.syleiman.comicsviewer.Dal.Entities.DbComics;

import java.util.Date;
import java.util.List;

/**
 * One comics
 */
public class Comics
{
    public long id;

    public String name;

    public String coverFilename;

    public Date creationDate;

    /** Last date/time when comics was viewed  */
    public Date lastViewDate;

    /** Index of last viewed page */
    public int lastViewedPageIndex;

    /** Total pages in comics */
    public int totalPages;

    /** true if comics if hidden - it apears only after pasword was enter */
    public boolean isPrivate;

    /** List of pages */
    public List<Page> pages;

    public Comics()
    {

    }

    public Comics(DbComics dbComics)
    {
        id = dbComics.getId();

        coverFilename = dbComics.coverFilename;
        creationDate = dbComics.creationDate;
        name = dbComics.name;
        lastViewDate = dbComics.lastViewDate;
        lastViewedPageIndex = dbComics.lastViewedPageIndex;
        totalPages = dbComics.totalPages;
        isPrivate = dbComics.isHidden;
    }
}


