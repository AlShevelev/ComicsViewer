package com.syleiman.comicsviewer.Activities.Main.Bookshelf;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.BookcaseComics;
import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.IComicsFilter;
import com.syleiman.comicsviewer.ComicsWorkers.CoverCreator;
import com.syleiman.comicsviewer.ComicsWorkers.IPreviewCreator;
import com.syleiman.comicsviewer.ComicsWorkers.PreviewCreator;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;
import com.syleiman.comicsviewer.Common.Helpers.BitmapsHelper;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.AppPrivateFilesHelper;
import com.syleiman.comicsviewer.Common.Structs.Size;
import com.syleiman.comicsviewer.Dal.Dto.Comics;

import java.util.List;

/**
 * Read comics for bookshelf
 */
public class BookshelfComicsReader  extends AsyncTask<Void, Void, Void>
{
    private List<BookshelfComicsInfo> readComics;

    private IActionZeroArgs beforeExecute;
    private IActionOneArgs<List<BookshelfComicsInfo>> afterExecute;

    private IComicsFilter comicsFilter;     // source of comics

    private Bitmap privateCover;

    private final IPreviewCreator previewCreator;

    public BookshelfComicsReader(
            IComicsFilter comicsFilter,
            IActionZeroArgs beforeExecute,
            IActionOneArgs<List<BookshelfComicsInfo>> afterExecute,
            Size clientSize)
    {
        this.beforeExecute = beforeExecute;
        this.afterExecute = afterExecute;
        this.comicsFilter = comicsFilter;

        previewCreator = new PreviewCreator(clientSize);
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            List<BookcaseComics> comics=comicsFilter.getComics();

            if(comics!=null)
                readComics=CollectionsHelper.transform(comics, item ->
                        new BookshelfComicsInfo(
                                item.id,
                                item.displayName,
                                getCover(item),
                                item.isNeedShowPrivateCover));

            else
                readComics = null;
        }
        catch (Exception ex)
        {
            Log.e("CV", "exception", ex);
        }

        return null;
    }

    private Bitmap getCover(BookcaseComics comics)
    {
        if(comics.isNeedShowPrivateCover)
        {
            if(privateCover==null)
                privateCover = CoverCreator.create(BitmapsHelper.loadFromRaw(R.raw.private_comics_cover), previewCreator);

            return privateCover;
        }

        return BitmapsHelper.loadFromFile(AppPrivateFilesHelper.getFullName(comics.coverFilename));
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        beforeExecute.process();
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);

        afterExecute.process(readComics);
    }
}