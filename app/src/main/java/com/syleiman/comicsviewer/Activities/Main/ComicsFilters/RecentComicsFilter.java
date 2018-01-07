package com.syleiman.comicsviewer.Activities.Main.ComicsFilters;


import com.syleiman.comicsviewer.Activities.Main.ComicsSortInfo;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Comics;

import java.util.List;

/**
 * Get recent comics - public and private
 */
public class RecentComicsFilter extends FilterBase
{
    private static final int comicsToReturn=6;

    public RecentComicsFilter(ComicsSortInfo comicsSortInfo, boolean isPrivateComicsHidden)
    {
        super(comicsSortInfo, isPrivateComicsHidden);
    }

    @Override
    protected List<Comics> getComicsList()
    {
        List<Comics> result =  DalFacade.Comics.getComics(true);

        result = CollectionsHelper.where(result, item->item.lastViewDate!=null);
        result = CollectionsHelper.sort(result, (c1, c2) -> c1.lastViewDate.compareTo(c2.lastViewDate), true);
        result = CollectionsHelper.take(result, comicsToReturn, 0);

        return result;
    }
}
