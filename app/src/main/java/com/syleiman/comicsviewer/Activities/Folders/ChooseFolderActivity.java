package com.syleiman.comicsviewer.Activities.Folders;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.ActivityCodes;
import com.syleiman.comicsviewer.Activities.ActivityResultCodes;
import com.syleiman.comicsviewer.Activities.UserActionsManager;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.DiskItems.DiskItemsNormalProcessor;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.DiskItems.DiskItemsRootProcessor;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.DiskItems.IDiskItemsProcessor;
import com.syleiman.comicsviewer.Activities.Folders.FileSystem.FoldersTree.FoldersTree;

import java.util.List;
import java.util.Stack;

public class ChooseFolderActivity extends ActionBarActivity implements IActivityFoldersActions
{
    private FoldersView view;

    private List<DiskItemInfo> diskItems;          // List of files and folders

    private TextView pathBar =null;

    private Stack<String> foldersInDeep;           // Set of folders in navigation order

    private static final String ROOT_FOLDER="/";            // Constant for root folder

    private ChooseFoldersOptionsMenuManager menuManager;

    private DiskItemInfo choosedDiskItem =null;

    private boolean isInited=false;         // Activiti initialized

    protected FoldersTree foldersTree;

    private UserActionsManager userActionsManager;

    private InitActivityBase initActivityTask;

    //region Init activity
    abstract class InitActivityBase extends AsyncTask<Void, Void, Void>
    {
        public void stopIfNeeded()
        {
            if(getStatus()!=Status.FINISHED)
            {
                this.cancel(true);
                foldersTree.cancel();
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            preExecute();
        }

        protected void preExecute()
        {
            userActionsManager.lock();

            foldersInDeep = new Stack<String>();
            foldersTree = new FoldersTree();

            view = new FoldersView(ChooseFolderActivity.this, R.layout.activity_choose_folder, ChooseFolderActivity.this, foldersTree);
            setContentView(view);

            view.showProgress();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                init();
            }
            catch (Exception e)
            {
                Log.e("", e.toString());
            }

            return null;
        }

        protected abstract void init();

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            postExecute();
        }

        protected void postExecute()
        {
            view.hideProgress();
            showDiskItems(ROOT_FOLDER, true);
            isInited=true;

            userActionsManager.unlock();

            ToastsHelper.Show(
                    R.string.message_choose_folder_tip,           // Show tip about way of sorting
                    ToastsHelper.Duration.Short,
                    ToastsHelper.Position.Bottom);
        }
    }

    /**
     * Initialization of activity if it starts first time
     */
    class InitActivityFirstTime extends InitActivityBase
    {
        @Override
        protected void init()
        {
            foldersTree.Create();
            foldersTree.MakeFlatten();
        }
    }

    /**
     * Initialization of restarting activity
     */
    class InitRestartingActivity extends InitActivityBase
    {
        private Bundle savedState;

        public InitRestartingActivity(Bundle savedState)
        {
            this.savedState=savedState;
        }

        @Override
        protected void init()
        {
            foldersTree.Load(savedState);
            foldersTree.MakeFlatten();
        }

        @Override
        protected void postExecute()
        {
            view.hideProgress();
            showDiskItems(ROOT_FOLDER, true);
            isInited=true;

            userActionsManager.unlock();
        }
    }
    //endregion

    /**
     * Start activity
     */
    public static void start(Activity parentActivity)
    {
        Intent intent = new Intent(parentActivity, ChooseFolderActivity.class);
        parentActivity.startActivityForResult(intent, ActivityCodes.ChooseFolderActivity);
    }

    /**
     * Parse result of activity
     */
    public static String parseResult(Intent data)
    {
        return data.getStringExtra(ActivityResultCodes.PathToFolder);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        userActionsManager = new UserActionsManager(this);

        initActivityTask=null;
        if(savedInstanceState == null)
            initActivityTask=new InitActivityFirstTime();
        else if(!savedInstanceState.getBoolean("isInited"))
            initActivityTask=new InitActivityFirstTime();
        else
            initActivityTask=new InitRestartingActivity(savedInstanceState);
        initActivityTask.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(initActivityTask!=null)
            initActivityTask.stopIfNeeded();

        outState.putBoolean("isInited", isInited);

        if(isInited)            // Save folders tree
            foldersTree.Save(outState);
    }

    /**
     * Show  sub-disk items for path
     * @param path
     */
    private void showDiskItems(String path, boolean memorizePath)
    {
        pathBar =(TextView)findViewById(R.id.tvPath);
        setPathText(path, !path.equals(ROOT_FOLDER));

        IDiskItemsProcessor diskItemsProcessor=path.equals(ROOT_FOLDER) ?
                new DiskItemsRootProcessor() :
                new DiskItemsNormalProcessor(path);

        diskItems = diskItemsProcessor.getDiskItems();

        view.updateDiskItems(diskItems);

        if(memorizePath)
            foldersInDeep.push(path);
    }

    private void setPathText(String path, boolean measure)
    {
        if(measure)
        {
            TextPaint paint = pathBar.getPaint();

            int panelWidth = pathBar.getMeasuredWidth();
            int textWidth = (int) paint.measureText(path);

            while (textWidth >= panelWidth)
            {
                path = path.substring(1);
                textWidth = (int) paint.measureText(path);
            }
        }

        pathBar.setText(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_choose_folder, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuManager = new ChooseFoldersOptionsMenuManager(menu);
        menuManager.setVisible(true);
        menuManager.setAcceptVisible(false);

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
            case R.id.action_back:          // Back to prior folder or close current activity
            {
                onBackPressed();
                return true;
            }
            case R.id.action_close:
            {
                closeCancel();
                return true;
            }
            case R.id.action_choose:
            {
                closeOk(choosedDiskItem.getAbsolutePath());
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Close with cancelation
     */
    private void closeCancel()
    {
        if(!isInited)
            return;

        Intent intent = new Intent();           // Close current activity
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void closeOk(String pathToFolder)
    {
        if(!isInited)
            return;

        Intent intent = new Intent();                       // Choose folder and close activity
        intent.putExtra(ActivityResultCodes.PathToFolder, pathToFolder);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     *When user press back button
     */
    @Override
    public void onBackPressed()
    {
        if(!isInited)
            return;

        foldersInDeep.pop();

        if(foldersInDeep.empty())            // If we are in root folder - close activity
            super.onBackPressed();
        else
            showDiskItems(foldersInDeep.peek(), false);        // Show items of parent level
    }

    /**
     * Folder's ckeckbox was changed by User
     * @param id Id of folder
     */
    public void FolderCheckChanged(int id, boolean isChecked)
    {
        menuManager.setAcceptVisible(isChecked);        // Show or hide accept button

        if(isChecked)
            choosedDiskItem = CollectionsHelper.first(diskItems, i -> i.getId() == id);
        else
            choosedDiskItem =null;
    }

    /**
     * User taped on folder
     * @param id Id of folder
     * @param folderWithImages true if this folder containts images
     */
    @Override
    public void FolderTaped(int id, boolean folderWithImages)
    {
        DiskItemInfo diskItem= CollectionsHelper.first(diskItems, i -> i.getId() == id);

        menuManager.setAcceptVisible(false);        // Hide accept button

        choosedDiskItem =null;

        showDiskItems(diskItem.getAbsolutePath(), true);         // show items of this folder

        if(folderWithImages)
            ToastsHelper.Show(R.string.message_tap_to_preview, ToastsHelper.Duration.Short, ToastsHelper.Position.Bottom);
    }
}
