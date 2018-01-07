package com.syleiman.comicsviewer.Activities.ComicsCreation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.ActivityCodes;
import com.syleiman.comicsviewer.Activities.ActivityResultCodes;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.AreaOnDragListener;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.ListItemDragCreator;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.LinearLayoutDrag;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.ListDragAdapter;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.ListItemDrag;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.ListItemDragingInfo;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.ListItemLongClickListener;
import com.syleiman.comicsviewer.Activities.ComicsCreation.DragLists.ListItemOnClickTouchListener;
import com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails.ThumbnailListIds;
import com.syleiman.comicsviewer.Activities.ComicsCreation.Thumbnails.ThumbnailManager;
import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.BookcaseComics;
import com.syleiman.comicsviewer.Activities.UserActionsManager;
import com.syleiman.comicsviewer.Activities.ViewComics.ActivityParamCodes;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionTwoArgs;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.CustomControls.ProgressBar;
import com.syleiman.comicsviewer.Common.Dialogs.ZoomedPagePreviewDialog;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.FolderInfo;
import com.syleiman.comicsviewer.Common.ListViewHelper;
import com.syleiman.comicsviewer.Common.Structs.Size;
import com.syleiman.comicsviewer.Common.Threads.CancelationToken;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for sorting pages on comics creation
 */
public class SortPagesActivity extends ActionBarActivity implements ISortPagesActivityItemsEvents
{
    List<ListItemDrag> items1, items2;
    ListView listView1, listView2;
    ListDragAdapter myItemsListAdapter1, myItemsListAdapter2;
    LinearLayoutDrag area1, area2;

    private ProgressBar previewProgressBar;

    private SortPagesMenuManager menuManager;

    int resumeColor;            //Used to resume original color in drop ended/exited

    private CreatePreviewTask createPreviewTask;

    private boolean isInited=false;         // Activiti initialized

    private SortPagesActivityView view;

    private UserActionsManager userActionsManager;

    private ThumbnailManager thumbnailManager;          // to load pages images dynamicly

    //region CreatePreviewTask - Making preview in background thread
    /**
     * Making preview in background thread
     */
    class CreatePreviewTask extends AsyncTask<Boolean, Integer, Void>
    {
        private String pathToFolder;
        private Size displaySize;
        private Paint textPaint;

        private boolean isSuccess;
        private boolean firstTime;

        private CancelationToken cancelationToken=new CancelationToken();

        private ListItemDragCreator listItemDragCreator;

