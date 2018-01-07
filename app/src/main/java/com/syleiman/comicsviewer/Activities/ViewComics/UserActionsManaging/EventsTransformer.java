package com.syleiman.comicsviewer.Activities.ViewComics.UserActionsManaging;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.syleiman.comicsviewer.Activities.ViewComics.Helpers.PointsHelper;
import com.syleiman.comicsviewer.Common.Helpers.ScreenHelper;
import com.syleiman.comicsviewer.Common.Structs.Area;
import com.syleiman.comicsviewer.Common.Structs.Point;
import com.syleiman.comicsviewer.Common.Structs.Size;

/**
 * Transform device's events to state machine's events
 */
public class EventsTransformer
{
    private Event lastMoveEvent=null;
    private final Area menuArea;                // Menu hit area

    public EventsTransformer(Size screenSize)
    {
        menuArea=new Area(
                new Point((int)(screenSize.getWidth()*0.9f), 0),
                new Size((int)(screenSize.getWidth()*0.1f), (int)(screenSize.getHeight()*0.1f)));    }

    /**
     * Reset internal state
     */
    public void reset()
    {
        lastMoveEvent=null;
    }

    public Event transform(MotionEvent deviceEvent)
    {
        int action=deviceEvent.getActionMasked();
        int pointersTotal=deviceEvent.getPointerCount();

        PointF[] points=null;
        if(pointersTotal>0)
        {
            points=new PointF[pointersTotal];
            for(int i=0; i<pointersTotal; i++)
                points[i]=new PointF(deviceEvent.getX(i), deviceEvent.getY(i));
        }

        switch (action)
        {
            case MotionEvent.ACTION_DOWN: return getActionDownEvent(points, deviceEvent.getPressure(), deviceEvent);
            case MotionEvent.ACTION_POINTER_DOWN: return getNotMoveEvent(EventsCodes.NextFingerDown, points, deviceEvent.getPressure(), deviceEvent);

            case MotionEvent.ACTION_MOVE: return getMoveEvent(points, deviceEvent.getPressure(), deviceEvent);

            case MotionEvent.ACTION_UP: return getNotMoveEvent(EventsCodes.OneFingerUp, points, deviceEvent.getPressure(), deviceEvent);
            case MotionEvent.ACTION_POINTER_UP: return getNotMoveEvent(EventsCodes.NextFingerUp, points, deviceEvent.getPressure(), deviceEvent);

            case MotionEvent.ACTION_OUTSIDE: return getNotMoveEvent(EventsCodes.Cancel, points, deviceEvent.getPressure(), deviceEvent);
            case MotionEvent.ACTION_CANCEL: return getNotMoveEvent(EventsCodes.Cancel, points, deviceEvent.getPressure(), deviceEvent);
        }

        return new Event(EventsCodes.None, points, deviceEvent.getPressure(), deviceEvent.getActionIndex());
    }

    private Event getNotMoveEvent(int code, PointF[] points, float pressure, MotionEvent deviceEvent)
    {
        lastMoveEvent = null;

        return new Event(code, points, pressure, deviceEvent.getActionIndex());
    }

    private Event getActionDownEvent(PointF[] points, float pressure, MotionEvent deviceEvent)
    {
        Point lastPoint = new Point((int)points[0].x, (int)points[0].y);

        int code=menuArea.isHit(lastPoint) ? EventsCodes.OneFingerDownInMenuArea : EventsCodes.OneFingerDown;
        return new Event(code, points, pressure, deviceEvent.getActionIndex());
    }

    private Event getMoveEvent(PointF[] points, float pressure, MotionEvent deviceEvent)
    {
        Event currentMoveEvent=new Event(EventsCodes.Move, points, pressure, deviceEvent.getActionIndex());

/*
        if(areEquals(currentMoveEvent, lastMoveEvent))
            return new Event(EventsCodes.None, points, pressure);         // Cut equals Move-events to avoid noising
        else
        {
            lastMoveEvent = currentMoveEvent;
            return currentMoveEvent;
        }
*/
        return currentMoveEvent;
    }

    private boolean areEquals(Event e1, Event e2)
    {
        if(e1==null || e2==null)
            return false;

        if(e1.getCode()!=e2.getCode())
            return false;

        PointF[] e1Points = e1.getPoints();
        PointF[] e2Points = e2.getPoints();

        if(e1Points==null && e2Points==null)
            return true;

        if(e1Points!=null && e2Points!=null)
        {
            if(e1Points.length != e2Points.length)
                return false;

            final float nearThreshold=2.5f;

            if(e1Points.length == 1)                // One-point event - calculate distance between events points
            {
                if(PointsHelper.getDistance(e1Points[0], e2Points[0]) < nearThreshold)
                    return true;
            }
            else                    // Multi-points event - calculate distance between points in every event
            {
                float e1PointsDistance= PointsHelper.getDistance(e1Points);
                float e2PointsDistance= PointsHelper.getDistance(e2Points);

                if(Math.abs(e1PointsDistance-e2PointsDistance) < nearThreshold)      // Merge events with near distances
                    return true;
            }

        }

        return false;
    }

    public void logTouchEvent(MotionEvent me)
    {
        int action=me.getActionMasked();
        int pointersTotal=me.getPointerCount();

        float[] x = new float[pointersTotal];
        float[] y = new float[pointersTotal];
        float[] id = new float[pointersTotal];

        for(int i=0; i<pointersTotal; i++)
        {
            x[i]=me.getX(i);
            y[i]=me.getY(i);
            id[i]=me.getPointerId(i);
        }

        String logData="Action: ";

        switch (action)
        {                       // Down -> [Move | PointerDown]* -> [PointerUp]* -> [Move]* -> Up
            case MotionEvent.ACTION_DOWN: logData+="Down"; break;
            case MotionEvent.ACTION_MOVE: logData+="Move"; break;                   // Must merge Move events by position
            case MotionEvent.ACTION_POINTER_DOWN: logData+="Pointer Down"; break;
            case MotionEvent.ACTION_UP: logData+="Up"; break;
            case MotionEvent.ACTION_POINTER_UP: logData+="Pointer Up"; break;
            case MotionEvent.ACTION_OUTSIDE: logData+="Outside"; break;
            case MotionEvent.ACTION_CANCEL: logData+="Cancel"; break;
        }

        logData+="; pointersTotal: "+pointersTotal;

        for(int i=0; i<pointersTotal; i++)
        {
            logData+="; [pointerIndex: "+i;
            logData+="; id: "+id[i];
            logData+="; value1: "+x[i];
            logData+="; value2: "+y[i]+"]";
        }

        Log.d("TOUCH_EV", logData);
    }

    public void logTouchEvent(Event e)
    {
        String logData="Action: ";

        switch (e.getCode())
        {
            case EventsCodes.NextFingerUp: logData+="NextFingerUp"; break;
            case EventsCodes.OneFingerDown: logData+="OneFingerDown"; break;
            case EventsCodes.None: logData+="None"; break;
            case EventsCodes.Move: logData+="Move"; break;
            case EventsCodes.OneFingerUp: logData+="OneFingerUp"; break;
            case EventsCodes.Cancel: logData+="Cancel"; break;
            case EventsCodes.NextFingerDown: logData+="NextFingerDown"; break;
        }

        PointF[] points = e.getPoints();
        logData+="; pointersTotal: "+ points!=null ? points.length : 0;

        for(int i=0; i<points.length; i++)
        {
            logData+="; [value1: "+points[i].x;
            logData+="; value2: "+points[i].y+"]";
        }

        Log.d("PROCESSED_EVENT", logData);
    }
}