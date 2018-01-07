package com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree;

import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.FolderInfo;
import com.syleiman.comicsviewer.Common.Threads.ICancelationTokenRead;

import java.util.List;

/**
 * Returns common (ordinary folders) items of folders tree
 */
public class FoldersTreeCommonItemsGetter implements IFoldersTreeItemsGetter
{
    private final FolderInfo folderInfo;

    public FoldersTreeCommonItemsGetter(FolderInfo folderInfo)
    {
        this.folderInfo = folderInfo;
    }

    @Override
    public List<FoldersTreeItem> getSubItems(ICancelationTokenRead cancelationToken) {

        List<FoldersTreeItem> result= CollectionsHelper.transform(folderInfo.getSubFolders(),
                t -> new FoldersTreeItem(FoldersTreeItemTypes.Folder, t.getAbsolutePath(), cancelationToken));

        return result;
    }
}
