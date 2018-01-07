package com.syleiman.comicsviewer.Activities.ViewComics.BitmapRepository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.syleiman.comicsviewer.Activities.ViewComics.ResizingState;
import com.syleiman.comicsviewer.Common.Helpers.Files.AppPrivateFilesHelper;
import com.syleiman.comicsviewer.Dal.Dto.Page;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Caches one bitmap of max size per page
 */
public class BitmapRepository implements IBitmapRepository
{
    private List<Page> pages;       // list of comics pages

    private static final int MAX_INDEXES_TO_STORE=3;
    private QueueWithDisplacement indexesQueue;             // Indexes of cached bitmaps
    private Map<Integer, Bitmap> cachedBitmaps;

    public BitmapRepository(List<Page> pages)
    {
        indexesQueue=new QueueWithDisplacement(MAX_INDEXES_TO_STORE);
        cachedBitmaps=new TreeMap<>();

        this.pages = pages;
    }

    /**
     *
     * @param index index of page
     * @return
     */
    @Override
    public Bitmap getByIndex(int index, int viewAreaWidth, int viewAreaHeight)
    {
        if(indexesQueue.isExists(index))            // get from cache
            return cachedBitmaps.get(index);

        Integer displacementIndex= indexesQueue.push(index);

        if(displacementIndex!=null)             // remove old value from cache
            cachedBitmaps.remove(displacementIndex);

        Bitmap bitmap = loadBitmap(index, viewAreaWidth, viewAreaHeight);

        cachedBitmaps.put(index, bitmap);

        return bitmap;
    }

    @Override
    public int getPageCount()
    {
        return pages.size();
    }

    private Bitmap loadBitmap(int index, int viewAreaWidth, int viewAreaHeight)
    {
        String fullName = AppPrivateFilesHelper.getFullName(pages.get(index).fileName);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fullName, options);           // Read image size only
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if(imageHeight<=viewAreaHeight || imageWidth <= viewAreaWidth)      // Image is in-screen - decode it without scaling
            return BitmapFactory.decodeFile(fullName);

        int viewAreaMaxWidth = (int)(viewAreaWidth * ResizingState.MAX_SCALE);
        int viewAreaMaxHeight = (int)(viewAreaHeight * ResizingState.MAX_SCALE);            // Max possible image size

        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(imageWidth, imageHeight, viewAreaMaxWidth, viewAreaMaxHeight);
        return BitmapFactory.decodeFile(fullName, options);
    }

    /**
     * Calculate scaling factor (as power of 2)
     */
    public static int calculateInSampleSize(int imageWidth, int imageHeight, int viewAreaMaxWidth, int viewAreaMaxHeight)
    {
        int inSampleSize = 1;

        if (imageHeight > viewAreaMaxHeight || imageWidth > viewAreaMaxWidth) {

            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (((halfHeight / inSampleSize) > viewAreaMaxHeight || (float)(halfHeight/inSampleSize)/(float)viewAreaMaxHeight > 0.7f) ||
                    ((halfWidth / inSampleSize) > viewAreaMaxHeight || (float)(halfWidth/inSampleSize)/(float)viewAreaMaxHeight > 0.7f))
                inSampleSize *= 2;
        }

        return inSampleSize;
    }
}