package com.syleiman.comicsviewer.Common.Rhea;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Base class for Rhea operation
 */
public abstract class RheaOperationBase extends AsyncTask<Void, Integer, Object> implements IRheaOperationForComposer
{
    private IRheaOperationComposer composer;
    private final String tag;

    private Exception processingException;

    public RheaOperationBase(String tag)
    {
        this.tag = tag;
    }

    public String getTag()
    {
        return tag;
    }

    /**
     * Set composer for operation
     * @param composer
     */
    public void setComposer(IRheaOperationComposer composer)
    {
        this.composer = composer;
    }

    @Override
    protected Object doInBackground(Void... params)
    {
        try
        {
            return process();
        }
        catch(Exception ex)
        {
            Log.e("RheaOperation (tag: "+tag + ")", ex.getMessage(), ex);
            processingException = ex;
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
        composer.onOperationProgress(tag, values[0], values[1]);
    }


    @Override
    protected void onPostExecute(Object result)
    {
        super.onPostExecute(result);

        if(processingException!=null)
            composer.onOperationCompletedByError(tag, processingException);
        else
            composer.onOperationCompleted(tag, result);
    }

    /**
     * Start method for processing operation (if it needed can throw exception)
     * @return result of operation (may be null)
     */
    protected abstract Object process();

    /**
     * Update progress if needed
     * @param value current progress value
     * @param total total progress items
     */
    protected void updateProgress(int value, int total)
    {
        this.publishProgress(value, total);
    }
}