        public void stopIfNeeded()
        {
            if(getStatus()!=Status.FINISHED)
            {
                this.cancel(true);
                cancelationToken.cancel();
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            previewProgressBar.show();

            userActionsManager.lock();

            isSuccess =false;

            pathToFolder =getIntent().getExtras().getString(ActivityParamCodes.PathToFolder);

            DisplayMetrics metrics=getResources().getDisplayMetrics();
            displaySize =new Size(metrics.widthPixels, metrics.heightPixels);

            LayoutInflater inflater = (LayoutInflater)getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate(R.layout.sort_pages_list_item, null);
            TextView textView=(TextView)view.findViewById(R.id.rowTextView);
            textPaint=textView.getPaint();              // Paint of text area for measuring text size

            listItemDragCreator = new ListItemDragCreator(displaySize, textPaint);

            thumbnailManager = new ThumbnailManager(listItemDragCreator, SortPagesActivity.this::onStopProcessingThumbnails);
        }

        @Override
        protected Void doInBackground(Boolean... params)
        {
            try
            {
                if(cancelationToken.isCanceled())
                    return null;

                firstTime=params[0];

                List<ListItemDrag> calculatedItems=createListsItems(pathToFolder);

                items1 = CollectionsHelper.transform(calculatedItems, (i) -> i, cancelationToken);           // Equals lists
                items2 = CollectionsHelper.transform(calculatedItems, (i) -> i, cancelationToken);

                thumbnailManager.warmUpCaches(items1);

                isSuccess =true;
            }
            catch(Exception e) {
                Log.e("", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if(cancelationToken.isCanceled())
                return;

            if(isSuccess)
            {
                thumbnailManager.start();

                myItemsListAdapter1 = new ListDragAdapter(SortPagesActivity.this, items1, R.color.drag, thumbnailManager, SortPagesActivity.this::onDragToList, ThumbnailListIds.Left);
                myItemsListAdapter2 = new ListDragAdapter(SortPagesActivity.this, items2, R.color.drag, thumbnailManager, SortPagesActivity.this::onDragToList, ThumbnailListIds.Right);
                listView1.setAdapter(myItemsListAdapter1);
                listView2.setAdapter(myItemsListAdapter2);
            }
            else
                ToastsHelper.Show(
                        R.string.message_box_no_cant_create_preview,
                        ToastsHelper.Duration.Long,
                        ToastsHelper.Position.Center);

            previewProgressBar.hide();

            if(firstTime)
                ToastsHelper.Show(
                        R.string.message_sorting_tip,           // Show tip about way of sorting
                        ToastsHelper.Duration.Short,
                        ToastsHelper.Position.Bottom);

            isInited=true;

            userActionsManager.unlock();
        }

        /**
         * @return
         */
        private List<ListItemDrag> createListsItems(String pathToFolderk)
        {
            ArrayList<ListItemDrag> result=new ArrayList<ListItemDrag>();

            if(cancelationToken.isCanceled())
                return result;

            FolderInfo folderInfo=new FolderInfo(pathToFolder);
            List<DiskItemInfo> images=folderInfo.getImages();

            int totalImages = images.size();

            for(int i=0; i < totalImages; i++)
            {
                if(cancelationToken.isCanceled())
                    break;

                result.add(listItemDragCreator.create(images.get(i)));
            }

            return result;
        }
    }
    //endregion

    /**
     * Start activity
     */
    public static void start(Activity parentActivity, String pathToFolder)
    {
        Intent intent = new Intent(parentActivity, SortPagesActivity.class);

        Bundle b = new Bundle();
        b.putString(ActivityParamCodes.PathToFolder, pathToFolder);
        intent.putExtras(b);

        parentActivity.startActivityForResult(intent, ActivityCodes.SortPagesActivity);
    }

    /**
     * Parse result of activity
     */
    public static int[] parseResult(Intent data)
    {
        return data.getIntArrayExtra(ActivityResultCodes.idOfPages);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        userActionsManager = new UserActionsManager(this);

        view=new SortPagesActivityView(this, R.layout.activity_sort_pages);
        setContentView(view);

        previewProgressBar=(ProgressBar)findViewById(R.id.previewProgressBar);

        listView1 = (ListView)findViewById(R.id.listviewLeft);
        listView1.setDivider(null);
        listView1.setDividerHeight(0);

        listView2 = (ListView)findViewById(R.id.listviewRight);
        listView2.setDivider(null);
        listView2.setDividerHeight(0);

        area1 = (LinearLayoutDrag)findViewById(R.id.panelLeft);
        area2 = (LinearLayoutDrag)findViewById(R.id.panelRight);

        area1.setOnDragListener(new AreaOnDragListener((dragInfo) -> { onDragToArea(dragInfo); }));
        area2.setOnDragListener(new AreaOnDragListener((dragInfo) -> { onDragToArea(dragInfo); }));

        area1.setListView(listView1);
        area2.setListView(listView2);

        ListItemOnClickTouchListener listener=new ListItemOnClickTouchListener(SortPagesActivity.this);
        listView1.setOnItemClickListener(listener);
        listView1.setOnTouchListener(listener);
        listView2.setOnItemClickListener(listener);
        listView2.setOnTouchListener(listener);

        listView1.setOnItemLongClickListener(new ListItemLongClickListener());
        listView2.setOnItemLongClickListener(new ListItemLongClickListener());

        resumeColor  = getResources().getColor(android.R.color.background_light);

        createPreviewTask = new CreatePreviewTask();
        createPreviewTask.execute(savedInstanceState == null);          // Create preview in background thread
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(createPreviewTask!=null)
            createPreviewTask.stopIfNeeded();

        outState.putBoolean("isRestarted", true);
    }

    /**
     * Draging from list to empty area
     */
    private void onDragToArea(ListItemDragingInfo dragInfo)
    {
        dragInfo.getSourceList().remove(dragInfo.getDraggedItem());
        dragInfo.getDestinationList().remove(dragInfo.getDraggedItem());

        dragInfo.getSourceList().add(dragInfo.getDraggedItem());
        dragInfo.getDestinationList().add(dragInfo.getDraggedItem());

        dragInfo.getSourceAdapter().notifyDataSetChanged();
        dragInfo.getDestinationAdapter().notifyDataSetChanged();
    }

    /**
     * Draging from list to list
     */
    public void onDragToList(ListItemDragingInfo dragInfo)
    {
        List<ListItemDrag> srcList = dragInfo.getSourceList();
        List<ListItemDrag> destList = dragInfo.getDestinationList();

        ListDragAdapter srcAdapter=dragInfo.getSourceAdapter();
        ListDragAdapter destAdapter=dragInfo.getDestinationAdapter();

        if(srcList == destList)
        {
            destList = srcList == items1 ? items2 : items1;
            destAdapter = srcAdapter == myItemsListAdapter1 ? myItemsListAdapter2 : myItemsListAdapter1;
        }

        srcList.remove(dragInfo.getDraggedItem());
        destList.remove(dragInfo.getDraggedItem());

        srcList.add(dragInfo.getDestinationLocation(), dragInfo.getDraggedItem());
        destList.add(dragInfo.getDestinationLocation(), dragInfo.getDraggedItem());

        srcAdapter.notifyDataSetChanged();
        destAdapter.notifyDataSetChanged();
    }

    /**
     * When user click on item's zoom icon
     */
    @Override
    public void onZoomItem(int itemIndex)
    {
        ListItemDrag item=items1.get(itemIndex);            // List have same orders of items

        ZoomedPagePreviewDialog dialog=new ZoomedPagePreviewDialog(
                this,
                item.getFullPathToImageFile(),
                view.getSize().scale(0.9f));
        dialog.show();
    }

    @Override
    public void onSetVisibilityItem(int itemIndex)
    {
        ListItemDrag item=items1.get(itemIndex);            // Both list contant same items so we must update only one
        item.setIsVisibile(!item.getIsVisibile());

        ListViewHelper.invalidateListItem(itemIndex, listView1);
        ListViewHelper.invalidateListItem(itemIndex, listView2);

        menuManager.setAcceptVisible(CollectionsHelper.any(items1, (i) -> i.getIsVisibile()));      // all items hided - hide accept icon
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sort_pages, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuManager = new SortPagesMenuManager(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Menu on Action bar handlers
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_close:
            {
                thumbnailManager.stop(false);
                return true;
            }
            case R.id.action_accept:
            {
                thumbnailManager.stop(true);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *When user press back button
     */
    @Override
    public void onBackPressed()
    {
        if(!isInited)
            return;

        thumbnailManager.stop(false);
    }

    /**
     * When thumbnail processing stopedv - finish work
     */
    private void onStopProcessingThumbnails(Object dataToReturn)
    {
        if((Boolean)dataToReturn)
            closeOk();
        else
            closeCancel();          // Close activity if Back pressed
    }

    /**
     * Close with Cancel result
     */
    private void closeCancel()
    {
        if(!isInited)
            return;

        Intent intent = new Intent();           // Close current activity
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    /**
     * Close with Ok result
     */
    private void closeOk()
    {
        if(!isInited)
            return;

        List<Integer> idsList=CollectionsHelper.transform(
                CollectionsHelper.where(items1, (item) -> item.getIsVisibile()),
                (item)->item.getId());

        int[] ids=new int[idsList.size()];
        for(int i=0; i<ids.length; i++)
            ids[i]=idsList.get(i);

        Intent intent = new Intent();                       // Choose folder and close activity
        intent.putExtra(ActivityResultCodes.idOfPages, ids);
        setResult(RESULT_OK, intent);
        finish();
    }
}