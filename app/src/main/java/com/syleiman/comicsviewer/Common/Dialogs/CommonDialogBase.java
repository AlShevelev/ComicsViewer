package com.syleiman.comicsviewer.Common.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;

/**
 * Base class for common dialogs
 * @param <TI> input model
 * @param <TO> output model
 */
abstract class CommonDialogBase<TI, TO>
{
    protected Button okButton;
    protected Button cancelButton;

    private Activity parentActivity;

    private IActionOneArgs<TO> okAction;
    private IActionZeroArgs cancelAction;

    private String title;

    private int dialogLayoutId;

    private TI initModel;                // Model to init dialog

    protected CommonDialogBase(
            Activity parentActivity,
            IActionOneArgs<TO> okAction,
            IActionZeroArgs cancelAction,
            int titleResourceId,
            int dialogLayoutId,
            TI initModel)
    {
        this.parentActivity = parentActivity;
        this.okAction = okAction;
        this.cancelAction = cancelAction;
        this.title = getStringFromResources(titleResourceId);
        this.dialogLayoutId = dialogLayoutId;
        this.initModel = initModel;
    }

    protected String getStringFromResources(int resourceId)
    {
        return App.getContext().getString(resourceId);
    }

    private AlertDialog create()
    {
        LayoutInflater inflater = parentActivity.getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTitle(title);

        View view=inflater.inflate(dialogLayoutId, null);

        initControls(view, initModel);

        builder = builder.setView(view);
        setOkButton(builder, R.string.message_box_ok_button);
        setCancelButton(builder, R.string.message_box_cancel_button);

        return builder.create();
    }

    protected abstract void initControls(View view, TI initModel);

    protected abstract TO getOutputModel();

    protected void setOkButton(AlertDialog.Builder builder, int buttonTextResourceId)
    {
        builder.setPositiveButton(buttonTextResourceId, (dialog, which) -> {
            okAction.process(getOutputModel());
        });
    }

    protected void setCancelButton(AlertDialog.Builder builder, int buttonTextResourceId)
    {
        builder.setNegativeButton(buttonTextResourceId, (dialog, which) -> {
            if(cancelAction!=null)
                cancelAction.process();
        });
    }

    public void show()
    {
        AlertDialog dialog = create();
        dialog.show();

        okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);         // Buttons are accessible only after show()
        cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        afterButtonsInited();
    }

    protected void afterButtonsInited()
    {
        return;
    }
}