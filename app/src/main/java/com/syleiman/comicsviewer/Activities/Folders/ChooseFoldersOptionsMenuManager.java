package com.syleiman.comicsviewer.Activities.Folders;

import android.view.Menu;

import com.syleiman.comicsviewer.Common.OptionsMenuManager;

public class ChooseFoldersOptionsMenuManager extends OptionsMenuManager
{
    public ChooseFoldersOptionsMenuManager(Menu menu)
    {
        super(menu);
    }

    public void setAcceptVisible(boolean visible)
    {
        setVisible(1, visible);
    }
}
