package com.syleiman.comicsviewer.Activities.Main;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.ComicsViewMode;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;


/**
 * Handle change mode of comics view
 */
public class ChangeModeHandler implements IChangeModeHandlerView
{
    private ComicsViewMode viewMode;
    private IActionOneArgs<ComicsViewMode> modeChanged;         // Mode changed callback

    private final int activeColor;
    private final int inactiveColor;

    public ChangeModeHandler(ComicsViewMode viewMode, IActionOneArgs<ComicsViewMode> modeChanged)
    {
        this.viewMode = viewMode;
        this.modeChanged = modeChanged;

        Resources resources= App.getContext().getResources();
        activeColor = resources.getColor(R.color.bookcase_header_label_active);
        inactiveColor = resources.getColor(R.color.bookcase_header_label_inactive);
    }

    public ComicsViewMode getViewMode()
    {
        return viewMode;
    }

    public void setViewMode(ComicsViewMode viewMode)
    {
        this.viewMode = viewMode;
    }

    private void showMode(TextView allComicsControl, TextView recentComicsControl)
    {
        allComicsControl.setTextColor(viewMode==ComicsViewMode.All ? activeColor : inactiveColor);
        recentComicsControl.setTextColor(viewMode==ComicsViewMode.All ? inactiveColor : activeColor);
    }

    @Override
    public void onAllComicsClicked(View allComicsControl, View recentComicsControl)
    {
        viewMode=ComicsViewMode.All;

        showMode((TextView)allComicsControl, (TextView)recentComicsControl);
        modeChanged.process(viewMode);
    }

    @Override
    public void onRecentComicsClicked(View allComicsControl, View recentComicsControl)
    {
        viewMode=ComicsViewMode.Recent;

        showMode((TextView)allComicsControl, (TextView)recentComicsControl);
        modeChanged.process(viewMode);
    }

    @Override
    public void initState(View allComicsControl, View recentComicsControl)
    {
         showMode((TextView)allComicsControl, (TextView)recentComicsControl);
    }
}