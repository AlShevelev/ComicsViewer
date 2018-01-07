package com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTreeItem;
import com.syleiman.comicsviewer.Activities.Folders.IActivityFoldersActions;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.IAdapterFoldersActions;

public class CreateFolderView extends CreateViewBase
{
    private IActivityFoldersActions activityActions;
    private IAdapterFoldersActions adapterActions;

    public CreateFolderView(
            Activity context,
            LayoutInflater _inflater,
            int parentViewWidth,
            IActivityFoldersActions activityActions,
            IAdapterFoldersActions adapterActions)
    {
        super(context, _inflater, parentViewWidth);

        this.activityActions = activityActions;
        this.adapterActions = adapterActions;
    }

    @Override
    public View CreateView(DiskItemInfo diskItem, FoldersTreeItem foldersTreeItem)
    {
        boolean disabled=foldersTreeItem==null || (!foldersTreeItem.getIsActive() && !foldersTreeItem.getHasImages());

        if(!disabled)
        {
            ViewHolder viewHolder = getView(R.layout.folders_list_item);
            viewHolder.text.setText(diskItem.getDisplayName());

            if(!foldersTreeItem.getHasImages())
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            else
                viewHolder.checkBox.setOnCheckedChangeListener((checkBox, isChecked) -> { adapterActions.folderCheckedChange((CheckBox)checkBox, diskItem); });

            viewHolder.image.setImageResource(R.drawable.ic_folder);

//            viewHolder.image.setOnClickListener(v -> activityActions.FolderTaped(diskItem.getId(), foldersTreeItem.getHasImages()));
//            viewHolder.text.setOnClickListener(v -> activityActions.FolderTaped(diskItem.getId(), foldersTreeItem.getHasImages()));
            viewHolder.view.setOnClickListener(v -> activityActions.FolderTaped(diskItem.getId(), foldersTreeItem.getHasImages()));


            return viewHolder.view;
        }
        else
        {
            ViewHolder viewHolder = getView(R.layout.folders_list_item_disabled);
            viewHolder.text.setText(diskItem.getDisplayName());

            viewHolder.image.setImageResource(R.drawable.ic_folder_disabled);
            viewHolder.image.setOnClickListener(v -> {} );
            viewHolder.text.setOnClickListener(v -> {});
            viewHolder.view.setOnClickListener(v -> { });

            viewHolder.checkBox.setVisibility(View.INVISIBLE);

            return viewHolder.view;
        }
    }
}