package com.syleiman.comicsviewer.Activities.ViewComics.UserActionsManaging;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.syleiman.comicsviewer.Common.Structs.Size;

public class UserActionManager
{
    private int currentState;
    private EventsTransformer eventsTransformer;

    ITransitionFunc[][] transitionsMatrix;           // Rows - states, cols - event's codes, cells - transition actions

    private final IUserActionsManaged managedObject;

    public UserActionManager(IUserActionsManaged managedObject, Size screenSize)
    {
        this.managedObject = managedObject;

        currentState=StatesCodes.Init;
        eventsTransformer = new EventsTransformer(screenSize);

        transitionsMatrix=new ITransitionFunc[6][8];
        transitionsMatrix[StatesCodes.Init][EventsCodes.None]=(event, viewStateCode)-> doNothing(StatesCodes.Init);
        transitionsMatrix[StatesCodes.Init][EventsCodes.OneFingerDown]=(event, viewStateCode)-> fromInitOnOneFingerDown(event.getPoints(), event.getPressure(), viewStateCode);
        transitionsMatrix[StatesCodes.Init][EventsCodes.NextFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.Init);
        transitionsMatrix[StatesCodes.Init][EventsCodes.Move]=(event, viewStateCode)-> doNothing(StatesCodes.Init);
        transitionsMatrix[StatesCodes.Init][EventsCodes.NextFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.Init);
        transitionsMatrix[StatesCodes.Init][EventsCodes.OneFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.Init);
        transitionsMatrix[StatesCodes.Init][EventsCodes.Cancel]=(event, viewStateCode)-> doNothing(StatesCodes.Init);
        transitionsMatrix[StatesCodes.Init][EventsCodes.OneFingerDownInMenuArea]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);

        transitionsMatrix[StatesCodes.Final][EventsCodes.None]=(event, viewStateCode)-> doNothing(StatesCodes.Final);
        transitionsMatrix[StatesCodes.Final][EventsCodes.OneFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.Final);
        transitionsMatrix[StatesCodes.Final][EventsCodes.NextFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.Final);
        transitionsMatrix[StatesCodes.Final][EventsCodes.Move]=(event, viewStateCode)-> doNothing(StatesCodes.Final);
        transitionsMatrix[StatesCodes.Final][EventsCodes.NextFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.Final);
        transitionsMatrix[StatesCodes.Final][EventsCodes.OneFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.Final);
        transitionsMatrix[StatesCodes.Final][EventsCodes.Cancel]=(event, viewStateCode)-> doNothing(StatesCodes.Final);
        transitionsMatrix[StatesCodes.Final][EventsCodes.OneFingerDownInMenuArea]=(event, viewStateCode)-> doNothing(StatesCodes.Final);

        transitionsMatrix[StatesCodes.Curving][EventsCodes.None]=(event, viewStateCode)-> doNothing(StatesCodes.Curving);
        transitionsMatrix[StatesCodes.Curving][EventsCodes.OneFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.Curving);
        transitionsMatrix[StatesCodes.Curving][EventsCodes.NextFingerDown]=(event, viewStateCode)-> cancelCurving(event.getPoints(), event.getPressure());
        transitionsMatrix[StatesCodes.Curving][EventsCodes.Move]=(event, viewStateCode)-> processCurving(event.getPoints(), event.getPressure());
        transitionsMatrix[StatesCodes.Curving][EventsCodes.NextFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.Curving);
        transitionsMatrix[StatesCodes.Curving][EventsCodes.OneFingerUp]=(event, viewStateCode)-> completeCurving(event.getPoints(), event.getPressure());
        transitionsMatrix[StatesCodes.Curving][EventsCodes.Cancel]=(event, viewStateCode)-> completeCurving(event.getPoints(), event.getPressure());
        transitionsMatrix[StatesCodes.Curving][EventsCodes.OneFingerDownInMenuArea]=(event, viewStateCode)-> doNothing(StatesCodes.Curving);

        transitionsMatrix[StatesCodes.Resizing][EventsCodes.None]=(event, viewStateCode)-> doNothing(StatesCodes.Resizing);
        transitionsMatrix[StatesCodes.Resizing][EventsCodes.OneFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.Resizing);
        transitionsMatrix[StatesCodes.Resizing][EventsCodes.NextFingerDown]=(event, viewStateCode)-> processResizing(event.getPoints());
        transitionsMatrix[StatesCodes.Resizing][EventsCodes.Move]=(event, viewStateCode)-> processResizing(event.getPoints());
        transitionsMatrix[StatesCodes.Resizing][EventsCodes.NextFingerUp]=(event, viewStateCode)-> processResizingOneFingerUp(event.getPoints(), viewStateCode, event.getFingerIndex());
        transitionsMatrix[StatesCodes.Resizing][EventsCodes.OneFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.Resizing);
        transitionsMatrix[StatesCodes.Resizing][EventsCodes.Cancel]=(event, viewStateCode)-> doNothing(StatesCodes.Resizing);
        transitionsMatrix[StatesCodes.Resizing][EventsCodes.OneFingerDownInMenuArea]=(event, viewStateCode)-> doNothing(StatesCodes.Resizing);

