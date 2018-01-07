package com.syleiman.comicsviewer.Activities.Folders.ListAdapter.CreateView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTreeItem;

public abstract class CreateViewBase implements ICreateView
{
    private LayoutInflater inflater =null;
    private int parentViewWidth =0;
    protected Activity context;

    /**
     * Temporary data struct vith view
     */
    protected class ViewHolder
    {
        public View view;
        public TextView text;
        public ImageView image;
        public CheckBox checkBox;

        public ViewHolder(View view, TextView text, ImageView image, CheckBox checkBox)
        {
            this.view = view;
            this.text = text;
            this.image = image;
            this.checkBox = checkBox;
        }
    }

    public CreateViewBase(Activity context, LayoutInflater _inflater, int parentViewWidth)
    {
        this.inflater = _inflater;
        this.parentViewWidth = parentViewWidth;
        this.context = context;
    }

    protected ViewHolder getView(int viewResId)
    {
        View rowView = inflater.inflate(viewResId, null);
        rowView.setMinimumWidth(parentViewWidth);

        TextView tv = (TextView) rowView.findViewById(R.id.tvDiskItemName);
        ImageView img = (ImageView) rowView.findViewById(R.id.ivDiskItemIcon);
        CheckBox cb = (CheckBox) rowView.findViewById(R.id.cbDiskItemCheck);

        return new ViewHolder(rowView, tv, img, cb);
    }

    @Override
    public abstract View CreateView(DiskItemInfo diskItem, FoldersTreeItem foldersTreeItem);
}
