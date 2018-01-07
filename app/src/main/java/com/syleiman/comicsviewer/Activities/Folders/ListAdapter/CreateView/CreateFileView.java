package com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTreeItem;

public class CreateFileView extends CreateViewBase
{
    public CreateFileView(Activity context, LayoutInflater inflater, int parentViewWidth)
    {
        super(context, inflater, parentViewWidth);
    }

    @Override
    public View CreateView(DiskItemInfo diskItem, FoldersTreeItem foldersTreeItem)
    {
        ViewHolder viewHolder = getView(R.layout.folders_list_item);

        viewHolder.text.setText(diskItem.getDisplayName());

        viewHolder.image.setImageResource(R.drawable.ic_file);
        viewHolder.checkBox.setVisibility(View.INVISIBLE);

        viewHolder.image.setOnClickListener(v -> { });            // For prevent clicking track on checked item
        viewHolder.text.setOnClickListener(v -> { });

        return viewHolder.view;
    }
}
