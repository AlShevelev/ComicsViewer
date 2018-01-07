package com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree;

import com.syleiman.comicsviewer.Common.Threads.ICancelationTokenRead;

import java.util.List;

/**
 * Interface of class for getting tree items
 */
public interface IFoldersTreeItemsGetter
{
    List<FoldersTreeItem> getSubItems(ICancelationTokenRead cancelationToken);
}
