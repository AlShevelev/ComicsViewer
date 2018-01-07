package com.syleiman.comicsviewer.Activities.Main.OneComicsWorking;

import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations.CreateComicsOperation;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations.DeleteComicsOperation;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations.EditComicsOperation;
import com.syleiman.comicsviewer.Activities.Main.OneComicsWorking.Operations.ViewComicsOperation;

/**
 * Facade for working with one concrete comics
 */
public class ComicsWorkingFacade
{
    public EditComicsOperation edit;
    public DeleteComicsOperation delete;
    public ViewComicsOperation view;
    public CreateComicsOperation create;

    public ComicsWorkingFacade(IOneComicsActivity activity)
    {
        edit = new EditComicsOperation(activity);
        delete = new DeleteComicsOperation(activity);
        view = new ViewComicsOperation(activity);
        create = new CreateComicsOperation(activity);
    }
}
