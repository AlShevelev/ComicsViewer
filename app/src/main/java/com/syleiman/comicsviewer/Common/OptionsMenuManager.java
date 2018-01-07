package com.syleiman.comicsviewer.Common;


import android.view.Menu;
import android.view.MenuItem;

public class OptionsMenuManager
{
    private MenuItem[] allMenuItems;

    public OptionsMenuManager(Menu menu)
    {
        allMenuItems=new MenuItem[menu.size()];
        for(int i=0; i< allMenuItems.length; i++)
            allMenuItems[i]=menu.getItem(i);
    }

    public void setVisible(boolean visible)
    {
        for(int i=0; i< allMenuItems.length; i++)
            allMenuItems[i].setVisible(visible);
    }

    protected void setVisible(int menuItemIndex, boolean visible)
    {
        allMenuItems[menuItemIndex].setVisible(visible);
    }
}
