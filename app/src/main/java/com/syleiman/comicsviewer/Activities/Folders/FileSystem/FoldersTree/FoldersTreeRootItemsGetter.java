package com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree;

import android.os.Environment;

import com.syleiman.comicsviewer.Common.Threads.ICancelationTokenRead;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns root items of folders tree
 */
public class FoldersTreeRootItemsGetter implements IFoldersTreeItemsGetter
{
    @Override
    public List<FoldersTreeItem> getSubItems(ICancelationTokenRead cancelationToken)
    {
        List<FoldersTreeItem> result = new ArrayList<>(2);

        String storageState= Environment.getExternalStorageState();
        if(!storageState.equals(Environment.MEDIA_MOUNTED))             // No mounted disks
            return result;

        File dataFolder =  Environment.getExternalStorageDirectory();
        if(dataFolder!=null)                            // Main storage
            result.add(new FoldersTreeItem(FoldersTreeItemTypes.Device, dataFolder.getAbsolutePath(), cancelationToken));

        String sdCardFolderPath=System.getenv("SECONDARY_STORAGE");
        if(sdCardFolderPath!=null)
        {                                               // Card's storage
            File sdCardFolder = new File(System.getenv("SECONDARY_STORAGE"));
            if (sdCardFolder != null)
                result.add(new FoldersTreeItem(FoldersTreeItemTypes.SdCard, sdCardFolder.getAbsolutePath(), cancelationToken));
        }

        return result;
    }
}
