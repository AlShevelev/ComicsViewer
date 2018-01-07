package com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems;

/**
 * One significant item of file system - device, memory card, folder, image or non-image file
 */
public class DiskItemInfo
{
    private final int _id;
    public int getId() { return _id; }

    private final DiskItemTypes _itemType;
    public DiskItemTypes getItemType() { return _itemType; }

    /**
     * Display name - may be cutted and formatted
     */
    private final String _displayName;
    public String getDisplayName() { return _displayName; }

    /**
     * Name of file - uncutted and unformatted
     */
    private final String _name;
    public String getName() { return _name; }

    /**
     * full path to item (without name)
     */
    private final String _absolutePath;
    public String getAbsolutePath() { return _absolutePath; }

    /**
     * full name of file (with path)
     */
    public String getFullname() { return _absolutePath+"/"+ _name; }

    public DiskItemInfo(int id, DiskItemTypes itemType, String displayName, String name, String absolutePath)
    {
        _id=id;
        _itemType = itemType;
        _displayName = displayName;
        _name = name;
        _absolutePath = absolutePath;
    }
}