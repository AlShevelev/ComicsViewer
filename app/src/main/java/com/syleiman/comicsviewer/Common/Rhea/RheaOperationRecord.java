package com.syleiman.comicsviewer.Common.Rhea;

/**
 * One record with operation state
 */
public class RheaOperationRecord
{
    /**
     * Unique code of activity in witch this operation started
     */
    private String activityCode;

    /**
     * Tag of operation
     */
    private String tag;

    /**
     * Result of work completed successfully
     */
    public Object result;

    /**
     * Result of work completed with error
     */
    public Exception error;

    /**
     * Current progress
     */
    public RheaOperationProgressInfo progressInfo;

    private IRheaOperationForComposer operation;

    public RheaOperationRecord(String activityCode, String tag, IRheaOperationForComposer operation)
    {
        this.activityCode = activityCode;
        this.tag = tag;
        this.operation = operation;

        this.result = null;
        this.error = null;
    }

    public String getActivityCode()
    {
        return activityCode;
    }

    public String getTag()
    {
        return tag;
    }

    public IRheaOperationForComposer getOperation()
    {
        return operation;
    }
}
