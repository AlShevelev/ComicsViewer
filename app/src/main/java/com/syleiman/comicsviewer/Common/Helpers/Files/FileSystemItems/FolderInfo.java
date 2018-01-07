package com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems;

import android.util.Log;

import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All data about folder
 */
public class FolderInfo
{
    private final String _path;
    public String getPath() { return _path; }

    private String _name;
    public String getName() { return _name; }

    private List<DiskItemInfo> _subFolders;
    public List<DiskItemInfo> getSubFolders() { return _subFolders; }

    private List<DiskItemInfo> _images;
    public List<DiskItemInfo> getImages() { return _images; }

    private List<DiskItemInfo> _files;          // All files but not images
    public List<DiskItemInfo> getFiles() { return _files; }

    private static final List<String> _imagesExt = new ArrayList<>(Arrays.asList("png", "jpg", "jpeg", "gif"));

    public FolderInfo(String _path)
    {
        this._path = _path;
        initDiskItems();
    }

    private void initDiskItems()
    {
        try
        {
            File root = new File(_path);
            _name = root.getName();
            File[] files = root.listFiles();

            if(files==null)
            {
                _subFolders=new ArrayList<DiskItemInfo>();
                _images=new ArrayList<DiskItemInfo>();
                _files=new ArrayList<DiskItemInfo>();
            }
            else
            {
                _subFolders=new ArrayList<DiskItemInfo>(files.length);
                _images=new ArrayList<DiskItemInfo>(files.length);
                _files=new ArrayList<DiskItemInfo>(files.length);
            }

            int index=0;
            for (File file : files)
            {
                if(file.isHidden())
                    continue;

                String name = file.getName();
                if (file.isDirectory())
                    _subFolders.add(new DiskItemInfo(index++, DiskItemTypes.Folder, name, name, _path+"/"+name));
                else {
                    if (isImage(name))
                        _images.add(new DiskItemInfo(index++, DiskItemTypes.Image, file.getName(), file.getName(), _path));
                    else
                        _files.add(new DiskItemInfo(index++, DiskItemTypes.File, file.getName(), file.getName(), _path));
                }
            }
        }
        catch(Exception ex)
        {
            Log.e("", ex.getMessage());
            throw ex;
        }
    }

    private Boolean isImage(String name)
    {
        if(name==null || name == "")
            return false;

        int lastDotIndex=name.lastIndexOf(".");

        if(lastDotIndex==-1)        // There is not extention - it's a file
            return false;

        String ext=name.substring(lastDotIndex+1, name.length()).toLowerCase();

        return CollectionsHelper.any(_imagesExt, i -> i.equals(ext));
    }
}
