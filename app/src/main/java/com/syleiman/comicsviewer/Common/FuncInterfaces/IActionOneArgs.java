package com.syleiman.comicsviewer.Common.FuncInterfaces;

/**
 * Analog of Action from .NET
 */
public interface IActionOneArgs<TSource>
{
    void process(TSource t);
}
