package com.syleiman.comicsviewer.Activities.ComicsCreation;

import android.view.Menu;

import com.syleiman.comicsviewer.Common.OptionsMenuManager;

public class SortPagesMenuManager extends OptionsMenuManager
{
    public SortPagesMenuManager(Menu menu)
    {
        super(menu);
    }

    public void setAcceptVisible(boolean visible)
    {
        setVisible(0, visible);
    }
}
