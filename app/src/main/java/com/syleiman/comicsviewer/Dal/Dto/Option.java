package com.syleiman.comicsviewer.Dal.Dto;

import com.syleiman.comicsviewer.Dal.Entities.DbOption;

/**
 * One innerOptionsCollection record
 */
public class Option
{
    public int key;

    public String value;

    public Option(int key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public Option(DbOption option)
    {
        this.key = option.key;
        this.value = option.value;
    }
}
