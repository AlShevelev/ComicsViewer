package com.syleiman.comicsviewer.Activities.ViewComics;

/**
 * Holder for vertex information.
 */
class Vertex
{
    public int mColor;
    public float mColorFactor;
    public double mPenumbraX;
    public double mPenumbraY;
    public double mPosX;
    public double mPosY;
    public double mPosZ;
    public double mTexX;
    public double mTexY;

    public Vertex()
    {
        mPosX = mPosY = mPosZ = mTexX = mTexY = 0;
        mColorFactor = 1.0f;
    }

    public void rotateZ(double theta)
    {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        double x = mPosX * cos + mPosY * sin;
        double y = mPosX * -sin + mPosY * cos;
        mPosX = x;
        mPosY = y;
        double px = mPenumbraX * cos + mPenumbraY * sin;
        double py = mPenumbraX * -sin + mPenumbraY * cos;
        mPenumbraX = px;
        mPenumbraY = py;
    }

    public void set(Vertex vertex)
    {
        mPosX = vertex.mPosX;
        mPosY = vertex.mPosY;
        mPosZ = vertex.mPosZ;
        mTexX = vertex.mTexX;
        mTexY = vertex.mTexY;
        mPenumbraX = vertex.mPenumbraX;
        mPenumbraY = vertex.mPenumbraY;
        mColor = vertex.mColor;
        mColorFactor = vertex.mColorFactor;
    }

    public void translate(double dx, double dy)
    {
        mPosX += dx;
        mPosY += dy;
    }
}