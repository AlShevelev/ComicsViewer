package com.syleiman.comicsviewer.Common.Dialogs;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Min/max filter for EditText element (with inputType=number)
 */
public class EditTextInputFilterMinMax implements InputFilter
{
    private final int min, max;

    public EditTextInputFilterMinMax(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {
        try
        {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        }
        catch (NumberFormatException nfe)
        {

        }
        return "";
    }

    private boolean isInRange(int a, int b, int c)
    {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
