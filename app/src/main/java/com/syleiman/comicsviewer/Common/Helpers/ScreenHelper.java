package com.syleiman.comicsviewer.Common.Helpers;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

import com.syleiman.comicsviewer.Common.Structs.Size;

/**
 * All about screen and pixels
 */
public class ScreenHelper
{
    /**
     * Get size of device screen in pixels
     * @param context
     * @return
     */
    public static Size getScreenSize(Activity context)
    {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();               // Get size of screen
        display.getSize(size);

        return new Size(size.x, size.y);
    }

    /**
     * Get size of activity client area
     * @param context
     * @return
     */
    public static Size getClientSize(Activity context)
    {
        Size screenSize=getScreenSize(context);

        ActionBar ab=context.getActionBar();
        if(ab==null)
            return screenSize;

        return new Size(screenSize.getWidth(), screenSize.getHeight()-ab.getHeight());
    }

    /**
     * Convert DP units to PX
     * @param dp
     * @param context
     * @return
     */
    public static int dpToPx(int dp, Activity context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * Convert PX units to DP
     * @param px
     * @param context
     * @return
     */
    public static int pxToDp(int px, Activity context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
