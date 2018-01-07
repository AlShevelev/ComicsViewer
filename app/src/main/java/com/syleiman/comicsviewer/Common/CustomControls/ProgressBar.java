package com.syleiman.comicsviewer.Common.CustomControls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.App;

/**
 * Custom progress bar
 */
public class ProgressBar extends LinearLayout
{
    private TextView text;

    public ProgressBar(Context context)
    {
        super(context);
        init(context);
    }

    public ProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        inflate(context, R.layout.progress_bar_control, this);
        text = (TextView)findViewById(R.id.progressControlText);
    }

    public void show()
    {
        setVisibility(View.VISIBLE);
    }

    public void hide()
    {
        setVisibility(View.INVISIBLE);
    }

    public void setText(String text)
    {
        this.text.setText(text);
    }
}
