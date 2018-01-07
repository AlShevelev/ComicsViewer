package com.syleiman.comicsviewer.Dal;

import com.syleiman.comicsviewer.Dal.Dto.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Dal for innerOptionsCollection
 */
public interface IOptionsDal
{
    /**
     * @return false - fail
     */
    boolean update(List<Option> optionsToAdd, List<Option> optionsToUpdate);

    /**
     * @return false - fail
     */
    boolean delete(int[] keys);

    /**
     * @return null - fail
     */
    List<Option> getAll();
}
