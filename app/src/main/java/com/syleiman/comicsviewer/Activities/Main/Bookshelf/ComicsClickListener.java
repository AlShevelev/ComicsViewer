package com.syleiman.comicsviewer.Activities.Main.Bookshelf;

import android.view.MotionEvent;
import android.view.View;

import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;

public class ComicsClickListener implements View.OnClickListener
{
    private long dbComicsId;

    private IActionOneArgs<Long> onComicsChoosen;

    public ComicsClickListener(long dbComicsId, IActionOneArgs<Long> onComicsChoosen)
    {
        this.dbComicsId = dbComicsId;
        this.onComicsChoosen = onComicsChoosen;
    }

    @Override
    public void onClick(View v)
    {
        onComicsChoosen.process(dbComicsId);
    }
}
