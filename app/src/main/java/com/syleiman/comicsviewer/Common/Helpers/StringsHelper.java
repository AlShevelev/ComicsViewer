package com.syleiman.comicsviewer.Common.Helpers;

import android.graphics.Paint;
import android.graphics.Rect;

import com.syleiman.comicsviewer.Common.Structs.Size;

public class StringsHelper {

    /**
     * Cut off file's extention
     */
    public static String cutOffFileExt(String fileName){
        int lastDotIndex=fileName.lastIndexOf(".");

        if(lastDotIndex!=-1)          // We'v got last "." in file's name
            return fileName.substring(0, lastDotIndex);

        return fileName;
    }

    /**
     * Cut string to some length (by insering "..." in a middle of the source string)
     */
    public static String cutToLength(String source, int totalLen){
        final String strToInsert="...";
        final int strToInsertLen=strToInsert.length();
        final int sourceLen=source.length();

        if(source.length()<=totalLen)
            return source;

        if(totalLen <= strToInsertLen)
            return source.substring(0, strToInsertLen);

        int len=totalLen-strToInsertLen;

        int headLen=len/2;
        int tailLen=headLen;

        if(len % 2 != 0)
            headLen++;

        String result=
                source.substring(0, headLen) +
                strToInsert +
                source.substring(sourceLen-tailLen, sourceLen);

        return result;
    }

    /**
     * Cut text so its size can't be greate than some width
     * @param text  - text to cut
     * @param maxWidth
     * @param textPaint
     * @return
     */
    public static String cutToSize(String text, int maxWidth, Paint textPaint)
    {
        text= StringsHelper.cutOffFileExt(text);            // remove extention

        if(getTextSize(text, textPaint).getWidth()<maxWidth)        // No need cut to size
            return text;

        final int minLen=5;
        final int maxLen=100;

        String oldName=StringsHelper.cutToLength(text, minLen);
        for(int i= minLen+1; i < maxLen; i++) {
            String newName = StringsHelper.cutToLength(text, i);
            if(getTextSize(newName, textPaint).getWidth()>maxWidth)
                break;
            oldName=newName;
        }
        return oldName;
    }

    /**
     * Calculate size of text
     * @param text - text to calculate
     * @param textPaint - text's painter
     * @return Calculated size
     */
    public static Size getTextSize(String text, Paint textPaint)
    {
        Rect bounds = new Rect();

        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return new Size(bounds.width(), bounds.height());
    }
}
