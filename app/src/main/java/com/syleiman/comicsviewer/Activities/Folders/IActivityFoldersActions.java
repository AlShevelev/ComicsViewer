package com.syleiman.comicsviewer.Activities.Folders;

public interface IActivityFoldersActions
{
    /**
     * Folder's ckeckbox was changed by User
     * @param id Id of folder
     */
    void FolderCheckChanged(int id, boolean isChecked);

    /**
     * User taped on folder
     * @param id Id of folder
     * @param folderWithImages true if this folder containts images
     */
    void FolderTaped(int id, boolean folderWithImages);
}
