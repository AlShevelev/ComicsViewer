package com.syleiman.comicsviewer.Activities;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Turn on/off user actios
 */
public class UserActionsManager
{
    private boolean actionsBlocked =false;         // Flag for blocking on-screen buttons

    private final Activity activity;

    public UserActionsManager(Activity activity)
    {
        this.activity = activity;
    }

    public boolean isActionsBlocked()
    {
        return actionsBlocked;
    }

    public void lock()
    {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        actionsBlocked=true;
    }

    public void unlock()
    {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        actionsBlocked=false;
    }
}
