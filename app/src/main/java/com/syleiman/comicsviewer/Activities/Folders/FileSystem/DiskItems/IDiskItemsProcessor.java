package com.syleiman.comicsviewer.Activities.Folders.FileSystem.DiskItems;

import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;

import java.util.List;

/**
 * Created by Syleiman on 26.07.2015.
 */
public interface IDiskItemsProcessor
{
    List<DiskItemInfo> getDiskItems();
}
