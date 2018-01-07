package com.syleiman.comicsviewer.Common.Dialogs;

import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;

/**
 * Dialog for setDiskItems/edit comics password
 */
public class CreatePasswordDialog extends CommonDialogBase<CreatePasswordDialog.Model, CreatePasswordDialog.Model>
{
    private EditText passwordControl;
    private EditText hintControl;

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

    public CreatePasswordDialog(Activity parentActivity, IActionOneArgs<Model> okAction, IActionZeroArgs cancelAction)
    {
        super(parentActivity, okAction, cancelAction, R.string.dialog_create_password_title, R.layout.dialog_create_password, new CreatePasswordDialog.Model("", ""));
    }

    public CreatePasswordDialog(
            Activity parentActivity,
            IActionOneArgs<Model> okAction,
            IActionZeroArgs cancelAction,
            int titleId,
            int layoutId,
            String password)
    {
        super(parentActivity, okAction, cancelAction, titleId, layoutId, new CreatePasswordDialog.Model(password, ""));
    }


    @Override
    protected void initControls(View view, Model initModel)
    {
        passwordControl =((EditText)view.findViewById(R.id.password));
        passwordControl.setText(initModel.password);
        passwordControl.setSelection(initModel.password.length());           // Move cursor to end

        hintControl=((EditText)view.findViewById(R.id.passwordHint));
        hintControl.setText(initModel.hint);
        hintControl.setSelection(initModel.hint.length());

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
    protected Model getOutputModel()
    {
        return new Model(passwordControl.getText().toString(), hintControl.getText().toString());
    }

    @Override
    protected void afterButtonsInited()
    {
        okButton.setEnabled(passwordControl.getText().length()>0);          // Disable ok button
    }
}