package com.syleiman.comicsviewer.ComicsWorkers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.Helpers.BitmapsHelper;
import com.syleiman.comicsviewer.Common.Helpers.ScreenHelper;
import com.syleiman.comicsviewer.Common.Structs.Size;

/**
 * Crete cover logic
 */
public class CoverCreator
{
    /**
     * Create cover bitmap from file
     * @param fullNameToFile - path to file with source bitmap
     * @return
     */
    public static Bitmap create(String fullNameToFile, IPreviewCreator previewCreator)
    {
        Bitmap coverBitmapScaled = previewCreator.createPreview(fullNameToFile);           // Scale source bitmap
        return drawCover(coverBitmapScaled);          // and setDiskItems cover
    }

    /**
     * Create cover bitmap from slaledBitmap
     * @param sourceBitmapScaled - scaled bitmap
     * @return
     */
    public static Bitmap create(Bitmap sourceBitmapScaled, IPreviewCreator previewCreator)
    {
        Bitmap coverBitmapScaled = previewCreator.createPreview(sourceBitmapScaled);           // Scale source bitmap
        return drawCover(coverBitmapScaled);
    }

    /**
     *  Calculate size of shadow (value1 is same as value2)
     */
    private static int calculateShadowSize(Size bitmapSize)
    {
        float result=(bitmapSize.getWidth()+bitmapSize.getHeight())/(2f*15.48f);

        float shadowScaleFactor=1.2f;

        return (int)(result*shadowScaleFactor);        // we must *shadowScaleFactor becouse cubic curve is used to draw shadod
    }

    /**
     * Calculate total size of cover (with bitmap and shadow)
     * @param bitmap
     * @return
     */
    private static Size calculateCoverSize(Bitmap bitmap, int shadowSize)
    {
        int resultWidth= bitmap.getWidth()+shadowSize/2;
        int resultHeight= bitmap.getHeight();

        if(resultWidth>resultHeight)
            resultHeight=resultWidth;

        return new Size(resultWidth, resultHeight);
    }

    /**
     *  Draw cover
     * @param bitmap bitmap to draw
     * @return
     */
    private static Bitmap drawCover(Bitmap bitmap)
    {
        Size bitmapSize=new Size(bitmap.getWidth(), bitmap.getHeight());
        int shadowSize=calculateShadowSize(bitmapSize);
        Size coverSize=calculateCoverSize(bitmap, shadowSize);

        Bitmap bmOverlay = Bitmap.createBitmap(coverSize.getWidth(), coverSize.getHeight(), Bitmap.Config.ARGB_8888);
        bmOverlay.eraseColor(Color.TRANSPARENT);            // Make transparent

        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(bitmap, 0, coverSize.getHeight() - bitmap.getHeight(), null);               // Place bitmap on bottom
        drawShadow(canvas, bitmapSize, coverSize, shadowSize);         // Draw shadow

        return bmOverlay;
    }

    private static void drawShadow(Canvas canvas, Size bitmapSize, Size coverSize, int shadowSize)
    {
        Paint paint = new Paint();          // draw shadow - in separate method

        paint.setColor(App.getContext().getResources().getColor(R.color.bookcase_cover_shadow));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Point a = new Point(bitmapSize.getWidth(), coverSize.getHeight());
        Point b = new Point(a.x+shadowSize, a.y-shadowSize);
        Point c = new Point(a.x, coverSize.getHeight() - bitmapSize.getHeight());

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(a.x, a.y);
        path.quadTo(b.x, b.y, c.x, c.y);            // Use cubic curve to avoid sharp angle of shadow (it's to rough)
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
