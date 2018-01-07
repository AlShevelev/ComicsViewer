package com.syleiman.comicsviewer.Activities.Folders.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemTypes;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTree;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTreeItem;
import com.syleiman.comicsviewer.Activities.Folders.IActivityFoldersActions;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView.CreateDeviceView;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView.CreateFileView;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView.CreateFolderView;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView.CreateImageView;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView.CreateSdCardView;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView.ICreateView;

import java.util.HashMap;
import java.util.List;

/**
 * Draws one item in folders list
 */
public class FoldersListAdapter extends BaseAdapter implements IAdapterFoldersActions
{
    private final Activity context;
    private static LayoutInflater inflater =null;

    private List<DiskItemInfo> diskItems;
    private HashMap<Integer, View> viewMap;

    private final IActivityFoldersActions actions;

    private int parentViewWidth =0;
    public void setParentViewWidth(int width) { parentViewWidth =width; }

    private DiskItemInfo checkedDiskItem;
    private CheckBox checkedCheckBox;

    private HashMap<String, FoldersTreeItem> foldersTreeFlatten;
    private HashMap<DiskItemTypes, ICreateView> viewCreators;

    public FoldersListAdapter(Activity activity, List<DiskItemInfo> diskItems, IActivityFoldersActions actions, FoldersTree foldersTree)
    {
        context =activity;
        this.diskItems = diskItems;
        this.actions =actions;
        this.foldersTreeFlatten=foldersTree.MakeFlatten();

        checkedDiskItem =null;
        checkedCheckBox =null;

        inflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewMap = new HashMap<>(this.diskItems.size());
        viewCreators = new HashMap<>(5);
    }

    @Override
    public int getCount()
    {
        return diskItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(viewMap.containsKey(position))
            return viewMap.get(position);
        else
        {
            DiskItemInfo diskItem= diskItems.get(position);
            FoldersTreeItem foldersTreeItem = foldersTreeFlatten.get(diskItem.getAbsolutePath());

            ICreateView createView=getViewCreator(diskItem);
            View rowView = createView.CreateView(diskItem, foldersTreeItem);

            viewMap.put(position, rowView);
            return rowView;
        }
    }

    public void folderCheckedChange(CheckBox checkBox, DiskItemInfo diskItem)
    {
        if(checkedDiskItem ==null)
        {
            checkedDiskItem = diskItem;                // Check by user
            checkedCheckBox =checkBox;
            actions.FolderCheckChanged(diskItem.getId(), true);
        }
        else
        {
            if(checkedDiskItem ==diskItem)
            {
                checkedDiskItem = null;                // Uncheck by user
                checkedCheckBox = null;
                actions.FolderCheckChanged(diskItem.getId(), false);
            }
            else
            {
                checkedCheckBox.setChecked(false);      // Switch

                checkedDiskItem = diskItem;
                checkedCheckBox =checkBox;

                actions.FolderCheckChanged(diskItem.getId(), true);
            }
        }
    }

    private ICreateView getViewCreator(DiskItemInfo diskItem)
    {
        DiskItemTypes diskItemType=diskItem.getItemType();

        ICreateView creator = null;
        creator = viewCreators.get(diskItemType);

        if(creator==null)
            switch (diskItemType)
            {
                case File: { creator = new CreateFileView(context, inflater, parentViewWidth); break; }
                case Image:  { creator = new CreateImageView(context, inflater, parentViewWidth); break; }
                case Folder: { creator = new CreateFolderView(context, inflater, parentViewWidth, actions, this ); break; }
                case Device: { creator = new CreateDeviceView(context, inflater, parentViewWidth, actions); break; }
                case SdCard: { creator = new CreateSdCardView(context, inflater, parentViewWidth, actions); break; }
            }

        viewCreators.put(diskItemType, creator);
        return creator;
    }
}