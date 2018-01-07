package com.syleiman.comicsviewer.Activities.ViewComics;

import android.graphics.RectF;
import android.util.Log;

import com.syleiman.comicsviewer.Common.Structs.Pair;
import com.syleiman.comicsviewer.Common.Structs.SizeF;

public class DraggingState
{
    private Pair<Float> startDrag;        // Stating dragging factor by X and Y
    private Pair<Float> currentDragging;      // Current dragging factor by X and Y

    private RectF draggingBorders;                  // Absolute
    private RectF currentDraggingBorders;           // Based on the current scale

    private float minMargin;
    private float maxMargin;
    private Margins currentMargins;

    private float unitsInPixels;            // How many units (OGL) in one pixel

    public DraggingState(float minMargin, float maxMargin)
    {
        startDrag=new Pair<>(0f, 0f);
        currentDragging=new Pair<>(0f, 0f);

        this.minMargin = minMargin;
        this.maxMargin = maxMargin;
    }

    private Pair<Float> convertPixelsToUnits(float distanceInPixelsX, float distanceInPixelsY)
    {
        return new Pair<>(distanceInPixelsX * unitsInPixels, -distanceInPixelsY * unitsInPixels);
    }

    public Pair<Float> getDraggingFactor(boolean checkBorders)
    {
        Pair<Float> result=new Pair<>(startDrag.value1 +currentDragging.value1, startDrag.value2 +currentDragging.value2);

        if(checkBorders)
        {
            Log.d("DRAGGING", String.format("draggingBorders (ltrb): %1f; %2f; %3f; %4f",
                    currentDraggingBorders.left, currentDraggingBorders.top, currentDraggingBorders.right, currentDraggingBorders.bottom));

            if (result.value1 > currentDraggingBorders.right)                // Checking borders
            {
                currentDragging.value1 -= (result.value1 - currentDraggingBorders.right);
                result.value1 = currentDraggingBorders.right;
            }
            else if (result.value1 < currentDraggingBorders.left)
            {
                currentDragging.value1 += (result.value1 - currentDraggingBorders.left);
                result.value1 = currentDraggingBorders.left;
            }

            if (result.value2 > currentDraggingBorders.top)
            {
                currentDragging.value2 -= (result.value2 - currentDraggingBorders.top);
                result.value2 = currentDraggingBorders.top;
            }
            else if (result.value2 < currentDraggingBorders.bottom)
            {
                currentDragging.value2 += (result.value2 - currentDraggingBorders.bottom);
                result.value2 = currentDraggingBorders.bottom;
            }
        }

        Log.d("DRAGGING", String.format("draggingBorders result (xy): %1f; %2f;", result.value1, result.value2));

        return result;
    }

    public void startDragging()
    {
        float oneMargin=currentMargins.getLeft();
        float scaleFactor=(oneMargin-maxMargin)/(minMargin-maxMargin);

        currentDraggingBorders=new RectF(
                draggingBorders.left*scaleFactor,
                draggingBorders.top*scaleFactor,
                draggingBorders.right*scaleFactor,
                draggingBorders.bottom*scaleFactor);
    }

    /**
     *
     * @return Dragging factor
     */
    public Pair<Float> processDragging(float distanceInPixelsX, float distanceInPixelsY)
    {
        currentDragging = convertPixelsToUnits(distanceInPixelsX, distanceInPixelsY);
        return getDraggingFactor(true);
    }

    public void completeDragging()
    {
        startDrag = getDraggingFactor(true);        // startDrag + currentDragging (getDraggingFactor)
        currentDragging=new Pair<>(0f, 0f);
    }

    public Pair<Float> reset()
    {
        startDrag=new Pair<>(0f, 0f);
        currentDragging=new Pair<>(0f, 0f);
        return getDraggingFactor(false);
    }

    public void setViewInfo(RendererViewInfo viewInfo)
    {
        RectF viewRect = viewInfo.getViewRect();
        SizeF viewAreaSize = viewInfo.getViewAreaSize();
        this.unitsInPixels = ((Math.abs(viewRect.width())/viewAreaSize.width) + (Math.abs(viewRect.height())/viewAreaSize.height))/2f;

        this.draggingBorders = viewRect;
    }

    public void setCurrentMargins (Margins currentMargins)
    {
        this.currentMargins = currentMargins;
    }
}