package com.syleiman.comicsviewer.Activities.Main.ComicsFilters;

import java.util.Date;

/**
 * Dto for one comics for bookcase
 */
public class BookcaseComics
{
    /** Id in Db of comics  */
    public long id;

    /** Name of comics */
    public String name;

    /** Name to display (may be not equals with @name for private comics) */
    public String displayName;

    /** File name of cover without path */
    public String coverFilename;

    /** If true we should show private's comics cover (stub) */
    public boolean isNeedShowPrivateCover;

    /** Creation moment */
    public Date creationDate;

    /** Last date/time when comics was viewed  */
    public Date lastViewDate;

    /** Index of last viewed page */
    public int lastViewedPageIndex;

    /** Total pages in comics */
    public int totalPages;

    /** true if comics is private - it apears only after pasword was enter */
    public boolean isPrivate;

    public BookcaseComics()
    {
    }

    public BookcaseComics(
            long id,
            String name,
            String displayName,
            String coverFilename,
            boolean isNeedShowPrivateCover,
            Date creationDate,
            Date lastViewDate,
            int lastViewedPageIndex,
            int totalPages,
            boolean isPrivate)
    {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.coverFilename = coverFilename;
        this.isNeedShowPrivateCover = isNeedShowPrivateCover;
        this.creationDate = creationDate;
        this.lastViewDate = lastViewDate;
        this.lastViewedPageIndex = lastViewedPageIndex;
        this.totalPages = totalPages;
        this.isPrivate = isPrivate;
    }
}
