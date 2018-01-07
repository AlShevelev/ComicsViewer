package com.syleiman.comicsviewer.Common.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.App;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;

/**
 * Create message boxes
 */
public class MessageBoxHelper
{
    /**
     * Simple message box with Ok button
     */
    public static Dialog createOkDialog(Context context, String title, String message, IActionZeroArgs okAction)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);

        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton(context.getString(R.string.message_box_ok_button), (dialog, which) -> {
            if(okAction!=null)
                okAction.process();
        });
        dlgAlert.setCancelable(false);
        return dlgAlert.create();
    }

    /**
     * Simple message box with Yes and No buttons
     */
    public static Dialog createYesNoDialog(Context context, String title, String message, IActionZeroArgs yesAction, IActionZeroArgs noAction)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);

        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);

        dlgAlert.setPositiveButton(context.getString(R.string.message_box_yes_button), (dialog, which) -> {
            if (yesAction != null)
                yesAction.process();
        });
        dlgAlert.setNegativeButton(context.getString(R.string.message_box_no_button), (dialog, which) -> {
            if (noAction != null)
                noAction.process();
        });

        dlgAlert.setCancelable(true);
        return dlgAlert.create();
    }
}
