package com.syleiman.comicsviewer.Activities.ViewComics.Helpers;

import android.graphics.PointF;

public class PointsHelper
{
    public static float getDistance(PointF p1, PointF p2)
    {
        return (float)Math.sqrt(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2));
    }

    public static float getDistance(PointF[] points)
    {
        if(points==null || points.length==0)
            throw new IllegalArgumentException("'points' can't be empty");

        if(points.length==1)
            return 0f;

        if(points.length==2)
            return getDistance(points[0], points[1]);

        float sum=0f;
        int totalPoints=points.length;

        for(int i=0; i<totalPoints; i++)
            for(int j=0; j<totalPoints; j++)
                if(i != j)
                    sum+=getDistance(points[i], points[j]);             // Average distance

        return sum / (totalPoints * totalPoints - totalPoints);
    }
}
