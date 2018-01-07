package com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations;

import android.app.Activity;

import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.IOneComicsActivity;

/**
 * Base class for operations with comics
 */
abstract class ComicsOperationBase
{
    protected final IOneComicsActivity uiMethods;
    protected final Activity context;

    public ComicsOperationBase(IOneComicsActivity activity)
    {
        uiMethods = activity;
        context = (Activity)activity;
    }
}
