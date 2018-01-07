package com.syleiman.comicsviewer.ComicsWorkers;

import android.graphics.Bitmap;
import android.util.Log;

import com.syleiman.comicsviewer.Common.Helpers.BitmapDarkRate;
import com.syleiman.comicsviewer.Common.Helpers.BitmapsHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.AppPrivateFilesHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.BitmapsQuality;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Rhea.RheaOperationBase;
import com.syleiman.comicsviewer.Common.Structs.AreaF;
import com.syleiman.comicsviewer.Common.Structs.PointF;
import com.syleiman.comicsviewer.Common.Structs.Size;
import com.syleiman.comicsviewer.Common.Structs.SizeRelative;
import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Comics;
import com.syleiman.comicsviewer.Dal.Dto.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Comics is created here (result [Long]: id of comics in db (or null if something happend))
 */
public class ComicsCreator extends RheaOperationBase
{
    private final String name;            // Name of comics
    private final boolean isPrivate;      // Comics is private
    private final List<DiskItemInfo> sourceImages;          // Source images of pages

    private int[] diskItemsSortedIds;       // Sorted disk items

    private final IPreviewCreator previewCreator;

    public static final String tag = "ComicsCreationRhea";

    /**
     *
     * @param tag tag of Rhea operation
     * @param name name of comics
     * @param sourceImages
     * @param clientSize size of activity client area (ScreenHelper.getClientSize(context))
     */
    public ComicsCreator(
            String tag,
            String name,
            boolean isPrivate,
            List<DiskItemInfo> sourceImages,
            Size clientSize)
    {
        super(tag);

        this.name = name;
        this.isPrivate = isPrivate;
        this.sourceImages = sourceImages;

        this.previewCreator=new PreviewCreator(clientSize);
    }

    /**
     * Set disk items before comics creation
     * @param diskItemsSortedIds - sorted disk items
     */
    public void setDiskItems(int[] diskItemsSortedIds)
    {
        this.diskItemsSortedIds = diskItemsSortedIds;
    }

    private String createFileName()
    {
        return UUID.randomUUID().toString();
    }

    private Map<Integer, DiskItemInfo> sourceImagesToDict()
    {
        Map<Integer, DiskItemInfo> result= new TreeMap<>();             // Key: DiskItemInfo::Id, Value: name of file

        for(DiskItemInfo sourceImage : sourceImages)
            result.put(sourceImage.getId(), sourceImage);
        return result;
    }

    /**
     * Create preview and coping files of pages into private area
     * @return dictionary with ids and files names if success
     */
    private ArrayList<Page> createPagesAndCopyImages(Map<Integer, DiskItemInfo> pageFilesByIds, int[] diskItemsSortedIds)
    {
        int total = diskItemsSortedIds.length;
        ArrayList<Page> result=new ArrayList<>(total);

        for(int i=0; i< total; i++)
        {
            DiskItemInfo sourceImage = pageFilesByIds.get(diskItemsSortedIds[i]);

            String sourceFullName = sourceImage.getFullname();
            String pageFileName = createFileName();
            String previewFileName = PreviewCreator.getPreviewFileName(pageFileName);

            Bitmap previewBitmap = previewCreator.createPreviewAndSave(sourceFullName, previewFileName);       // Create and save preview
            if(previewBitmap==null)
                return null;

            if(!AppPrivateFilesHelper.createFromFile(sourceFullName, pageFileName))      // Copy images into private area
                return null;

            Page page=new Page();
            page.fileName = pageFileName;
            page.order=i;
            calculateCornerDarkness(previewBitmap, page);           // Calculate darrness of corners

            result.add(page);
            updateProgress(i+1, total);
        }

        return result;
    }

    private void calculateCornerDarkness(Bitmap source, Page page)
    {
        AreaF area=new AreaF(new PointF(0.85f, 0.85f), new SizeRelative(0.15f, 0.15f));         // right and bottom
        page.isRightBottomCornerDark = BitmapsHelper.isDark(source, area)== BitmapDarkRate.Dark;

        area=new AreaF(new PointF(0f, 0.85f), new SizeRelative(0.15f, 0.15f));
        page.isLeftBottomCornerDark= BitmapsHelper.isDark(source, area)== BitmapDarkRate.Dark;

        area=new AreaF(new PointF(0f, 0f), new SizeRelative(0.15f, 0.15f));
        page.isLeftTopCornerDark= BitmapsHelper.isDark(source, area)== BitmapDarkRate.Dark;

        area=new AreaF(new PointF(0.85f, 0f), new SizeRelative(0.15f, 0.15f));
        page.isRightTopCornerDark= BitmapsHelper.isDark(source, area)== BitmapDarkRate.Dark;
    }

    /**
     *
     * @param diskItemsSortedIds
     * @return file name with cover (in private area without path)
     */
    private String createCover(int[] diskItemsSortedIds, Map<Integer, DiskItemInfo> pageFilesByIds)
    {
        try
        {
            int firstId=diskItemsSortedIds[0];          // Cover is a first page
            DiskItemInfo sourceCover = pageFilesByIds.get(firstId);

            Bitmap coverBitmapScaled = CoverCreator.create(sourceCover.getFullname(), previewCreator);          // Create cover

            String destinationFileName=createFileName();                // Create file name for it
            AppPrivateFilesHelper.createFromBitmap(
                    destinationFileName,
                    coverBitmapScaled,
                    BitmapsQuality.Low,
                    Bitmap.CompressFormat.PNG);        // Save to disk

            return destinationFileName;             // and return file name
        }
        catch(Exception ex)
        {
            Log.e("CV", "exception", ex);
            return null;
        }
    }

    private Long saveToDb(String coverFileName, ArrayList<Page> pages)
    {
        Comics comics=new Comics();
        comics.name = this.name;
        comics.isPrivate = isPrivate;
        comics.coverFilename = coverFileName;
        comics.creationDate = new Date();           // UTC
        comics.totalPages = pages.size();

        return DalFacade.Comics.createComics(comics, pages);
    }

    @Override
    protected Object process()
    {
        Map<Integer, DiskItemInfo> sourceImageDict=sourceImagesToDict();
        ArrayList<Page> pages=createPagesAndCopyImages(sourceImageDict, diskItemsSortedIds);

        if(pages==null)
            return null;

        String coverFilename=createCover(diskItemsSortedIds, sourceImageDict);

        if(coverFilename!=null)
            return saveToDb(coverFilename, pages);

        return null;
    }
}