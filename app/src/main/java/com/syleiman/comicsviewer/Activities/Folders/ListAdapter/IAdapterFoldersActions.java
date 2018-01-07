package com.syleiman.comicsviewer.Activities.Folders.ListAdapter;

import android.widget.CheckBox;

import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;

public interface IAdapterFoldersActions
{
    void folderCheckedChange(CheckBox checkBox, DiskItemInfo diskItem);
}
