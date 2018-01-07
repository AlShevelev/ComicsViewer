package com.syleiman.comicsviewer.Activities.ComicsCreation;

import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.FolderInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Start sort pagers of comics
 */
public class PagesStartSorter
{
    /**
     * Comparator for sorting images
     */
    private static class ImagesComparator implements Comparator<DiskItemInfo>
    {
        public int compare(DiskItemInfo diskItem1, DiskItemInfo diskItem2)
        {
            return diskItem1.getDisplayName().compareToIgnoreCase(diskItem2.getDisplayName());
        }
    }

    public static List<DiskItemInfo> Sort(String pathToFolder)
    {
        FolderInfo folderInfo=new FolderInfo(pathToFolder);
        List<DiskItemInfo> images=folderInfo.getImages();

        if(CollectionsHelper.isNullOrEmpty(images))         //Empty list if no images
            return new ArrayList<DiskItemInfo>();

        Comparator<DiskItemInfo> comparator=new ImagesComparator();
        Collections.sort(images, comparator);

        return images;
    }
}


// 01, 02... 10, 11 - correct
// 1, 2... 10, 11 - incorrect