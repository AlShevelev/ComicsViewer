package com.syleiman.comicsviewer.Common.Structs;

/**
 * Created by shevelev on 06.10.2015.
 */
public class Area
{
    private Point leftTop;
    public Point getLeftTop() { return leftTop; }

    private Size size;
    public Size getSize() { return size; }

    public Area(Point leftTop, Size size)
    {
        this.leftTop = leftTop;
        this.size = size;
    }

    /**
     * Is point inside an area?
     */
    public boolean isHit(Point testedPoint)
    {
        Point rightBottom=new Point(leftTop.left+size.getWidth(), leftTop.top+size.getHeight());

        return testedPoint.left>=leftTop.left &&
                testedPoint.left<=rightBottom.left &&
                testedPoint.top>=leftTop.top &&
                testedPoint.top<=rightBottom.top;
    }
}
