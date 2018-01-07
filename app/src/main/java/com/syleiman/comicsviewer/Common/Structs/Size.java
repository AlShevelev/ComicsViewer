package com.syleiman.comicsviewer.Common.Structs;

import android.graphics.Bitmap;

/**
 * Size in pixels
 */
public class Size
{
    private int width;
    public int getWidth() { return width; }

    private int height;
    public int getHeight() { return height; }

    public Size(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public boolean isVertical()
    {
        return height > width;
    }

    /**
     * Scaling with saving proportions
     */
    public Size scale(float scaleFactor)
    {
        float tmpWidth = scaleFactor*width;
        float tmpHeight = scaleFactor*height;

        return new Size((int)tmpWidth, (int)tmpHeight);
    }

    /**
     * inscribe one area to another
     */
    public Size inscribe(Size sizeToInscribe)
    {
        float currentWidth=getWidth();
        float currentHeight=getHeight();

        float inscribeWidth=sizeToInscribe.getWidth();
        float inscribeHeight=sizeToInscribe.getHeight();

        if(isVertical())
        {
            float scaleFactor=currentHeight / inscribeHeight;
            float tmpWidth=inscribeWidth * scaleFactor;         // try to inscribe by height
            if(tmpWidth<=currentWidth)
                return new Size((int)tmpWidth, (int)currentHeight);
            else
            {
                scaleFactor=currentWidth / inscribeWidth;               // else try to inscribe by height
                return new Size((int)currentWidth, (int)(inscribeHeight * scaleFactor));
            }
        }
        else
        {
            float scaleFactor=currentWidth / inscribeWidth;
            float tmpHeight=inscribeHeight * scaleFactor;         // try to inscribe by height
            if(tmpHeight<=currentHeight)
                return new Size((int)currentWidth, (int)tmpHeight);
            else
            {
                scaleFactor=currentHeight / inscribeHeight;               // else try to inscribe by height
                return new Size((int)(inscribeWidth * scaleFactor), (int)currentHeight);
            }
        }
    }
}
