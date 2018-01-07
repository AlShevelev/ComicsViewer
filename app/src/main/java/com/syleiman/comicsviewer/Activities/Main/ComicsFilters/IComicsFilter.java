package com.syleiman.comicsviewer.Activities.Main.ComicsFilters;

import com.syleiman.comicsviewer.Dal.Dto.Comics;

import java.util.List;

/**
 * Return list of comics
 */
public interface IComicsFilter
{
    List<BookcaseComics> getComics();
}
