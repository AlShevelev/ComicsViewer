package com.syleiman.comicsviewer.Activities.Folders.FileSystem.DiskItems;

import android.util.Log;

import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.FolderInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Get list of disk sub-items for some folder
 */
public class DiskItemsNormalProcessor implements IDiskItemsProcessor
{
    private static final int MAX_NAME_LEN=35;           // Max length of item

    private final String _path;

    public DiskItemsNormalProcessor(String path)
    {
        this._path = path;
    }

    /**
     *
     * @return List of disk items or empty list in case of error
     */
    @Override
    public List<DiskItemInfo> getDiskItems()
    {
        try
        {
            FolderInfo folderInfo=new FolderInfo(_path);

            List<DiskItemInfo> folders= CollectionsHelper.transform(folderInfo.getSubFolders(),
                    t -> new DiskItemInfo(t.getId(), t.getItemType(), cutName(t.getName()), t.getName(), t.getAbsolutePath()));        // Cut long names
            List<DiskItemInfo> files= CollectionsHelper.transform(folderInfo.getFiles(),
                    t -> new DiskItemInfo(t.getId(), t.getItemType(), cutName(t.getName()), t.getName(), t.getAbsolutePath()));
            List<DiskItemInfo> images= CollectionsHelper.transform(folderInfo.getImages(),
                    t -> new DiskItemInfo(t.getId(), t.getItemType(), cutName(t.getName()), t.getName(), t.getAbsolutePath()));

            return mergeLists(folders, files, images);
        }
        catch(Exception ex)
        {
            Log.e("", ex.getMessage());
            return new ArrayList<DiskItemInfo>();           // empty list
        }
    }

    private String cutName(String name)
    {
        if(name.length()<=MAX_NAME_LEN)
            return name;

        return name.substring(0, MAX_NAME_LEN-4)+"...";
    }

    /**
     * Merge list in one for presentation
     */
    private List<DiskItemInfo> mergeLists(List<DiskItemInfo> foldersList, List<DiskItemInfo> filesList, List<DiskItemInfo> imagesList)
    {
        Collections.sort(foldersList, (d1, d2)-> d1.getDisplayName().compareTo(d2.getDisplayName()));           // Sorting by name
        Collections.sort(filesList, (d1, d2)-> d1.getDisplayName().compareTo(d2.getDisplayName()));
        Collections.sort(imagesList, (d1, d2)-> d1.getDisplayName().compareTo(d2.getDisplayName()));

        List<DiskItemInfo> result=new ArrayList<DiskItemInfo>(foldersList.size()+filesList.size()+imagesList.size());

        result.addAll(foldersList);
        result.addAll(imagesList);              // And merging
        result.addAll(filesList);

        return result;
    }
}