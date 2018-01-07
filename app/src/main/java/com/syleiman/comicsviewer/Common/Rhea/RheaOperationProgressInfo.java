package com.syleiman.comicsviewer.Common.Rhea;

/**
 * Information about work progress
 */
public class RheaOperationProgressInfo
{
    /**
     * Current value
     */
    public int value;

    /**
     * Total progress items
     */
    public int total;

    public RheaOperationProgressInfo(int value, int total)
    {
        this.value = value;
        this.total = total;
    }
}
