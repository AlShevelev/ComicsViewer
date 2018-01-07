package com.syleiman.comicsviewer.Activities.ViewComics.UserActionsManaging;

import android.graphics.PointF;

/**
 * This interface is for class managed by user touches
 */
public interface IUserActionsManaged
{
    void startCurving(PointF point, float pressure);
    void curving(PointF point, float pressure);
    void completeCurving(PointF point, float pressure);
    void cancelCurving(PointF point, float pressure);

    void startResizing();
    void resizing(PointF[] points);
    void completeResizing();

    void startDragging(PointF point);
    void dragging(PointF point);
    void completeDragging(PointF point);

    void showMenu();
}
