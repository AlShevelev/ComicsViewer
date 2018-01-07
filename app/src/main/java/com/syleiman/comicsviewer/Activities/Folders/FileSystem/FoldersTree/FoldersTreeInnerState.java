package com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;

public class FoldersTreeInnerState implements Parcelable
{
    public List<FoldersTreeItem> tree;
    public HashMap<String, FoldersTreeItem> flattenTree;           // Key: absolute path

    public FoldersTreeInnerState()
    {
    }

    protected FoldersTreeInnerState(Parcel in)
    {
        tree = in.createTypedArrayList(FoldersTreeItem.CREATOR);
    }

    public static final Creator<FoldersTreeInnerState> CREATOR = new Creator<FoldersTreeInnerState>()
    {
        @Override
        public FoldersTreeInnerState createFromParcel(Parcel in)
        {
            return new FoldersTreeInnerState(in);
        }

        @Override
        public FoldersTreeInnerState[] newArray(int size)
        {
            return new FoldersTreeInnerState[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedList(tree);
    }
}
