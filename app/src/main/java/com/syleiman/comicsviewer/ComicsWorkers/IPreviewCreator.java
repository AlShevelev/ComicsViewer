package com.syleiman.comicsviewer.ComicsWorkers;

import android.graphics.Bitmap;

public interface IPreviewCreator
{
    /**
     * Creates preview from source image in file
     * @param sourceFullNameOfFile
     * @return
     */
    Bitmap createPreview(String sourceFullNameOfFile);

    /**
     * Creates preview from source image
     * @param sourceBitmap
     * @return
     */
    Bitmap createPreview(Bitmap sourceBitmap);

    /**
     * Creates preview from source image and save its
     * @param sourceFullNameOfFile
     * @param previewFileName
     * @return created preview (null if fail)
     */
    Bitmap createPreviewAndSave(String sourceFullNameOfFile, String previewFileName);
}
