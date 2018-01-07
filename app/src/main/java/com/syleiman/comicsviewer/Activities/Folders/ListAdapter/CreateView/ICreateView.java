package com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView;

import android.view.View;

import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTreeItem;

public interface ICreateView
{
    View CreateView(DiskItemInfo diskItem, FoldersTreeItem foldersTreeItem);
}
