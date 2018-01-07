package com.syleiman.comicsviewer.Activities.PagesMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.Main.Bookshelf.ViewHolders;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionTwoArgs;
import com.syleiman.comicsviewer.Common.Structs.Size;
import com.syleiman.comicsviewer.Dal.Dto.Page;

import java.util.ArrayList;
import java.util.List;

public class PagesMapAdapter extends BaseAdapter
{
    private Size parentSize;            // Size of content view

    private int pagesInRow;           // Max pages in row
    private int rowsCountMin;

    private static final int PAGES_IN_ROW_VERT =2;
    private static final int PAGES_IN_ROW_HORIZ =3;

    private static final int ROWS_COUNT_MIN_HORIZ =2;
    private static final int ROWS_COUNT_MIN_VERT =3;         // Minimum shelfs on screen

    private int maxPageWidth;

    private List<Page> pages;
    private int pagesCount;

    private static LayoutInflater inflater=null;

    private final Activity activity;

    private int activePageIndex;

    private final IActionOneArgs<Integer> onChangeActivePage;
    private final IActionOneArgs<Integer> onZoomPage;

    public PagesMapAdapter(
            Activity activity,
            List<Page> pages,
            int activePageIndex,
            IActionOneArgs<Integer> onChangeActivePage,
            IActionOneArgs<Integer> onZoomPage)
    {
        this.activity = activity;

        this.pages=pages;
        pagesCount = pages.size();
        this.activePageIndex = activePageIndex;

        this.onChangeActivePage = onChangeActivePage;
        this.onZoomPage = onZoomPage;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setActivePageIndex(int activePageIndex)
    {
        this.activePageIndex = activePageIndex;
    }

    public void setParentSize(Size parentSize)
    {
        this.parentSize = parentSize;

        if(parentSize.getWidth() < parentSize.getHeight())
        {
            pagesInRow = PAGES_IN_ROW_VERT;
            rowsCountMin = ROWS_COUNT_MIN_VERT;
        }
        else
        {
            pagesInRow = PAGES_IN_ROW_HORIZ;
            rowsCountMin = ROWS_COUNT_MIN_HORIZ;
        }

        maxPageWidth = parentSize.getWidth()/ pagesInRow;
    }

    @Override
    public int getCount()
    {
        if(pagesCount <= pagesInRow * rowsCountMin)
            return rowsCountMin;                 // Alwais show at least shelfCountMin shelfs + header of bookcase
        else
        {
            int result = pagesCount / pagesInRow;

            if(pagesCount % pagesInRow!=0)          // Not full row
                result++;

            return result;
        }
    }

    /**
     * To prevent white splash when click on shelf
     * @param position
     * @return
     */
    @Override
    public boolean isEnabled(int position)
    {
        return false;
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolders.PagesHolder pageHolder=null;
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.pages_map_list_item, null);

            AbsListView.LayoutParams p = new AbsListView.LayoutParams(parentSize.getWidth(), parentSize.getHeight() / rowsCountMin);
            convertView.setLayoutParams(p);

            pageHolder=new ViewHolders.PagesHolder();
            pageHolder.pagesControl = (LinearLayout) convertView.findViewById(R.id.pageContainer);
            convertView.setTag(pageHolder);
        }
        else
            pageHolder=(ViewHolders.PagesHolder)convertView.getTag();

        pageHolder.pagesControl.removeAllViews();

        List<Page> pagesInRow = getPagesForRow(position);
        int pageNumber=getFirstPageNumberInrow(position);
        for (Page pageInRow : pagesInRow)             // Create comics for shelfs
        {
            pageHolder.pagesControl.addView(new PageControl(
                    activity,
                    pageInRow,
                    maxPageWidth,
                    pageNumber,
                    activePageIndex,
                    onChangeActivePage,
                    onZoomPage));
            pageNumber++;
        }

        return convertView;
    }

    private List<Page> getPagesForRow(int rowIndex)
    {
        ArrayList<Page> result= new ArrayList<>();

        if(pages.size() > 0)
        {
            int firstPagesIndex=rowIndex*pagesInRow;

            int count=0;
            while(firstPagesIndex<pages.size() && count<pagesInRow)
            {
                result.add(pages.get(firstPagesIndex));
                firstPagesIndex++;
                count++;
            }
        }

        return result;
    }

    private int getFirstPageNumberInrow(int rowIndex)
    {
        return (rowIndex*pagesInRow)+1;
    }

    /**
     * Get index of row for index of page
     * @param indexOfPage
     * @return
     */
    public int getRowForPageIndex(int indexOfPage)
    {
        if(indexOfPage< pagesInRow)
            return 0;               // +1 - has header

        return indexOfPage/pagesInRow;
    }
}
