package com.syleiman.comicsviewer.Common.Rhea;

import android.app.Activity;

/**
 * Facade interface of Rhea
 */
public class RheaFacade
{
    private static final RheaComposer composer=new RheaComposer();

    /**
     * Run operation
     * @param operation
     * @return result of operation runing
     */
    public static RheaOperationRunResult run(Activity activity, RheaOperationBase operation)
    {
        return composer.runOperation(activity, operation);
    }

    /**
     * Get progress about work
     * @param tag tag of work
     * @return progress of work (or null if operation not found)
     */
    public static RheaOperationProgressInfo getProgress(String tag)
    {
        return composer.getOperationProgress(tag);
    }

    /**
     * Get operation state
     * @param tag tag of work
     * @return result of operation runing (or null if operation not found)
     */
    public static RheaOperationState getState(String tag)
    {
        return composer.getOperationState(tag);
    }

    /**
     * Registration activity in Rhea
     * @param activity
     */
    public static <T extends Activity & IRheaActivity> void onCreate(T activity)
    {
        composer.onActivityCreate(activity);
    }

    /**
     * Inform about pausing activity
     * @param activity
     */
    public static void onPause(Activity activity)
    {
        composer.onActivityPause(activity);
    }

    /**
     * Inform about resuming activity
     * @param activity
     */
    public static void onResume(Activity activity)
    {
        composer.onActivityResume(activity);
    }

    /**
     * Inform about saving activity state
     */
    public static void onSaveInstanceState(Activity activity)
    {
        composer.onActivitySaveInstanceState(activity);
    }

    /**
     * Inform about destroing activity
     * @param activity
     */
    public static void onDestroy(Activity activity)
    {
        composer.onActivityDestroy(activity);
    }
}