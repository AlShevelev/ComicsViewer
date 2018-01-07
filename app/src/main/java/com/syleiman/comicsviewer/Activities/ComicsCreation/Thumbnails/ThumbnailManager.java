package com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.IListItemDragCreatorImages;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.ListItemDrag;
import com.syleiman.comicsviewer.Common.Collections.DynamicList;
import com.syleiman.comicsviewer.Common.Collections.SpillingQueue;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;
import com.syleiman.comicsviewer.Common.ProducerConsumer.ProducerConsumerTaskKinds;
import com.syleiman.comicsviewer.Common.ProducerConsumer.ProducerConsumerTaskProcessingResultBase;

import java.util.List;

/**
 * Core class for thumbnails calculation
 */
public class ThumbnailManager
{
    private static final int listImagesMaxCapasity=25;     // Max len of caching queues

    private final SpillingQueue<ThumbnailCalculationResult> firstListImages;
    private final SpillingQueue<ThumbnailCalculationResult> secondListImages;

    private static final int imagesProcessorMaxCapasity=25;     // Max len of queues for processing images
    private final ThumbnailProcessor imagesProcessor;

    private DynamicList<IThumbnailControl> waitingControls;         // Controls waintg for images

    private final IListItemDragCreatorImages listItemImages;

    private int warmUpCacheDeep = 5;            // How many items will be placed in cache while warming up

    private final IActionOneArgs<Object> stopCallback;         // Call when processing stoped

    public ThumbnailManager(IListItemDragCreatorImages listItemImages, IActionOneArgs<Object> stopCallback)
    {
        firstListImages = new SpillingQueue<>(listImagesMaxCapasity);
        secondListImages = new SpillingQueue<>(listImagesMaxCapasity);

        imagesProcessor = new ThumbnailProcessor(
                imagesProcessorMaxCapasity,
                listItemImages,
                this::onImageCalculated,
                this::onImageCalculationError,
                this::onImageCalculationSpilled,
                this::onStopProcessing);

        waitingControls = new DynamicList<>();

        this.listItemImages = listItemImages;
        this.stopCallback = stopCallback;
    }

    /**
     * Warm up caches - put some images into its
     */
    public void warmUpCaches(List<ListItemDrag> listItems)
    {
        int itemToPlace = Math.min(warmUpCacheDeep, listItems.size());
        for(int i=0; i<itemToPlace; i++)
        {
            ListItemDrag item = listItems.get(i);

            ProducerConsumerTaskProcessingResultBase result=imagesProcessor.processSync(new ThumbnailTask(item.getId(), ThumbnailListIds.Left, item.getFullPathToImageFile()));
            if(result==null)
                break;

            ThumbnailTaskResult typedResult=(ThumbnailTaskResult)result;

            firstListImages.push(new ThumbnailCalculationResult(typedResult.getId(), typedResult.getPageImage()));
            secondListImages.push(new ThumbnailCalculationResult(typedResult.getId(), typedResult.getPageImage()));
        }
    }

    /**
     * Get cached images for pages list
     */
    private SpillingQueue<ThumbnailCalculationResult> getListImages(ThumbnailListIds listId)
    {
        if(listId== ThumbnailListIds.Left)
            return firstListImages;
        return secondListImages;
    }

    /**
     * Get cached images for another list
     */
    private SpillingQueue<ThumbnailCalculationResult> getAnotherListImages(ThumbnailListIds listId)
    {
        if(listId== ThumbnailListIds.Left)
            return secondListImages;
        return firstListImages;
    }

    /**
     * Get thumbnail for control
     * @param thumbnailControl control needed thumbnail
     */
    public void getThumbnail(IThumbnailControl thumbnailControl)
    {
        if(waitingControls.isExists(t -> t.getId()==thumbnailControl.getId() && t.getListId() == thumbnailControl.getListId()))      // Control allready in waiting list
            return;

        IThumbnailControl oldControl=waitingControls.extract(t->t.getImageHachCode()==thumbnailControl.getImageHachCode());
        if(oldControl!=null)            // ListView reuses controls, so we must remove old control and its taks
        {
            imagesProcessor.removeTask(t ->
            {
                if(t.getKind()!= ProducerConsumerTaskKinds.Normal)
                    return false;

                ThumbnailTask typedTask = (ThumbnailTask)t;
                return typedTask.getId() == thumbnailControl.getId() && typedTask.getListIds() == thumbnailControl.getListId();
            });
        }

        SpillingQueue<ThumbnailCalculationResult> imagesList=getListImages(thumbnailControl.getListId());            // Try to find image in our list
        ThumbnailCalculationResult cachedImage = imagesList.getAndMoveToHead(t -> t.getId() == thumbnailControl.getId());
        if(cachedImage!=null)           // return cached image
        {
            thumbnailControl.setThumbnail(cachedImage.getPageImage());
            return;
        }

        SpillingQueue<ThumbnailCalculationResult> anotherListImages=getAnotherListImages(thumbnailControl.getListId());            // Second attempt - in another list
        cachedImage = anotherListImages.get(t -> t.getId() == thumbnailControl.getId());
        if(cachedImage!=null)
        {
            imagesList.push(cachedImage);               // push in our list
            thumbnailControl.setThumbnail(cachedImage.getPageImage());          // and return cached image
            return;
        }

        // image not found - we need to calculate it
        thumbnailControl.setThumbnail(listItemImages.getStubPageImage());          // set stub image
        waitingControls.push(thumbnailControl);         // memorize control...
        imagesProcessor.processAsync(new ThumbnailTask(thumbnailControl.getId(), thumbnailControl.getListId(), thumbnailControl.getFullSourceFileName()));         // and start calculation
    }

    /**
     * Image calculated successfully
     * @param id id of control
     * @param pageImage calculated image
     */
    private void onImageCalculated(int id, ThumbnailListIds listId, Drawable pageImage)
    {
        IThumbnailControl control = waitingControls.extract((item) -> item.getId() == id && item.getListId() == listId);      // remove control from waiting list
        if(control==null)
            return;

        SpillingQueue<ThumbnailCalculationResult> imagesList=getListImages(control.getListId());        // push image to cache
        imagesList.push(new ThumbnailCalculationResult(id, pageImage));

        control.setThumbnail(pageImage);            // and display it
    }

    /**
     * There was an error while calculate image
     * @param id id of control
     */
    private void onImageCalculationError(int id, ThumbnailListIds listId)
    {
        waitingControls.extract((item) -> item.getId() == id && item.getListId() == listId);      // remove control from waiting list
    }

    /**
     * Calculation was spilled
     * @param id id of control
     */
    private void onImageCalculationSpilled(int id, ThumbnailListIds listId)
    {
        waitingControls.extract((item) -> item.getId() == id && item.getListId() == listId);      // remove control from waiting list
    }

    /**
     * Start processing images
      */
    public void start()
    {
        imagesProcessor.start();
    }

    /**
     * Command to stop processing
     * @param dataToReturn some data (state) need to return when processing'll stop
     */
    public void stop(Object dataToReturn)
    {
        imagesProcessor.stop(dataToReturn);
    }

    private void onStopProcessing(Object dataToReturn)
    {
        stopCallback.process(dataToReturn);
    }
}