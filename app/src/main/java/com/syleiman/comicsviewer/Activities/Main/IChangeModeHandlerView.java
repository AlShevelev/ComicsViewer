package com.syleiman.comicsviewer.Activities.Main;

import android.view.View;

public interface IChangeModeHandlerView
{
    void onAllComicsClicked(View allComicsControl, View recentComicsControl);
    void onRecentComicsClicked(View allComicsControl, View recentComicsControl);

    void initState(View allComicsControl, View recentComicsControl);
}
