package com.syleiman.comicsviewer.Activities.PagesMap;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.ListViewHelper;
import com.syleiman.comicsviewer.Common.Structs.Size;
import com.syleiman.comicsviewer.Dal.Dto.Comics;
import com.syleiman.comicsviewer.Dal.Dto.Page;

import java.util.List;

public class PagesMapView extends RelativeLayout
{
    private Size parentSize;

    private PagesMapAdapter pagesMapAdapter;

    private int activePageIndex;
    private final List<Page> pages;

    private final ListView pagesList;

    private final IActionOneArgs<Integer> onChangeActivePage;
    private final IActionOneArgs<Integer> onZoomPage;

    public PagesMapView(
            Context context,
            Comics comics,
            List<Page> pages,
            IActionOneArgs<Integer> onChangeActivePage,
            IActionOneArgs<Integer> onZoomPage)
    {
        super(context);

        this.activePageIndex = comics.lastViewedPageIndex;
        this.pages = pages;
        this.onChangeActivePage = onChangeActivePage;
        this.onZoomPage = onZoomPage;

        inflate(context, R.layout.activity_pages_map, this);

        pagesList = (ListView)findViewById(R.id.pagesList);
        pagesList.setDivider(null);
        pagesList.setDividerHeight(0);
    }

    public Size getSize()
    {
        return new Size(getWidth(), getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        parentSize=new Size(w, h);

        pagesMapAdapter = new PagesMapAdapter((Activity)getContext(), pages, activePageIndex, onChangeActivePage, onZoomPage);
        pagesMapAdapter.setParentSize(parentSize);
        pagesList.setAdapter(pagesMapAdapter);
        scrollToPage(activePageIndex);
    }

    public void scrollToPage(int pageIndex)
    {
        if(pagesList.canScrollVertically(1))
        {
            int indexOfRow=pagesMapAdapter.getRowForPageIndex(pageIndex);
            pagesList.setSelection(indexOfRow);
        }
    }

    /**
     * Update controls after active page changed
     */
    public void updatePageControls(int newActivePageIndex)
    {
        pagesMapAdapter.setActivePageIndex(newActivePageIndex);

        int oldActivePageRow=pagesMapAdapter.getRowForPageIndex(activePageIndex);
        int newActivePageRow=pagesMapAdapter.getRowForPageIndex(newActivePageIndex);

        ListViewHelper.invalidateListItem(oldActivePageRow, pagesList);
        if(oldActivePageRow!=newActivePageRow)
            ListViewHelper.invalidateListItem(newActivePageRow, pagesList);

        activePageIndex = newActivePageIndex;
    }
}
