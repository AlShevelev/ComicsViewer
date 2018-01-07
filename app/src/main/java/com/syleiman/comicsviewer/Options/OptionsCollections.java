package com.syleiman.comicsviewer.Options;

import com.syleiman.comicsviewer.Dal.Dto.Option;

import java.util.TreeMap;

/**
 * Collection of innerOptionsCollection in memory
 */
public class OptionsCollections implements IOptionsCollections
{
    protected TreeMap<Integer, String> innerOptionsCollection = new TreeMap<>();

    public OptionsCollections()
    {
    }

    /**
     * @return null if not found
     */
    @Override
    public String get(int key)
    {
        return innerOptionsCollection.get(key);
    }

    /**
     * @return false if fail
     */
    @Override
    public void addOrUpdate(Option[] options)
    {
        for(Option option : options)
        {
            if(get(option.key)!=null)
                delete(option.key);
            innerOptionsCollection.put(option.key, option.value);
        }
    }

    /**
     * @return false if fail
     */
    private void delete(int key)
    {
        innerOptionsCollection.remove(key);
    }

    /**
     * @return false if fail
     */
    @Override
    public void delete(int[] keys)
    {
        for(int key : keys)
            innerOptionsCollection.remove(key);
    }

    protected void load()
    {
        return;         // Must be overriten in childs
    }
}
