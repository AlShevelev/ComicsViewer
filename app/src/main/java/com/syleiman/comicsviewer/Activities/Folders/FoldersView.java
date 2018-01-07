package com.syleiman.comicsviewer.Activities.Folders;

import android.app.Activity;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.CustomControls.ProgressBar;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTree;
import com.syleiman.comicsviewer.Activities.Folders.ListAdapter.FoldersListAdapter;

import java.util.List;

/**
 * Main view for folders list
 */
public class FoldersView extends RelativeLayout
{
    private ListView foldersList;           // List with folders
    private ProgressBar previewProgressBar;

    private final IActivityFoldersActions actions;

    private FoldersListAdapter listAdapter;

    private FoldersTree foldersTree;

    private Activity context;

    public FoldersView(Activity context, int layoutId, IActivityFoldersActions actions, FoldersTree foldersTree)
    {
        super(context);

        this.context = context;
        this.actions =actions;
        this.foldersTree = foldersTree;

        inflate(context, layoutId, this);
        foldersList = (ListView) findViewById(R.id.lvFolders);
        previewProgressBar=(ProgressBar)findViewById(R.id.previewProgressBar);
    }

    public void updateDiskItems(List<DiskItemInfo> diskItems)
    {
        listAdapter = new FoldersListAdapter(context, diskItems, actions, foldersTree);
        foldersList.setAdapter(listAdapter);
    }

    public void showProgress()
    {
        previewProgressBar.show();
    }

    public void hideProgress()
    {
        previewProgressBar.hide();
    }
}
