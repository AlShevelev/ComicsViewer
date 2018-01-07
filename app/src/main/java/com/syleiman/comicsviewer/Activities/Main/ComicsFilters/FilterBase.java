package com.syleiman.comicsviewer.Activities.Main.ComicsFilters;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.Main.ComicsSortInfo;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Dal.Dto.Comics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Base filter for comics
 */
public abstract class FilterBase implements IComicsFilter
{
    private final ComicsSortInfo comicsSortInfo;
    private final boolean isPrivateComicsHidden;

    public FilterBase(ComicsSortInfo comicsSortInfo, boolean isPrivateComicsHidden)
    {
        this.comicsSortInfo = comicsSortInfo;
        this.isPrivateComicsHidden = isPrivateComicsHidden;
    }

    public List<BookcaseComics> getComics()
    {
        List<Comics> resultDraft= getComicsList();

        if(comicsSortInfo!=null)
            resultDraft = CollectionsHelper.sort(resultDraft, comicsSortInfo.getComparator(), comicsSortInfo.isReverse());

        return transform(resultDraft);
    }

    protected abstract List<Comics> getComicsList();

    private List<BookcaseComics> transform(List<Comics> source)
    {
        ArrayList<BookcaseComics> result=new ArrayList<>(source.size());

        int nameCounter=1;
        for(Comics sourceComics : source)
        {
            BookcaseComics targetComics = new BookcaseComics();

            targetComics.id = sourceComics.id;

            targetComics.name = sourceComics.name;

            targetComics.displayName  = isPrivateComicsHidden & sourceComics.isPrivate ?
                    String.format(App.getResourceString(R.string.private_comics_title), nameCounter++) :
                    sourceComics.name;

            targetComics.coverFilename = sourceComics.coverFilename;

            targetComics.isNeedShowPrivateCover = isPrivateComicsHidden & sourceComics.isPrivate;

            targetComics.creationDate = sourceComics.creationDate;

            targetComics.lastViewDate = sourceComics.lastViewDate;

            targetComics.lastViewedPageIndex = sourceComics.lastViewedPageIndex;

            targetComics.totalPages = sourceComics.totalPages;

            targetComics.isPrivate = sourceComics.isPrivate;

            result.add(targetComics);
        }

        return result;
    }
}
