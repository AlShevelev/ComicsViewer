package com.syleiman.comicsviewer.Dal.Entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.syleiman.comicsviewer.Dal.Dto.Option;

/**
 * One innerOptionsCollection record
 */
@Table(name = "Options")
public class DbOption extends Model
{
    @Column(name = "Key")
    public int key;

    @Column(name = "Value")
    public String value;

    public DbOption()
    {
    }

    public DbOption(Option option)
    {
        this.key = option.key;
        this.value = option.value;
    }
}
