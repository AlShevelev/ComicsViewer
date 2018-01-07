package com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree;

import android.os.Bundle;

import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Threads.CancelationToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Creates tree of folders
 */
public class FoldersTree
{
    private FoldersTreeInnerState innerState;

    private CancelationToken cancelationToken;

    public FoldersTree()
    {
        innerState=new FoldersTreeInnerState();
        cancelationToken=new CancelationToken();
    }

    public void cancel()
    {
        cancelationToken.cancel();
    }

    public List<FoldersTreeItem> Create()
    {
        if(cancelationToken.isCanceled())
            return null;

        if(innerState.tree==null)
        {
            IFoldersTreeItemsGetter rootGetter=new FoldersTreeRootItemsGetter();
            List<FoldersTreeItem> tempRootItems=rootGetter.getSubItems(cancelationToken);

            if(CollectionsHelper.isNullOrEmpty(tempRootItems))
                innerState.tree = new ArrayList<FoldersTreeItem>(0);
            else
            {
                innerState.tree = new ArrayList<FoldersTreeItem>(tempRootItems.size());

                for(FoldersTreeItem rootItem : tempRootItems)
                {
                    if(cancelationToken.isCanceled())
                        return null;

                    rootItem.init();

                    if(rootItem.getHasImages() || rootItem.getIsActive())
                        innerState.tree.add(rootItem);
                }
            }
        }
        return innerState.tree;
    }

    public HashMap<String, FoldersTreeItem> MakeFlatten()
    {
        if(cancelationToken.isCanceled())
            return null;

        if(innerState.tree==null)
            return null;

        if(innerState.flattenTree==null)
        {
            innerState.flattenTree = new HashMap<>();
            for(FoldersTreeItem treeItem: innerState.tree)
            {
                if(cancelationToken.isCanceled())
                    return null;

                MakeFlattenInternal(treeItem);
            }
        }

        return innerState.flattenTree;
    }

    private void MakeFlattenInternal(FoldersTreeItem treeItem)
    {
        if(cancelationToken.isCanceled())
            return;

        innerState.flattenTree.put(treeItem.getAbsolutePath(), treeItem);

        List<FoldersTreeItem> subItems=treeItem.getSubItems();

        if(!CollectionsHelper.isNullOrEmpty(subItems))
            for(FoldersTreeItem subItem : subItems)
            {
                if(cancelationToken.isCanceled())
                    return;

                MakeFlattenInternal(subItem);
            }
    }

    public void Save(Bundle state)
    {
        state.putParcelable("foldersTree", innerState);
    }

    public void Load(Bundle state)
    {
        innerState = state.getParcelable("foldersTree");

        if(innerState==null)            // Can't read - recreate
            Create();
    }
}
