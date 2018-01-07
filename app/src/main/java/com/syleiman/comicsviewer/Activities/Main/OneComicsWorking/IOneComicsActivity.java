package com.syleiman.comicsviewer.Activities.Main.OneComicsWorking;

import com.syleiman.comicsviewer.Activities.Main.ComicsFilters.ComicsViewMode;

/**
 * Operation for working with one comics
 */
public interface IOneComicsActivity
{
    /**
     * Read comics and put them on the shelfs - in background thread
     * @param idOfComicsToScroll - id of comics to scroll to (if null - without scrolling)
     */
    void updateBooksList(Long idOfComicsToScroll);

    /**
     * Turn on/off user actios
     * @param isLocked if true - userActions locked
     */
    void setUserActionsLock(boolean isLocked);

    /**
     * Is user actios turn on/off
     * @return true - actions locked
     */
    boolean isUserActionsLock();

    /**
     * Show/hide progress
     * @param isVisible if true progress'll showed
     */
    void setProgressState(boolean isVisible);

    /**
     * Update text of progress control
     * @param text
     */
    void updateProgressText(String text);

    /**
     * Set view mode for bookcase (all comics or only recent)
     * @param viewMode
     */
    void setViewMode(ComicsViewMode viewMode);
}
