package com.syleiman.comicsviewer.Activities.MainOptions;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.ActivityCodes;
import com.syleiman.comicsviewer.Activities.ActivityResultCodes;
import com.syleiman.comicsviewer.Common.Dialogs.CreatePasswordDialog;
import com.syleiman.comicsviewer.Common.Dialogs.EnterPasswordDialog;
import com.syleiman.comicsviewer.Common.Helpers.ToastsHelper;
import com.syleiman.comicsviewer.Dal.Dto.Option;
import com.syleiman.comicsviewer.Options.OptionsFacade;
import com.syleiman.comicsviewer.Options.OptionsKeys;
import com.syleiman.comicsviewer.Options.OptionsValues;

public class MainOptionsActivity extends Activity
{
    private LinearLayout createPasswordControl;
    private LinearLayout enterPasswordControl;
    private LinearLayout changePasswordControl;

    private boolean passwordEnteredOnStart;

    /**
     * Start activity
     */
    public static void start(Activity parentActivity)
    {
        Intent intent = new Intent(parentActivity, MainOptionsActivity.class);
        parentActivity.startActivityForResult(intent, ActivityCodes.MainOptionsActivity);
    }

    /**
     * Parse result of activity
     */
    public static boolean parseResult(Intent data)
    {
        return data.getBooleanExtra(ActivityResultCodes.isPasswordEntered, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_options);

        createPasswordControl = (LinearLayout)findViewById(R.id.createPassword);
        enterPasswordControl = (LinearLayout)findViewById(R.id.enterPassword);
        changePasswordControl = (LinearLayout)findViewById(R.id.changePassword);

        createPasswordControl.setOnClickListener(v -> { onCreatePasswordClick(); });
        enterPasswordControl.setOnClickListener(v -> {
            onEnterPasswordClick();
        });
        changePasswordControl.setOnClickListener(v -> {
            onChangePasswordClick();
        });

        setPasswordControlsVisibility();

        passwordEnteredOnStart = OptionsFacade.ShortLivings.get(OptionsKeys.PasswordEntered)!=null;
    }

    private void setPasswordControlsVisibility()
    {
        createPasswordControl.setVisibility(View.GONE);
        enterPasswordControl.setVisibility(View.GONE);
        changePasswordControl.setVisibility(View.GONE);

        if(OptionsFacade.LongLivings.get(OptionsKeys.Password)!=null)
        {
            if(OptionsFacade.ShortLivings.get(OptionsKeys.PasswordEntered)!=null)
                changePasswordControl.setVisibility(View.VISIBLE);
            else
                enterPasswordControl.setVisibility(View.VISIBLE);
        }
        else
            createPasswordControl.setVisibility(View.VISIBLE);
    }

    private void onCreatePasswordClick()
    {
        CreatePasswordDialog dialog=new CreatePasswordDialog(
            this,
            (result)->
            {
                OptionsFacade.LongLivings.addOrUpdate(new Option[]{new Option(OptionsKeys.Password, result.password), new Option(OptionsKeys.PasswordsHint, result.hint)});
                OptionsFacade.ShortLivings.addOrUpdate(new Option[]{new Option(OptionsKeys.PasswordEntered, OptionsValues.True)});

                setPasswordControlsVisibility();
            },
            ()-> { });

        dialog.show();
    }

    private void onEnterPasswordClick()
    {
        String password = OptionsFacade.LongLivings.get(OptionsKeys.Password);
        String hint = OptionsFacade.LongLivings.get(OptionsKeys.PasswordsHint);

        EnterPasswordDialog dialog=new EnterPasswordDialog(
            this,
            (result)->
            {
                if(result.equals(password))
                {
                    OptionsFacade.ShortLivings.addOrUpdate(new Option[]{new Option(OptionsKeys.PasswordEntered, OptionsValues.True)});
                    setPasswordControlsVisibility();
                }
                else
                    ToastsHelper.Show(R.string.message_invalid_password, ToastsHelper.Position.Center);
            },
            ()->
            { }, hint);

        dialog.show();
    }

    private void onChangePasswordClick()
    {
        String password = OptionsFacade.LongLivings.get(OptionsKeys.Password);

        CreatePasswordDialog dialog=new CreatePasswordDialog(
            this,
            (result)->
            {
                OptionsFacade.LongLivings.addOrUpdate(new Option[]{new Option(OptionsKeys.Password, result.password), new Option(OptionsKeys.PasswordsHint, result.hint)});
            },
            ()-> { }, R.string.dialog_change_password_title, R.layout.dialog_create_password, password);

        dialog.show();
    }

    @Override
    public void onBackPressed()
    {
        boolean passwordEnteredOnFinish = OptionsFacade.ShortLivings.get(OptionsKeys.PasswordEntered)!=null;

        Intent intent = new Intent();
        intent.putExtra(ActivityResultCodes.isPasswordEntered, !passwordEnteredOnStart && passwordEnteredOnFinish);         // User enters or creates password
        setResult(RESULT_OK, intent);

        super.onBackPressed();              // call finish() and close acivity
    }
}