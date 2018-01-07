package com.syleiman.comicsviewer.Common.Rhea;

import android.app.Activity;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Core of management logic
 */
public class RheaComposer implements IRheaOperationComposer
{
    private LinkedList<RheaActivityRecord> activities;  // List of activities
    private LinkedList<RheaOperationRecord> operations;  // List of operations

    public RheaComposer()
    {
        activities = new LinkedList<>();
        operations = new LinkedList<>();
    }

    /**
     * Get unique code for activity
     */
    private String getActivityCode(Activity activity)
    {
        String code=activity.getLocalClassName();        // Class name without package
        return code;
    }

    /**
     * Inform about operation progress
     */
    @Override
    public void onOperationProgress(String tag, int value, int total)
    {
        RheaOperationRecord operationRecord = getOperationRecord(tag);
        RheaActivityRecord activityRecord = getActivityRecord(operationRecord.getActivityCode());

        // if activityRecord==null it means that activity was destroyed and operation progress is useless
        if(activityRecord!=null)
        {
            operationRecord.progressInfo = new RheaOperationProgressInfo(value, total);         // store progress info

            if(activityRecord.currentState== RheaActivityStates.Active)
                activityRecord.activity.onRheaWorkProgress(tag, operationRecord.progressInfo);         // show progress
        }
    }

    /**
     * Inform about operation completed successfully
     */
    @Override
    public void onOperationCompleted(String tag, Object result)
    {
        RheaOperationRecord operationRecord = getOperationRecord(tag);
        RheaActivityRecord activityRecord = getActivityRecord(operationRecord.getActivityCode());

        if(activityRecord==null)            // activity was destroyed
            removeOperationRecord(operationRecord);     // so result is useless
        else
        {
            if(activityRecord.currentState==RheaActivityStates.Active)
            {
                activityRecord.activity.onRheaWorkCompleted(tag, result);          // show result and remove operation
                removeOperationRecord(operationRecord);
            }
            else
                operationRecord.result = result;          // store result for future use
        }
    }

    /**
     * Inform about operation completed with error
     */
    @Override
    public void onOperationCompletedByError(String tag, Exception error)
    {
        RheaOperationRecord operationRecord = getOperationRecord(tag);
        RheaActivityRecord activityRecord = getActivityRecord(operationRecord.getActivityCode());

        if(activityRecord==null)            // activity was destroyed
            removeOperationRecord(operationRecord);     // so result is useless
        else
        {
            if(activityRecord.currentState==RheaActivityStates.Active)
            {
                activityRecord.activity.onRheaWorkCompletedByError(tag, error);          // show result and remove operation
                removeOperationRecord(operationRecord);
            }
            else
                operationRecord.error=error;          // store result for future use
        }
    }

    /**
     * Run operation
     * @param operation
     * @return result of operation running
     */
    public RheaOperationRunResult runOperation(Activity activity, RheaOperationBase operation)
    {
        RheaOperationRecord operationRecord = getOperationRecord(operation.getTag());
        if(operationRecord!=null)
            return RheaOperationRunResult.TagAlreadyRun;

        String activityCode=getActivityCode(activity);

        RheaActivityRecord activityRecord = getActivityRecord(activityCode);
        if(activityRecord==null)
            return RheaOperationRunResult.InvalidActivity;

        RheaOperationRecord newOperationRecord = new RheaOperationRecord(activityCode, operation.getTag(), operation);
        operation.setComposer(this);
        operations.add(newOperationRecord);
        operation.execute();                        // start operation in background

        return RheaOperationRunResult.Success;
    }

    /**
     * Get progress about work
     * @param tag tag of work
     * @return progress of work (or null if operation not found)
     */
    public RheaOperationProgressInfo getOperationProgress(String tag)
    {
        RheaOperationRecord operationRecord = getOperationRecord(tag);
        if(operationRecord!=null)
            return operationRecord.progressInfo;
        return null;
    }

