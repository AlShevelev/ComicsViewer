package com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.IListItemDragCreatorImages;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionThreeArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionTwoArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;
import com.syleiman.comicsviewer.Common.ProducerConsumer.ProducerConsumerBase;
import com.syleiman.comicsviewer.Common.ProducerConsumer.ProducerConsumerTaskBase;
import com.syleiman.comicsviewer.Common.ProducerConsumer.ProducerConsumerTaskProcessingResultBase;

/**
 * Thumbnail calculates here
 */
public class ThumbnailProcessor extends ProducerConsumerBase
{
    private final IListItemDragCreatorImages listItemImages;

    private IActionThreeArgs<Integer, ThumbnailListIds, Drawable> onTaskProcessedCallback;            // Callback methods
    private IActionTwoArgs<Integer, ThumbnailListIds> onTaskErrorCallback;
    private IActionTwoArgs<Integer, ThumbnailListIds> onTaskSpilledCallback;
    private IActionOneArgs<Object> onStopProcessingCallback;

    public ThumbnailProcessor(
            int maxQueueLen,
            IListItemDragCreatorImages listItemImages,
            IActionThreeArgs<Integer, ThumbnailListIds, Drawable> onTaskProcessedCallback,
            IActionTwoArgs<Integer, ThumbnailListIds> onTaskErrorCallback,
            IActionTwoArgs<Integer, ThumbnailListIds> onTaskSpilledCallback,
            IActionOneArgs<Object> onStopProcessingCallback)
    {
        super(maxQueueLen);
        this.listItemImages = listItemImages;

        this.onTaskProcessedCallback = onTaskProcessedCallback;
        this.onTaskErrorCallback = onTaskErrorCallback;
        this.onTaskSpilledCallback = onTaskSpilledCallback;
        this.onStopProcessingCallback = onStopProcessingCallback;
    }

    /**
     * Calculate image
     */
    @Override
    protected ProducerConsumerTaskProcessingResultBase processTask(ProducerConsumerTaskBase task) throws InterruptedException
    {
        try
        {
            ThumbnailTask thumbnailTask = (ThumbnailTask)task;

            Drawable image=listItemImages.getPageImage(thumbnailTask.getFullPathToImageFile());

            return new ThumbnailTaskResult(thumbnailTask.getId(), thumbnailTask.getListIds(), image);
        }
        catch(Exception ex)
        {
            Log.e("ThumbnailProcessor", ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    protected void onStopProcessing(Object data)
    {
        onStopProcessingCallback.process(data);
    }

    @Override
    protected void onTaskProcessed(ProducerConsumerTaskProcessingResultBase result)
    {
        ThumbnailTaskResult taskResult = (ThumbnailTaskResult)result;
        onTaskProcessedCallback.process(taskResult.getId(), taskResult.getListIds(), taskResult.getPageImage());
    }

    @Override
    protected void onTaskError(ProducerConsumerTaskBase task)
    {
        ThumbnailTask taskResult = (ThumbnailTask)task;
        onTaskErrorCallback.process(taskResult.getId(), taskResult.getListIds());
    }

    @Override
    protected void onTaskSpilled(ProducerConsumerTaskBase task)
    {
        ThumbnailTask taskResult = (ThumbnailTask)task;
        onTaskSpilledCallback.process(taskResult.getId(), taskResult.getListIds());
    }
}