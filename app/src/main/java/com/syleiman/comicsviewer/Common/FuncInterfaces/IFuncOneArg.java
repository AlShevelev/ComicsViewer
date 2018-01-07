package com.syleiman.comicsviewer.Common.FuncInterfaces;

/**
 * Analog of Func from .NET
 */
public interface IFuncOneArg<TSource, TTarget>
{
    TTarget process(TSource t);
}
