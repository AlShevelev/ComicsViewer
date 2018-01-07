package com.syleiman.comicsviewer.Common.Structs;

/**
 * Area as a portion of total
 */
public class AreaF
{
    private PointF leftTop;
    public PointF getLeftTop() { return leftTop; }

    private SizeRelative size;
    public SizeRelative getSize() { return size; }

    public AreaF(PointF leftTop, SizeRelative size)
    {
        this.leftTop = leftTop;
        this.size = size;
    }

    public Area toArea(Size intSize)
    {
        return new Area(leftTop.toPoint(intSize), size.toSize(intSize));
    }
}