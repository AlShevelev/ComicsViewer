package com.syleiman.comicsviewer.Common.Rhea;

import com.syleiman.comicsviewer.Common.Rhea.IRheaActivity;
import com.syleiman.comicsviewer.Common.Rhea.RheaActivityStates;

/**
 * One record with activity state
 */
public class RheaActivityRecord
{
    /**
     * Unique code of activity
     */
    private String code;

    /**
     * State of activity
     */
    public RheaActivityStates currentState;

    /**
     * Is method onSaveInstanceState was called for activity
     */
    public boolean wasSaved;

    public IRheaActivity activity;

    public RheaActivityRecord(String code, RheaActivityStates currentState, boolean wasSaved, IRheaActivity activity)
    {
        this.code = code;
        this.currentState = currentState;
        this.wasSaved = wasSaved;
        this.activity = activity;
    }

    public String getCode()
    {
        return code;
    }
}