    /**
     * Get operation state
     * @param tag tag of work
     * @return result of operation runing (or null if operation not found)
     */
    public RheaOperationState getOperationState(String tag)
    {
        RheaOperationRecord operationRecord = getOperationRecord(tag);
        if(operationRecord==null)
            return null;

        if(operationRecord.error==null && operationRecord.result==null)
            return RheaOperationState.InProgress;

        return RheaOperationState.Completed;
    }

    /**
     * Registration activity in Rhea
     * @param activity
     */
    public <T extends Activity & IRheaActivity> void onActivityCreate(T activity)
    {
        String activityCode= getActivityCode(activity);
        RheaActivityRecord activityRecord = getActivityRecord(activityCode);

        if(activityRecord!=null)                // Activity was re-created
        {
            activityRecord.wasSaved=false;
            activityRecord.activity = activity;
        }
        else
        {
            activityRecord = new RheaActivityRecord(activityCode, RheaActivityStates.Inactive, false, activity);
            activities.add(activityRecord);
        }

        for(RheaOperationRecord operationRecord : operations)                   // Inform UI about not completed activities
            if(operationRecord.getActivityCode().equals(activityCode))
                if(operationRecord.result!=null && operationRecord.error!=null)
                    activityRecord.activity.onRheaWorkInit(operationRecord.getTag(), operationRecord.progressInfo);

    }

    /**
     * Inform about pausing activity
     * @param activity
     */
    public void onActivityPause(Activity activity)
    {
        RheaActivityRecord activityRecord = getActivityRecord(getActivityCode(activity));
        if(activityRecord!=null)
            activityRecord.currentState = RheaActivityStates.Inactive;
    }

    /**
     * Inform about resuming activity
     * @param activity
     */
    public void onActivityResume(Activity activity)
    {
        String activityCode=getActivityCode(activity);

        RheaActivityRecord activityRecord = getActivityRecord(activityCode);
        if(activityRecord==null)
            return;

        activityRecord.currentState = RheaActivityStates.Active;
        activityRecord.wasSaved = false;

        ArrayList<RheaOperationRecord> completedOperations = new ArrayList<>(operations.size());
        for(RheaOperationRecord operationRecord : operations)
        {
            if(operationRecord.getActivityCode().equals(activityCode))      // if operations was completed - tall about it and remove they
            {
                if(operationRecord.result!=null)
                {
                    activityRecord.activity.onRheaWorkCompleted(operationRecord.getTag(), operationRecord.result);
                    completedOperations.add(operationRecord);
                }
                else if(operationRecord.error!=null)
                {
                    activityRecord.activity.onRheaWorkCompletedByError(operationRecord.getTag(), operationRecord.error);
                    completedOperations.add(operationRecord);
                }
            }
        }

        for(RheaOperationRecord operationRecord : completedOperations)          // remove completed operatios
            removeOperationRecord(operationRecord);
    }

    /**
     * Inform about saving activity state
     */
    public void onActivitySaveInstanceState(Activity activity)
    {
        RheaActivityRecord activityRecord = getActivityRecord(getActivityCode(activity));

        if(activityRecord!=null)
            activityRecord.wasSaved = true;
    }

    /**
     * Inform about destroing activity
     * @param activity
     */
    public void onActivityDestroy(Activity activity)
    {
        RheaActivityRecord activityRecord = getActivityRecord(getActivityCode(activity));
        if(!activityRecord.wasSaved)            // If state was not saved it means that activity was destroyed completely
            removeActivityRecord(activityRecord);
    }

    private RheaOperationRecord getOperationRecord(String tag)
    {
        for(RheaOperationRecord record : operations)
            if(record.getTag().equals(tag))
                return record;
        return null;                // not found
    }

    private RheaActivityRecord getActivityRecord(String activityCode)
    {
        for(RheaActivityRecord record : activities)
            if(record.getCode().equals(activityCode))
                return record;
        return null;                // not found
    }

    private void removeOperationRecord(RheaOperationRecord record)
    {
        record.getOperation().setComposer(null);            // tear link with composer
        operations.remove(record);
    }

    private void removeActivityRecord(RheaActivityRecord record)
    {
        record.activity = null;
        activities.remove(record);
    }
}