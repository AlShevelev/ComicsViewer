package com.syleiman.comicsviewer.Activities.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.ActivityCodes;
import com.syleiman.comicsviewer.Activities.ComicsCreation.SortPagesActivity;
import com.syleiman.comicsviewer.Activities.Folders.ChooseFolderActivity;
import com.syleiman.comicsviewer.Activities.Main.Bookshelf.BookshelfComicsInfo;
import com.syleiman.comicsviewer.Activities.Main.Bookshelf.BookshelfComicsReader;
import com.syleiman.comicsviewer.Activities.Main.Bookshelf.ComicsControl.ComicsContextMenuInfo;
import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.ComicsFilterFactory;
import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.ComicsViewMode;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.ComicsWorkingFacade;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.IOneComicsActivity;
import com.syleiman.comicsviewer.Activities.MainOptions.MainOptionsActivity;
import com.syleiman.comicsviewer.Activities.UserActionsManager;
import com.syleiman.comicsviewer.Activities.ViewComics.CurlActivity;
import com.syleiman.comicsviewer.ComicsWorkers.ComicsCreator;
import com.syleiman.comicsviewer.ComicsWorkers.ComicsDeletor;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Common.Helpers.ScreenHelper;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;
import com.syleiman.comicsviewer.Common.Rhea.IRheaActivity;
import com.syleiman.comicsviewer.Common.Rhea.RheaFacade;
import com.syleiman.comicsviewer.Common.Rhea.RheaOperationProgressInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements IRheaActivity, IOneComicsActivity
{
    private BookshelfView view;

    private ChangeModeHandler changeModeHandler;      // Handle changing view of comics (All <--> Recent)

    private Long choosedContextComicsId;            // Id of comics choosed by context menu

    private UserActionsManager userActionsManager;          // For lock/unlock screen while user actions

    private ComicsWorkingFacade comicsWorkingFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RheaFacade.onCreate(this);

        userActionsManager=new UserActionsManager(this);

        changeModeHandler=new ChangeModeHandler(ComicsViewMode.All, (mode) -> comicsViewModeChanged(mode));

        view = new BookshelfView(this, R.layout.activity_main, changeModeHandler, (dbComicsId) -> onComicsChoosen(dbComicsId));
        setContentView(view);

        comicsWorkingFacade = new ComicsWorkingFacade(this);

        updateBooksList(null);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RheaFacade.onDestroy(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        RheaFacade.onPause(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        RheaFacade.onResume(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        RheaFacade.onSaveInstanceState(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);  // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    /**
     * Launch activity for choosing folder
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(userActionsManager.isActionsBlocked())
            return super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.action_add:
            {
                ChooseFolderActivity.start(this);                   // Choose folder with comics
                return true;
            }
            case R.id.action_menu:
            {
                MainOptionsActivity.start(this);            // Show main innerOptionsCollection
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        ComicsContextMenuInfo contextMenuInfo = (ComicsContextMenuInfo)menuInfo;
        choosedContextComicsId = contextMenuInfo.dbId;

        getMenuInflater().inflate(R.menu.context_comics, menu);
        menu.setHeaderTitle(contextMenuInfo.menuTitle);
    }

    public boolean onContextItemSelected(MenuItem item)
    {
        if(userActionsManager.isActionsBlocked())
            super.onContextItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.comics_delete: comicsWorkingFacade.delete.start(choosedContextComicsId); break;
            case R.id.comics_edit: comicsWorkingFacade.edit.start(choosedContextComicsId); break;
        }

        return super.onContextItemSelected(item);
    }

    /**
     *  Processing result from child activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data == null  || resultCode!=RESULT_OK)
            return;

        switch (requestCode)
        {
            case ActivityCodes.ChooseFolderActivity: comicsWorkingFacade.create.preStart(ChooseFolderActivity.parseResult(data)); break;        // Folder was choosen - start to createFromFile comics
            case ActivityCodes.SortPagesActivity: comicsWorkingFacade.create.start(SortPagesActivity.parseResult(data)); break;     // Start create comics
            case ActivityCodes.CurlActivity: comicsWorkingFacade.view.complete(CurlActivity.parseResult(data)); break;      // Complete view comics
            case ActivityCodes.MainOptionsActivity:
            {
                if(MainOptionsActivity.parseResult(data))                         // Only if user enter password
                    updateBooksList(null);
                break;
            }
        }
    }

    /**
     * When user clicks on comics on shelve
     * @param dbComicsId - id of choosen comics
     */
    private void onComicsChoosen(long dbComicsId)
    {
        comicsWorkingFacade.view.start(dbComicsId);     // Start view comics
    }

    /**
     * Read comics and put them on the shelfs - in background thread
     * @param idOfComicsToScroll - id of comics to scroll to (if null - without scrolling)
     */
    @Override
    public void updateBooksList(Long idOfComicsToScroll)
    {
        BookshelfComicsReader reader=new BookshelfComicsReader(ComicsFilterFactory.getFilter(changeModeHandler.getViewMode()),
            () ->
            {
                userActionsManager.lock();
                view.showProgress();
            },
            (comics) ->
            {
                if(comics!=null)
                {
                    view.setBooks(comics);
                    if(idOfComicsToScroll!=null)
                        view.scrollToComics(idOfComicsToScroll);
                }
                else
                    ToastsHelper.Show(R.string.message_cant_read_comics, ToastsHelper.Position.Center);

                view.hideProgress();
                userActionsManager.unlock();
            }, ScreenHelper.getClientSize(this));
        reader.execute();
    }

    /**
     * Turn on/off user actios
     * @param isLocked if true - userActions locked
     */
    @Override
    public void setUserActionsLock(boolean isLocked)
    {
        if(isLocked)
            userActionsManager.lock();
        else
            userActionsManager.unlock();
    }

    /**
     * Is user actios turn on/off
     * @return true - actions locked
     */
    @Override
    public boolean isUserActionsLock()
    {
        return userActionsManager.isActionsBlocked();
    }

    /**
     * Show/hide progress
     * @param isVisible if true progress'll showed
     */
    @Override
    public void setProgressState(boolean isVisible)
    {
        if(isVisible)
            view.showProgress();
        else
            view.hideProgress();
    }

    /**
     * Update text of progress control
     * @param text
     */
    @Override
    public void updateProgressText(String text)
    {
        this.view.setProgressText(text);
    }

    /**
     * Set view mode for bookcase (all comics or only recent)
     * @param viewMode
     */
    @Override
    public void setViewMode(ComicsViewMode viewMode)
    {
        changeModeHandler.setViewMode(viewMode);
    }

    /**
     * When view mode of comics was changed
      */
    private void comicsViewModeChanged(ComicsViewMode mode)
    {
        updateBooksList(null);
    }

    /**
     * Work completed successfully
     */
    @Override
    public void onRheaWorkCompleted(String tag, Object result)
    {
        switch (tag)
        {
            case ComicsCreator.tag: comicsWorkingFacade.create.complete(result); break;
            case ComicsDeletor.tag: comicsWorkingFacade.delete.complete(); break;
        }
    }

    /**
     * There was an error while working
     */
    @Override
    public void onRheaWorkCompletedByError(String tag, Exception exception)
    {
        switch (tag)
        {
            case ComicsCreator.tag: comicsWorkingFacade.create.completeWithError(); break;
            case ComicsDeletor.tag: comicsWorkingFacade.delete.completeWithError(); break;
        }
    }

    /**
     * Show work progress
     */
    @Override
    public void onRheaWorkProgress(String tag, RheaOperationProgressInfo progressInfo)
    {
        if(tag.equals(ComicsCreator.tag))           // Comics creation completed successfully
            comicsWorkingFacade.create.updateProgress(progressInfo);
    }

    /**
     * Call when activity restarts for every not completed work (so we should init view here)
     */
    @Override
    public void onRheaWorkInit(String tag, RheaOperationProgressInfo progressInfo)
    {
        if(tag.equals(ComicsCreator.tag))
            comicsWorkingFacade.create.initOnRestart(progressInfo);
    }
}