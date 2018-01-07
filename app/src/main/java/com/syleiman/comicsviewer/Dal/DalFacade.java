package com.syleiman.comicsviewer.Dal;

public class DalFacade
{
    public static final IComicsDal Comics = new ComicsDal();

    public static final IOptionsDal Options = new OptionsDal();
}