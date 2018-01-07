package com.syleiman.comicsviewer;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;

/**
 * Application configuration class
 */
public class App extends Application
{
    private static Context context;

    public void onCreate()
    {
        super.onCreate();

        ActiveAndroid.initialize(this);

        App.context = getApplicationContext();
    }

    public static Context getContext()
    {
        return App.context;
    }

    public static String getResourceString(int resourceId)
    {
        return getContext().getResources().getString(resourceId);
    }
}
