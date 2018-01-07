package com.syleiman.comicsviewer.Common.Helpers;

import android.view.Gravity;
import android.widget.Toast;

import com.syleiman.comicsviewer.App;

/**
 * Helper function for toasts
 */
public class ToastsHelper
{
    public enum Duration
    {
        Long,
        Short
    }

    public enum Position
    {
        Bottom,
        Center,
        Top
    }

    public static void Show(CharSequence text, Duration duration, Position position)
    {
        Toast toast=Toast.makeText(App.getContext(), text, duration==Duration.Long ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);

        int gravity=0;
        switch (position){
            case Bottom: gravity= Gravity.BOTTOM; break;
            case Center: gravity= Gravity.CENTER; break;
            case Top: gravity= Gravity.TOP; break;
        }

        toast.setGravity(gravity, 0, 0);

        toast.show();
    }

    public static void Show(int textResId,  Duration duration, Position position)
    {
        Show(App.getContext().getResources().getText(textResId), duration, position);
    }

    public static void Show(int textResId,  Position position) {
        Show(textResId, Duration.Short, position);
    }

    public static void Show(CharSequence text,  Position position) {
        Show(text, Duration.Short, position);
    }
}
