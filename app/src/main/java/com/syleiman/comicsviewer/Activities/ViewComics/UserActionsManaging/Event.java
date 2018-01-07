package com.syleiman.comicsviewer.Activities.ViewComics.UserActionsManaging;

import android.graphics.PointF;

/**
 * One event of state machine
 */
public class Event
{
    private final int code;
    /** Code from EventsCodes */
    public int getCode(){ return code; }

    private final PointF[] points;
    /** Fingers points - may be null */
    public PointF[] getPoints() {return points; }

    private float pressure;
    /** Size of finger's spot */
    public float getPressure() {return pressure;}

    public int fingerIndex;
    /** Index of finger in last action (for example - when we up one finger) */
    public int getFingerIndex() { return fingerIndex; }

    public Event(int code, PointF[] points, float pressure, int fingerIndex)
    {
        this.code = code;
        this.points = points;
        this.pressure = pressure;
        this.fingerIndex = fingerIndex;
    }
}