package com.syleiman.comicsviewer.Common.Dialogs;

import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;

/**
 * Dialog for enter comics password
 */
public class EnterPasswordDialog  extends CommonDialogBase<EnterPasswordDialog.Model, String>
{
    private EditText passwordControl;

    /**
     * Model for dialog
     */
    public static final class Model
    {
        public String password;
        public String hint;

        public Model(String password, String hint)
        {
            this.password = password;
            this.hint = hint;
        }
    }

    public EnterPasswordDialog(
            Activity parentActivity,
            IActionOneArgs<String> okAction,
            IActionZeroArgs cancelAction,
            String hint)
    {
        super(parentActivity, okAction, cancelAction, R.string.dialog_enter_password_title, R.layout.dialog_enter_password, new EnterPasswordDialog.Model("", hint));
    }

    @Override
    protected void initControls(View view, EnterPasswordDialog.Model initModel)
    {
        passwordControl =((EditText)view.findViewById(R.id.password));
        passwordControl.setText(initModel.password);
        passwordControl.setSelection(initModel.password.length());           // Move cursor to end

        TextView hintControl=((TextView)view.findViewById(R.id.passwordHint));
        hintControl.setText(getStringFromResources(R.string.dialog_passwordHint_enter)+" "+initModel.hint);

        CheckBox showPasswordCheckbox = ((CheckBox)view.findViewById(R.id.showPasswordCheckbox));

        passwordControl.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                okButton.setEnabled(s.length() > 0);          // Disable ok button
            }
        });

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
                passwordControl.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            else
                passwordControl.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            passwordControl.setSelection(passwordControl.getText().toString().length());            // Move cursor to end
        });
    }

    @Override
    protected String getOutputModel()
    {
        return passwordControl.getText().toString();
    }

    @Override
    protected void afterButtonsInited()
    {
        okButton.setEnabled(false);
    }
}
