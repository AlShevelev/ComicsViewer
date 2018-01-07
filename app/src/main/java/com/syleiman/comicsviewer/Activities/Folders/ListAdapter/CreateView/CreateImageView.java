package com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.Dialogs.ZoomedPagePreviewDialog;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTreeItem;
import com.syleiman.comicsviewer.Common.Helpers.ScreenHelper;

public class CreateImageView extends CreateViewBase
{
    public CreateImageView(Activity context, LayoutInflater _inflater, int parentViewWidth)
    {
        super(context, _inflater, parentViewWidth);
    }

    @Override
    public View CreateView(DiskItemInfo diskItem, FoldersTreeItem foldersTreeItem)
    {
        ViewHolder viewHolder = getView(R.layout.folders_list_item);

        viewHolder.text.setText(diskItem.getDisplayName());

        viewHolder.image.setImageResource(R.drawable.ic_image);
        viewHolder.checkBox.setVisibility(View.INVISIBLE);

        viewHolder.view.setOnClickListener(v -> showImage(diskItem));

        return viewHolder.view;
    }

    private void showImage(DiskItemInfo diskItem)
    {
        ZoomedPagePreviewDialog dialog=new ZoomedPagePreviewDialog(
                context,
                diskItem.getFullname(),
                ScreenHelper.getScreenSize(context).scale(0.85f));
        dialog.show();
    }
}
