package com.syleiman.comicsviewer.Dal;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IFuncOneArg;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Dal.Dto.Comics;
import com.syleiman.comicsviewer.Dal.Dto.Page;
import com.syleiman.comicsviewer.Dal.Entities.DbComics;
import com.syleiman.comicsviewer.Dal.Entities.DbPage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ComicsDal implements IComicsDal
{
    public Long createComics(Comics comics, List<Page> pages)
    {
        ActiveAndroid.beginTransaction();
        try
        {
            comics.lastViewedPageIndex=0;           // From first page
            DbComics dbComics=new DbComics(comics);
            dbComics.save();

            for(Page page : pages)
            {
                DbPage dbPage=new DbPage(page, dbComics);
                dbPage.save();
            }

            ActiveAndroid.setTransactionSuccessful();

            return dbComics.getId();
        }
        catch (Exception ex)
        {
            Log.e("CV", "exception", ex);
            return null;
        }
        finally
        {
            ActiveAndroid.endTransaction();
        }
    }

    /**
     * Get unsorted list of all comics
     * @param returnAll - return all comics (false - not private only)
     * @return - set of comics or null in case of error
     */
    public List<Comics> getComics(boolean returnAll)
    {
        try
        {
            ArrayList<DbComics> dbResult=null;
            if(returnAll)
                dbResult = new Select().from(DbComics.class).execute();
            else
                dbResult = new Select().from(DbComics.class).where("IsHidden = ?", 0).execute();        // boolean maps to integer!

            return CollectionsHelper.transform(dbResult, item -> new Comics(item));
        }
        catch (Exception ex)
        {
            Log.e("CV", "exception", ex);
            return null;
        }
    }

    /**
     * Get one comics by its Id
     * @return - one comics or null in case of error
     */
    public Comics getComicsById(long id)
    {
        try
        {
            DbComics dbComics = DbComics.load(DbComics.class, id);
            return new Comics(dbComics);
        }
        catch (Exception ex)
        {
            Log.e("CV", "exception", ex);
            return null;
        }
    }

    /**
     * Get unsorted list of comics' pages
     * @return
     */
    public List<Page> getPages(long comicsId)
    {
        try
        {
            ArrayList<DbPage> dbResult=new Select().from(DbPage.class).where("Comics = ?", comicsId).execute();

            return CollectionsHelper.transform(dbResult, item -> new Page(item));
        }
        catch (Exception ex)
        {
            Log.e("CV", "exception", ex);
            return null;
        }
    }

    public boolean updateLastViewedPageIndex(long comicsId, int lastViewedPageIndex)
    {
        return update(comicsId, comics->{ comics.lastViewedPageIndex= lastViewedPageIndex; });

    }

    public boolean updateLastViewDate(long comicsId, Date lastViewDate)
    {
        return update(comicsId, comics->{ comics.lastViewDate=lastViewDate; });
    }

    @Override
    public boolean updateNameAndHidden(long comicsId, String name, boolean isHidden)
    {
        return update(comicsId, comics->{ comics.name=name; comics.isHidden=isHidden; });
    }

    private boolean update(long comicsId, IActionOneArgs<DbComics> processor)
    {
        ActiveAndroid.beginTransaction();
        try
        {
            DbComics dbComics = DbComics.load(DbComics.class, comicsId);
            processor.process(dbComics);
            dbComics.save();

            ActiveAndroid.setTransactionSuccessful();
            return true;
        }
        catch (Exception ex)
        {
            Log.e("CV", "exception", ex);
            return false;
        }
        finally
        {
            ActiveAndroid.endTransaction();
        }
    }

    /**
     * Delete one comics by its id
     * @return - deleleted comics or null if unsuccess
     */
    @Override
    public Comics deleteComics(long id)
    {
        Comics result = null;

        ActiveAndroid.beginTransaction();
        try
        {
            result = getComicsById(id);
            result.pages = getPages(id);

            new Delete().from(DbPage.class).where("Comics = ?", id).execute();
            DbComics.delete(DbComics.class, id);

            ActiveAndroid.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.e("CV", "exception", ex);
            return null;
        }
        finally
        {
            ActiveAndroid.endTransaction();
        }

        return result;
    }
}