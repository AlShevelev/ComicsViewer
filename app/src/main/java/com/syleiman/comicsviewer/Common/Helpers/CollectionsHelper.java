package com.syleiman.comicsviewer.Common.Helpers;

import com.android.internal.util.Predicate;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IFuncOneArg;
import com.syleiman.comicsviewer.Common.Threads.ICancelationTokenRead;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionsHelper
{
    /**
     * Возвращает true, если хотя бы для одного элемента коллекции выполняется условие
     * @param source
     * @param condition
     * @param <T>
     * @return
     */
    public static <T> boolean any(List<T> source, Predicate<T> condition)
    {
        if(source==null)
            return false;

        for (T t : source)
            if(condition.apply(t))
                return true;
        return false;
    }

    /**
     * Return filtered collection
     * @param source
     * @param condition
     * @param <T>
     * @return
     */
    public static <T> List<T> where(List<T> source, Predicate<T> condition)
    {
        if(source==null)
            return null;

        if(source.size()==0)
            return new ArrayList<>(0);

        ArrayList<T> result=new ArrayList<>(source.size());

        for(T item : source)
        {
           if(condition.apply(item))
               result.add(item);
        }

        return result;
    }

    /**
     * Преобразует один список в другой
     * @param source - начальный список
     * @param func - функциональный интерфейс
     * @param <TSource> - исходный тип
     * @param <TTarget> - целевой тип
     * @return - результат
     */
    public static <TSource, TTarget> List<TTarget> transform(List<TSource> source, IFuncOneArg<TSource, TTarget> func)
    {
        return transform(source, func, null);
    }

    /**
     * Transfrom one list to another (wiht cancelaction)
     * @param source - начальный список
     * @param func - функциональный интерфейс
     * @param cancelationToken - cancelation token to abort operation in other thread
     * @param <TSource> - исходный тип
     * @param <TTarget> - целевой тип
     * @return - результат
     */
    public static <TSource, TTarget> List<TTarget> transform(List<TSource> source, IFuncOneArg<TSource, TTarget> func, ICancelationTokenRead cancelationToken)
    {
        if(cancelationToken!=null && cancelationToken.isCanceled())
            return null;

        if(source==null)
            return null;

        ArrayList<TTarget> result=new ArrayList<TTarget>(source.size());

        for (TSource s : source)
        {
            if(cancelationToken!=null && cancelationToken.isCanceled())
                break;

            result.add(func.process(s));
        }

        return result;
    }

    /**
     * Process some action for every item of collection
     * @param source - processed collection
     * @param <TSource> - type of collection's items
     * @param action - processing action
     */
    public static <TSource> void forEach(List<TSource> source, IActionOneArgs<TSource> action)
    {
        for (TSource s : source)
            action.process(s);
    }

    /**
     * Returns first items for which condition is true, or null if such item is not exists
     * @param source
     * @param condition
     * @param <T>
     * @return
     */
    public static <T> T first(List<T> source, Predicate<T> condition)
    {
        if(source==null)
            return null;

        for (T t : source)
            if(condition.apply(t))
                return t;
        return null;
    }

    /**
     * Returns index of first items for which condition is true, or null if such item is not exists
     * @param source
     * @param condition
     * @param <T>
     * @return
     */
    public static <T> Integer firstIndexOf(List<T> source, Predicate<T> condition)
    {
        if(source==null)
            return null;

        int index=0;
        for (T t : source)
        {
            if (condition.apply(t))
                return index;
            index++;
        }
        return null;
    }

    /**
     * Check - can we work with collection?
     */
    public static <T> boolean isNullOrEmpty(List<T> source)
    {
        return source==null || source.size()==0;
    }

    /**
     * Sort collection
     * @param source source collection
     * @param comparator comparator to sort
     * @param reverse should pass True if we need reverse sort
     * @param <T>
     * @return
     */
    public static <T> List<T> sort(List<T> source, Comparator<T> comparator, boolean reverse)
    {
        List<T> target=new ArrayList<>(source);

        Collections.sort(target, comparator);

        if(reverse)
            Collections.reverse(target);

        return target;
    }

    /**
     * Take *quantityToTake* items from collection and skip *quantityToSkip*
     * @param source
     * @param quantityToTake
     * @param quantityToSkip
     * @param <T>
     * @return
     */
    public static <T> List<T> take(List<T> source, int quantityToTake, int quantityToSkip)
    {
        if(source==null)
            return null;

        if(quantityToSkip >= source.size())
            return new ArrayList<>(0);

        ArrayList<T> result=new ArrayList<>(quantityToTake);
        int count=0;
        for(int i=quantityToSkip; i< source.size() && count < quantityToTake; i++ )
        {
            result.add(source.get(i));
            count++;
        }

        return result;
    }
}