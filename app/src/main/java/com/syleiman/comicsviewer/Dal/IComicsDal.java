package com.syleiman.comicsviewer.Dal;

import com.syleiman.comicsviewer.Dal.Dto.Comics;
import com.syleiman.comicsviewer.Dal.Dto.Page;

import java.util.Date;
import java.util.List;

public interface IComicsDal
{
    /**
     *
     * @return id of comics or null if save unsuccess
     */
    Long createComics(Comics comics, List<Page> pages);

    /**
     * Get unsorted list of all comics
     * @param returnAll - return all comics (false - public only)
     * @return - set of comics or null in case of error
     */
    List<Comics> getComics(boolean returnAll);

    /**
     * Get one comics by its Id
     * @return - one comics or null in case of error
     */
    Comics getComicsById(long id);

    /**
     * Get unsorted list of comics' pages
     * @return
     */
    List<Page> getPages(long comicsId);

    boolean updateLastViewedPageIndex(long comicsId, int lastViewedPageIndex);

    boolean updateLastViewDate(long comicsId, Date lastViewDate);

    boolean updateNameAndHidden(long comicsId, String name, boolean isHidden);

    /**
     * Delete one comics by its id
     * @return - one comics or null in case of error
     */
    Comics deleteComics(long id);
}
