package com.syleiman.comicsviewer.Activities.Main;

import com.syleiman.comicsviewer.Dal.Dto.Comics;

import java.util.Comparator;

/**
 * Data for comics sort
 */
public class ComicsSortInfo
{
    private Comparator<Comics> comparator;

    /** Need revers order     */
    private boolean reverse;

    public ComicsSortInfo(Comparator<Comics> comparator, boolean reverse)
    {
        this.comparator = comparator;
        this.reverse = reverse;
    }

    public Comparator<Comics> getComparator()
    {
        return comparator;
    }

    public boolean isReverse()
    {
        return reverse;
    }
}
