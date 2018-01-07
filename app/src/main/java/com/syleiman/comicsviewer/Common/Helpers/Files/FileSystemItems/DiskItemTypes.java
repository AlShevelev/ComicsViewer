package com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems;

public enum DiskItemTypes
{
    Folder,

    /**
     * Some image file - jpg, png and so on
     */
    Image,

    /**
     * Not image file
     */
    File,

    /**
     * Phone root folder without sd-card
     */
    Device,

    /**
     * Sd-card root folder
     */
    SdCard
}
