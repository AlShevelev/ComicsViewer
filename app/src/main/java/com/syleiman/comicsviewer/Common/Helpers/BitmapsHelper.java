package com.syleiman.comicsviewer.Common.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.Structs.Area;
import com.syleiman.comicsviewer.Common.Structs.AreaF;
import com.syleiman.comicsviewer.Common.Structs.Size;

import java.io.IOException;

public class BitmapsHelper
{
    /**
     * Is bitmap dark?
     * Idea (but not code :) ) was got from here: https://gist.github.com/brwnx/191c79b6c2b3befbfc7d
     * @return BitmapDarkRate
     */
    public static BitmapDarkRate isDark(Bitmap bitmap, AreaF areaToCheck)
    {
        Area checkedArea=areaToCheck.toArea(new Size(bitmap.getWidth(), bitmap.getHeight()));

        boolean dark=false;

        float totalPixels = checkedArea.getSize().getWidth()*checkedArea.getSize().getHeight();
        float darkThreshold = totalPixels*0.5f;
        int darkPixels=0;

        int[] pixels = new int[checkedArea.getSize().getWidth()*checkedArea.getSize().getHeight()];
        bitmap.getPixels(
                pixels,
                0,
                checkedArea.getSize().getWidth(),
                checkedArea.getLeftTop().left,
                checkedArea.getLeftTop().top,
                checkedArea.getSize().getWidth(),
                checkedArea.getSize().getHeight());

        for(int pixel : pixels)
        {
            int color = pixel;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            double luminance = (0.299*r+0.0f + 0.587*g+0.0f + 0.114*b+0.0f);

            if (luminance<150)
                darkPixels++;
        }

/*
        if(Math.abs(darkPixels-darkThreshold)<totalPixels*0.15f)            // So-so
            return BitmapDarkRate.Gray;
*/

        if (darkPixels >= darkThreshold)
            return BitmapDarkRate.Dark;
        return BitmapDarkRate.Light;
    }

    public static Bitmap loadFromFile(String fullFileName)
    {
        return BitmapFactory.decodeFile(fullFileName);
    }

    public static Bitmap loadFromRaw(int rawResourceId)
    {
        return BitmapFactory.decodeStream(App.getContext().getResources().openRawResource(rawResourceId));
    }

    public static Bitmap scale(Bitmap bmp, Size targetSize)
    {
        return Bitmap.createScaledBitmap(bmp, targetSize.getWidth(), targetSize.getHeight(), false);
    }
}
