package com.syleiman.comicsviewer.Activities.Main.ComicsFilters;

import com.syleiman.comicsviewer.Activities.Main.ComicsSortInfo;
import com.syleiman.comicsviewer.Options.OptionsFacade;
import com.syleiman.comicsviewer.Options.OptionsKeys;

public class ComicsFilterFactory
{
    public static IComicsFilter getFilter(ComicsViewMode mode)
    {
        boolean isPrivateComicsHidden = OptionsFacade.ShortLivings.get(OptionsKeys.PasswordEntered)==null;

        if(mode== ComicsViewMode.All)
            return new AllComicsFilter(new ComicsSortInfo((c1, c2) -> c1.name.compareTo(c2.name), false), isPrivateComicsHidden);
        else
            return new RecentComicsFilter(null, isPrivateComicsHidden);
    }
}
