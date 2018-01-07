package com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.ComicsCreation.ChooseComicsName;
import com.syleiman.comicsviewer.Activities.ComicsCreation.PagesStartSorter;
import com.syleiman.comicsviewer.Activities.ComicsCreation.SortPagesActivity;
import com.syleiman.comicsviewer.Activities.Folders.ChooseFolderActivity;
import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.ComicsViewMode;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.IOneComicsActivity;
import com.syleiman.comicsviewer.ComicsWorkers.ComicsCreator;
import com.syleiman.comicsviewer.Common.Helpers.Files.FileSystemItems.DiskItemInfo;
import com.syleiman.comicsviewer.Common.Helpers.ScreenHelper;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;
import com.syleiman.comicsviewer.Common.Rhea.RheaFacade;
import com.syleiman.comicsviewer.Common.Rhea.RheaOperationProgressInfo;

import java.util.List;

/**
 * Operation for create comics
 */
public class CreateComicsOperation extends ComicsOperationBase
{
    private ChooseComicsName chooseComicsName;
    private ComicsCreator comicsCreator;

    public CreateComicsOperation(IOneComicsActivity activity)
    {
        super(activity);
    }

    public void preStart(String pathToFolder)
    {
        chooseComicsName = new ChooseComicsName(context, (name, isPrivate, pathToComicsFolder) -> onComicsNameChoose(name, isPrivate, pathToComicsFolder));
        chooseComicsName.startCreate(pathToFolder);        // Choose comics name
    }

    public void start(int[] diskItemsSortedIds)
    {
        comicsCreator.setDiskItems(diskItemsSortedIds);

        uiMethods.setUserActionsLock(true);
        uiMethods.setProgressState(true);

        RheaFacade.run(context, comicsCreator);
    }

    public void complete(Object result)
    {
        uiMethods.setProgressState(false);

        if(result==null)              // Shit happends
            ToastsHelper.Show(R.string.message_cant_create_comics_title, ToastsHelper.Position.Center);
        else
        {
            uiMethods.setViewMode(ComicsViewMode.All);
            uiMethods.updateBooksList((Long)result);
        }
        uiMethods.setUserActionsLock(false);
    }

    public void completeWithError()
    {
        ToastsHelper.Show(R.string.message_cant_create_comics_title, ToastsHelper.Position.Center);
    }

    public void updateProgress(RheaOperationProgressInfo progressInfo)
    {
        uiMethods.updateProgressText(String.format(context.getResources().getText(R.string.pages_processing_progress).toString(), progressInfo.value, progressInfo.total));
    }

    public void initOnRestart(RheaOperationProgressInfo progressInfo)
    {
        uiMethods.setUserActionsLock(true);
        uiMethods.setProgressState(true);
        updateProgress(progressInfo);
    }

    /**
     * When we choose comics name
     * @param name - name of comics
     * @param pathToFolder - path to comics folder
     */
    private void onComicsNameChoose(String name, boolean isPrivateComics, String pathToFolder)
    {
        List<DiskItemInfo> images= PagesStartSorter.Sort(pathToFolder);

        comicsCreator=new ComicsCreator(ComicsCreator.tag, name, isPrivateComics, images, ScreenHelper.getClientSize(context));

        SortPagesActivity.start(context, pathToFolder);                // Start pages sorting
    }
}
