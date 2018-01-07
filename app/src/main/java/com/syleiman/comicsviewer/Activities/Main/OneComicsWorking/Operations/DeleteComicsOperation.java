package com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.IOneComicsActivity;
import com.syleiman.comicsviewer.ComicsWorkers.ComicsDeletor;
import com.syleiman.comicsviewer.Common.Dialogs.MessageBoxHelper;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;
import com.syleiman.comicsviewer.Common.Rhea.RheaFacade;

/**
 * Operation for delete comics
 */
public class DeleteComicsOperation extends ComicsOperationBase
{
    public DeleteComicsOperation(IOneComicsActivity activity)
    {
        super(activity);
    }

    public void start(long comicsId)
    {
        MessageBoxHelper.createYesNoDialog(context, context.getString(R.string.message_box_delete_query_title), context.getString(R.string.message_box_delete_query),
            () ->
            {
                uiMethods.setUserActionsLock(true);
                uiMethods.setProgressState(true);
                RheaFacade.run(context, new ComicsDeletor(ComicsDeletor.tag, comicsId));

            }, null).show();
    }

    public void complete()
    {
        uiMethods.setProgressState(false);             // this execute after comics creation
        uiMethods.updateBooksList(null);
        uiMethods.setUserActionsLock(false);
    }

    public void completeWithError()
    {
        ToastsHelper.Show(R.string.message_cant_delete_comics_title, ToastsHelper.Position.Center);
    }
}
