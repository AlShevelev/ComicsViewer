package com.syleiman.comicsviewer.Activities.ViewComics;

public class Margins
{
    private float left;
    public float getLeft() { return left; }
    public void setLeft(float value) { left=value; }

    private float top;
    public float getTop() { return top; }
    public void setTop(float value) { top=value; }

    private float right;
    public float getRight() { return right; }
    public void setRight(float value) { right=value; }

    private float bottom;
    public float getBottom() { return bottom; }
    public void setBottom(float value) { bottom=value; }

    public Margins(float left, float top, float right, float bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
