package com.syleiman.comicsviewer.Options;

import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of innerOptionsCollection in memory and Db
 */
public class DbOptionsCollections extends OptionsCollections
{
    public DbOptionsCollections()
    {
        load();
    }

    @Override
    public void addOrUpdate(Option[] options)
    {
        ArrayList<Option> optionsToAdd=new ArrayList<>(options.length);
        ArrayList<Option> optionsToUpdate=new ArrayList<>(options.length);

        for(Option option : options)
        {
            if(get(option.key)==null)
                optionsToAdd.add(option);
            else
                optionsToUpdate.add(option);
        }

        if(DalFacade.Options.update(optionsToAdd, optionsToUpdate))
        {
            for(Option option : optionsToAdd)
                innerOptionsCollection.put(option.key, option.value);

            for(Option option : optionsToUpdate)
            {
                innerOptionsCollection.remove(option.key);
                innerOptionsCollection.put(option.key, option.value);
            }
        }
    }

    @Override
    public void delete(int[] keys)
    {
        if(DalFacade.Options.delete(keys))
            super.delete(keys);
    }

    @Override
    protected void load()
    {
        List<Option> dbOptions= DalFacade.Options.getAll();

        if(dbOptions!=null)
            for (Option option : dbOptions)
                innerOptionsCollection.put(option.key, option.value);
    }
}
