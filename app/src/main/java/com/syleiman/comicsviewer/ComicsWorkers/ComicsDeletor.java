package com.syleiman.comicsviewer.ComicsWorkers;

import com.syleiman.comicsviewer.Common.Helpers.Files.AppPrivateFilesHelper;
import com.syleiman.comicsviewer.Common.Rhea.RheaOperationBase;
import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Comics;
import com.syleiman.comicsviewer.Dal.Dto.Page;

/**
 * Comics is deleted here
 */
public class ComicsDeletor extends RheaOperationBase
{
    private final Long comicsId;

    public static final String tag = "ComicsDeletionRhea";

    public ComicsDeletor(String tag, long comicsId)
    {
        super(tag);
        this.comicsId = comicsId;
    }


    @Override
    protected Object process()
    {
        Comics comics=DalFacade.Comics.deleteComics(this.comicsId);
        if(comics!=null)
            if(AppPrivateFilesHelper.delete(comics.coverFilename))
                for (Page page : comics.pages)
                    AppPrivateFilesHelper.delete(page.fileName);
        return null;
    }
}
