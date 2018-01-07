package com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree;

import android.os.Parcel;
import android.os.Parcelable;

import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.FolderInfo;
import com.syleiman.comicsviewer.Common.Threads.ICancelationTokenRead;

import java.util.ArrayList;
import java.util.List;

/**
 *  One item of folders' tree
 */
public class FoldersTreeItem implements Parcelable
{
    private ICancelationTokenRead cancelationToken;

    /**
     * Type of item
     */
    private FoldersTreeItemTypes type;

    public FoldersTreeItemTypes getType() { return type; }

    /**
     * Absolute path to item
     */
    private String absolutePath;
    public String getAbsolutePath() { return absolutePath; }

    /**
     * Tree item has any image
     */
    private boolean hasImages;
    public boolean getHasImages() { return hasImages; }

    /**
     * Tree item has image in any subfolder
     */
    private boolean isActive;
    public boolean getIsActive() { return isActive; }

    /**
     * Child items
     */
    private List<FoldersTreeItem> subItems;
    public List<FoldersTreeItem> getSubItems() { return subItems; };

    public FoldersTreeItem(FoldersTreeItemTypes type, String absolutePath, ICancelationTokenRead cancelationToken)
    {
        this.type = type;
        this.absolutePath = absolutePath;
        this.cancelationToken = cancelationToken;
    }

    /**
     * Init state of object
     */
    public void init()
    {
        if(cancelationToken.isCanceled())
            return;

        FolderInfo folderInfo=new FolderInfo(absolutePath);
        hasImages=!CollectionsHelper.isNullOrEmpty(folderInfo.getImages());

        IFoldersTreeItemsGetter itemsGetter = new FoldersTreeCommonItemsGetter(folderInfo);
        List<FoldersTreeItem> tempSubItems=itemsGetter.getSubItems(cancelationToken);

        if(cancelationToken.isCanceled())
            return;

        if(CollectionsHelper.isNullOrEmpty(tempSubItems))           // It's a leaf
            subItems=null;
        else
        {
            subItems=new ArrayList<FoldersTreeItem>(tempSubItems.size());

            for(FoldersTreeItem subItem : tempSubItems)
            {
                if(cancelationToken.isCanceled())
                    return;

                subItem.init();

                if(subItem.hasImages || subItem.isActive)
                    subItems.add(subItem);
            }
        }

        isActive=calculateIsActive();
    }

    private boolean calculateIsActive()
    {
        if(CollectionsHelper.isNullOrEmpty(subItems))
            return false;

        return CollectionsHelper.any(subItems, i-> i.isActive || i.hasImages);
    }

    //region Parcelable implementation
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(type.name());
        dest.writeString(absolutePath);
        dest.writeInt(hasImages ? 1 : 0);
        dest.writeInt(isActive ? 1 : 0);
        dest.writeTypedList(subItems);
    }

    protected FoldersTreeItem(Parcel in)
    {
        type = FoldersTreeItemTypes.valueOf(in.readString());
        absolutePath = in.readString();
        hasImages = in.readInt() == 1;
        isActive = in.readInt() ==1;
        subItems = in.createTypedArrayList(FoldersTreeItem.CREATOR);
    }

    public static final Creator<FoldersTreeItem> CREATOR = new Creator<FoldersTreeItem>()
    {
        @Override
        public FoldersTreeItem createFromParcel(Parcel in)
        {
            return new FoldersTreeItem(in);
        }

        @Override
        public FoldersTreeItem[] newArray(int size)
        {
            return new FoldersTreeItem[size];
        }
    };
    //endregion
}