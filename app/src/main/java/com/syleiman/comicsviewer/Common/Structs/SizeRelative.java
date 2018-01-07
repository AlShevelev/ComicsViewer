package com.syleiman.comicsviewer.Common.Structs;

/**
 * Size as a portion of total size
 * 0:0 - is a zero size;
 * 1:1 - is a full size
 */
public class SizeRelative extends FloatStructBase
{
    private float width;
    public float getWidth() { return width; }

    private float height;
    public float getHeight() { return height; }

    public SizeRelative(float width, float height)
    {
        this.width = width;
        this.height = height;
    }

    public Size toSize(Size intSize)
    {
        return new Size(floatToInt(width, intSize.getWidth()),floatToInt(height, intSize.getHeight()));
    }

    public SizeRelative scale(float scaleFactor)
    {
        return new SizeRelative(width*scaleFactor, height*scaleFactor);
    }
}
