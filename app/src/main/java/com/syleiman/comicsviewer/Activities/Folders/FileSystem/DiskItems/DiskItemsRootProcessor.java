package com.syleiman.comicsviewer.Activities.Folders.FileSystem.DiskItems;

import android.os.Environment;
import android.util.Log;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Get list of root disk sub-items
 */
public class DiskItemsRootProcessor implements IDiskItemsProcessor
{
    public DiskItemsRootProcessor()
    {
    }

    @Override
    public List<DiskItemInfo> getDiskItems()
    {
        List<DiskItemInfo> result = new ArrayList<>(2);

        String storageState=Environment.getExternalStorageState();
        if(!storageState.equals(Environment.MEDIA_MOUNTED))
        {
            Log.i("", "Invalid storage state: "+storageState);
            return result;
        }

        File dataFolder =  Environment.getExternalStorageDirectory();
        if(dataFolder!=null)
            result.add(new DiskItemInfo(
                    0,
                    DiskItemTypes.Device,
                    App.getContext().getString(R.string.dev_root_folder),
                    App.getContext().getString(R.string.dev_root_folder),
                    dataFolder.getAbsolutePath()));

        String sdCardFolderPath=System.getenv("SECONDARY_STORAGE");
        if(sdCardFolderPath!=null)
        {
            File sdCardFolder = new File(System.getenv("SECONDARY_STORAGE"));
            if (sdCardFolder != null)
                result.add(new DiskItemInfo(
                        1,
                        DiskItemTypes.SdCard,
                        App.getContext().getString(R.string.sdcard_root_folder),
                        App.getContext().getString(R.string.sdcard_root_folder),
                        sdCardFolder.getAbsolutePath()));
        }

        return result;
    }
}
