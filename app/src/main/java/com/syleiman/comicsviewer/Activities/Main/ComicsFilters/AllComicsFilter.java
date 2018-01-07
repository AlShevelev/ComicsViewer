package com.syleiman.comicsviewer.Activities.Main.ComicsFilters;

import com.syleiman.comicsviewer.Activities.Main.ComicsSortInfo;
import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Comics;

import java.util.List;

/**
 * Get all comics - public and private
 */
public class AllComicsFilter extends FilterBase
{
    public AllComicsFilter(ComicsSortInfo comicsSortInfo, boolean isPrivateComicsHidden)
    {
        super(comicsSortInfo, isPrivateComicsHidden);
    }

    @Override
    protected List<Comics> getComicsList()
    {
        return DalFacade.Comics.getComics(true);
    }
}
