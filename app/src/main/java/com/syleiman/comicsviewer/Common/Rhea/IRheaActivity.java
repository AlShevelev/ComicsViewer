package com.syleiman.comicsviewer.Common.Rhea;

/**
 * Interface of activity to support Rhea
 */
public interface IRheaActivity
{
    /**
     * Work completed successfully
     * @param tag tag of work
     * @param result result
     */
    void onRheaWorkCompleted(String tag, Object result);

    /**
     * There was an error while working
     * @param tag tag of work
     * @param exception concrete exception
     */
    void onRheaWorkCompletedByError(String tag, Exception exception);

    /**
     * Show work progress
     * @param tag tag of work
     * @param progressInfo Progress info
     */
    void onRheaWorkProgress(String tag, RheaOperationProgressInfo progressInfo);

    /**
     * Call when activity restarts for every not completed work (so we should init view here)
     * @param tag tag of work
     * @param progressInfo Progress info
     */
    void onRheaWorkInit(String tag, RheaOperationProgressInfo progressInfo);
}
