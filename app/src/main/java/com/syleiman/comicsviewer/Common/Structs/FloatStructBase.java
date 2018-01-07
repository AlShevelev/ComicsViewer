package com.syleiman.comicsviewer.Common.Structs;

public abstract class FloatStructBase
{
    protected int floatToInt(float value, int maxIntValue)
    {
        int tmpValue;

        if(value==0.0)
            tmpValue=0;
        else if(value==1.0)
            tmpValue=maxIntValue;
        else
            tmpValue=(int)(maxIntValue*value);

        if(tmpValue<0.0)
            tmpValue=0;
        else if(tmpValue>maxIntValue)
            tmpValue=maxIntValue;

        return tmpValue;
    }
}
