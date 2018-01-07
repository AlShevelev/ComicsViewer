package com.syleiman.comicsviewer.Common.Dialogs;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;

/**
 * Dialog "Go to page"
 */
public class GotoPageDialog extends CommonDialogBase<GotoPageDialog.InputModel, GotoPageDialog.OutputModel>
{
    private EditText inputField;
    private TextView hint;

    /** Input model for dialog */
    public static final class InputModel
    {
        public int currentPageNumber;

        public int maxPageNumber;

        public InputModel(int currentPageNumber, int maxPageNumber)
        {
            this.currentPageNumber = currentPageNumber;
            this.maxPageNumber = maxPageNumber;
        }
    }

    /** Output model for dialog */
    public static final class OutputModel
    {
        public int pageNumber;

        public OutputModel(int pageNumber)
        {
            this.pageNumber = pageNumber;
        }
    }

    public GotoPageDialog(Activity parentActivity, IActionOneArgs<OutputModel> okAction, InputModel initModel)
    {
        super(parentActivity, okAction, null, R.string.dialog_goto_page_title, R.layout.dialog_goto_page, initModel);
    }

    @Override
    protected void initControls(View view, InputModel initModel)
    {
        inputField=((EditText)view.findViewById(R.id.pageNumber));
        hint=((TextView)view.findViewById(R.id.hint));

        String pageNumberText=Integer.toString(initModel.currentPageNumber + 1);
        inputField.setFilters(new InputFilter[]{new EditTextInputFilterMinMax(1, initModel.maxPageNumber)});
        inputField.setText(pageNumberText);
        inputField.setSelection(pageNumberText.length());           // Move cursor to end

        inputField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                okButton.setEnabled(s.length() > 0);
            }
        });


        hint.setText(String.format(getStringFromResources(R.string.goto_page_dialog_hint), 1, initModel.maxPageNumber));
    }

    @Override
    protected OutputModel getOutputModel()
    {
        String value=inputField.getText().toString();
        return new OutputModel(Integer.parseInt(value)-1);
    }
}
