package com.syleiman.comicsviewer.Common.Helpers;

import com.syleiman.comicsviewer.Common.Structs.Pair;

import java.util.ArrayList;
import java.util.List;

public class MathHelper
{
    /**
     * Split interval on @parts equals parts
     * @param from - included
     * @param to - included
     * @param parts - quantity of parts
     */
    public static List<Pair<Integer>> splitInterval(int from, int to, int parts)
    {
        if(from>to)
            throw new IllegalArgumentException("Argument:from must be less than Argument:to");
        if(parts<1)
            throw new IllegalArgumentException("Argument:parts can't be less than 1");

        ArrayList<Pair<Integer>> result=new ArrayList<>(parts);
        if(parts==1 || to-from <= 1)
        {
            result.add(new Pair<>(from, to));
        }
        else
        {
            int sizeOfPart = (to - from) / parts;
            int start=from;
            for (int i = 0; i < parts; i++)
            {
                int end = start+sizeOfPart;         // There will be some inaccuracy for the last part (but it's not important now)
                result.add(new Pair<>(start, end));

                start=end;
            }
        }
        return result;
    }
}
