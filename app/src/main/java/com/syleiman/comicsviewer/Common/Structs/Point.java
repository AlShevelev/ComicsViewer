package com.syleiman.comicsviewer.Common.Structs;

public class Point
{
    /**
     * Left coordinate as a portion of total size ([0-1])
     */
    public int left;


    /**
     * Top coordinate as a portion of total size ([0-1])
     */
    public int top;

    public Point(int left, int top)
    {
        this.left = left;
        this.top = top;
    }
}
