package com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails.ThumbnailListIds;
import com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails.ThumbnailManager;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;

import java.util.List;

/**
 * Adapter for pages sorting view
 */
public class ListDragAdapter extends BaseAdapter
{
    private Activity activity;
    private List<ListItemDrag> list;

    private final IActionOneArgs<ListItemDragingInfo> onDrag;
    private final int dragColor;

    private final ThumbnailManager thumbnailManager;

    private final ThumbnailListIds lisId;

    public ListDragAdapter(
            Activity activity,
            List<ListItemDrag> list,
            int dragColor,
            ThumbnailManager thumbnailManager,
            IActionOneArgs<ListItemDragingInfo> onDrag,
            ThumbnailListIds lisId)
    {
        this.activity = activity;
        this.list = list;
        this.onDrag = onDrag;
        this.dragColor = dragColor;
        this.thumbnailManager = thumbnailManager;
        this.lisId = lisId;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
//        Log.d("Thumbnail", "[getView] (position " + position + ")");

        View rowView = convertView;

        ViewHolderDrag viewHolder=null;

        if (rowView == null)                // reuse views
        {
            LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
            rowView = inflater.inflate(R.layout.sort_pages_list_item, null);

            viewHolder = new ViewHolderDrag();
            viewHolder.Icon = (ImageView) rowView.findViewById(R.id.rowImageView);
            viewHolder.ZoomIcon = (ImageView) rowView.findViewById(R.id.rowZoomIcon);
            viewHolder.VisibilityIcon = (ImageView) rowView.findViewById(R.id.rowVisibilityIcon);
            viewHolder.Text = (TextView) rowView.findViewById(R.id.rowTextView);
            rowView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolderDrag) rowView.getTag();

        ListItemDrag listItem = list.get(position);

        ListThumbnailHolder listThumbnailHolder = new ListThumbnailHolder(listItem.getId(), lisId, listItem.getFullPathToImageFile(), viewHolder.Icon);
        thumbnailManager.getThumbnail(listThumbnailHolder);         // Load image dynamicly

        viewHolder.ZoomIcon.setImageResource(R.drawable.ic_zoom_in_yellow_48dp);

        viewHolder.VisibilityIcon.setImageResource(getVisibilityIcon(listItem));

        viewHolder.Text.setText(listItem.getItemString());

        rowView.setOnDragListener(new ListItemOnDragListener(listItem, dragColor, onDrag));

        return rowView;
    }

    public List<ListItemDrag> getList()
    {
        return list;
    }

    private int getVisibilityIcon(ListItemDrag listItem)
    {
        if(listItem.getIsVisibile())
            return R.drawable.ic_visibility_yellow_48dp;
        else
            return R.drawable.ic_visibility_off_yellow_48dp;
    }
}