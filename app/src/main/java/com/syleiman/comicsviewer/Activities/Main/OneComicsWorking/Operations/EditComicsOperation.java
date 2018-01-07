package com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations;

import android.app.Activity;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.IOneComicsActivity;
import com.syleiman.comicsviewer.Common.Dialogs.ComicsNameDialog;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;
import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Comics;

/**
 * Operation for edit comics
 */
public class EditComicsOperation extends ComicsOperationBase
{
    public EditComicsOperation(IOneComicsActivity activity)
    {
        super(activity);
    }

    public void start(long comicsId)
    {
        Comics comics = DalFacade.Comics.getComicsById(comicsId);
        ComicsNameDialog dialog=new ComicsNameDialog(
                context,
                (result)->{             // We get name here
                    boolean updateResult=DalFacade.Comics.updateNameAndHidden(comicsId, result.title, result.isPrivate);
                    if(updateResult)
                        uiMethods.updateBooksList(null);
                    else
                        ToastsHelper.Show(R.string.message_cant_update_comics_title, ToastsHelper.Position.Center);
                },
                ()->{
                    // That's it - final
                }, R.string.dialog_comics_name_title, R.layout.dialog_enter_name, new ComicsNameDialog.Model(comics.name, comics.isPrivate));
        dialog.show();
    }
}