        transitionsMatrix[StatesCodes.Dragging][EventsCodes.None]=(event, viewStateCode)-> doNothing(StatesCodes.Dragging);
        transitionsMatrix[StatesCodes.Dragging][EventsCodes.OneFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.Dragging);
        transitionsMatrix[StatesCodes.Dragging][EventsCodes.NextFingerDown]=(event, viewStateCode)-> startResizing(event.getPoints());
        transitionsMatrix[StatesCodes.Dragging][EventsCodes.Move]=(event, viewStateCode)-> processDragging(event.getPoints());
        transitionsMatrix[StatesCodes.Dragging][EventsCodes.NextFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.Dragging);
        transitionsMatrix[StatesCodes.Dragging][EventsCodes.OneFingerUp]=(event, viewStateCode)-> processFromDraggingToFinal(event.getPoints());
        transitionsMatrix[StatesCodes.Dragging][EventsCodes.Cancel]=(event, viewStateCode)-> processFromDraggingToFinal(event.getPoints());
        transitionsMatrix[StatesCodes.Dragging][EventsCodes.OneFingerDownInMenuArea]=(event, viewStateCode)-> doNothing(StatesCodes.Dragging);

        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.None]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);
        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.OneFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);
        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.NextFingerDown]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);
        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.Move]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);
        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.NextFingerUp]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);
        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.OneFingerUp]=(event, viewStateCode)-> showMenu();
        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.Cancel]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);
        transitionsMatrix[StatesCodes.MenuMode][EventsCodes.OneFingerDownInMenuArea]=(event, viewStateCode)-> doNothing(StatesCodes.MenuMode);
    }

    private int fromInitOnOneFingerDown(PointF[] points, float pressure, ViewStateCodes viewStateCode)
    {
        if(viewStateCode== ViewStateCodes.NotResized)
        {
            managedObject.startCurving(points[0], pressure);
            return StatesCodes.Curving;
        }
        managedObject.startDragging(points[0]);
        return StatesCodes.Dragging;
    }

    private int processCurving(PointF[] points, float pressure)
    {
        managedObject.curving(points[0], pressure);
        return StatesCodes.Curving;
    }

    private int completeCurving(PointF[] points, float pressure)
    {
        managedObject.completeCurving(points[0], pressure);
        return StatesCodes.Final;
    }

    private int cancelCurving(PointF[] points, float pressure)
    {
        managedObject.cancelCurving(points[0], pressure);
        Log.d("RESIZING", "Start");
        managedObject.startResizing();

        return StatesCodes.Resizing;
    }

    private int startResizing(PointF[] points)
    {
        Log.d("RESIZING", "Start");
        managedObject.completeDragging(points[0]);
        managedObject.startResizing();
        return StatesCodes.Resizing;
    }

    private int processResizing(PointF[] points)
    {
        Log.d("RESIZING", "In progress");
        managedObject.resizing(points);
        return StatesCodes.Resizing;
    }

    private int processResizingOneFingerUp(PointF[] points, ViewStateCodes viewStateCode, int fingerIndex)
    {
        Log.d("RESIZING", "One finger up");
        if(points.length>2)             // There is an old point in 'points' array too
        {
            Log.d("RESIZING", "points.length > 2");
            managedObject.resizing(points);
            return StatesCodes.Resizing;
        }
        else
        {
            Log.d("RESIZING", "points.length <= 2");
            managedObject.completeResizing();
            if(viewStateCode==ViewStateCodes.NotResized)
                return StatesCodes.Curving;
            else
            {
                managedObject.startDragging(points[fingerIndex==0 ? 1 : 0]);
                return StatesCodes.Dragging;
            }
        }
    }

    private int processDragging(PointF[] points)
    {
        managedObject.dragging(points[0]);
        return StatesCodes.Dragging;
    }

    private int processFromDraggingToFinal(PointF[] points)
    {
        managedObject.completeDragging(points[0]);
        return StatesCodes.Final;
    }

    /**
     *  Empty transition
     */
    private int doNothing(int state)
    {
        return state;
    }

    /**
     *  Empty transition
     */
    private int showMenu()
    {
        managedObject.showMenu();
        return StatesCodes.Init;
    }

    /**
     * Process next motion event
     * @param motionEvent
     * @param viewStateCode
     */
    public void Process(MotionEvent motionEvent, ViewStateCodes viewStateCode)
    {
        tryReset();         // Try to reset machine's state if it's final

        eventsTransformer.logTouchEvent(motionEvent);          // Debug only
        Event event = eventsTransformer.transform(motionEvent);
        eventsTransformer.logTouchEvent(event);

        currentState = transitionsMatrix[currentState][event.getCode()].process(event, viewStateCode);
    }

    private void tryReset()
    {
        if(currentState==StatesCodes.Final)
        {
            currentState=StatesCodes.Init;
            eventsTransformer.reset();
        }
    }
}
