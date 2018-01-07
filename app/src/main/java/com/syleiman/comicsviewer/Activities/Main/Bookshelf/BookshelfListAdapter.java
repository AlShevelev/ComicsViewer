package com.syleiman.comicsviewer.Activities.Main.Bookshelf;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.Main.Bookshelf.ComicsControl.ComicsControl;
import com.syleiman.comicsviewer.Activities.Main.IChangeModeHandlerView;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.Structs.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptor for displaing one bookshelf
 */
public class BookshelfListAdapter extends BaseAdapter
{
    private final Activity activity;
    private static LayoutInflater inflater=null;

    private int booksCount;            // Books total
    private final List<BookshelfComicsInfo> books;

    private final IChangeModeHandlerView changeModeHandlerView;

    private static final int BOOKS_ON_SHELF_VERT =2;
    private static final int SHELF_COUNT_MIN_VERT =3;         // Minimum shelfs on screen

    private static final int BOOKS_ON_SHELF_HORIZ =3;
    private static final int SHELF_COUNT_MIN_HORIZ =2;

    private int booksOnShelf;           // Max books on shelf
    private int shelfCountMin;

    private Size parentSize;            // Size of content view

    private int maxComicsWidth;

    private IActionOneArgs<Long> onComicsChoosen;           // On comics click handler - for view comics

    public BookshelfListAdapter(Activity activity, List<BookshelfComicsInfo> books, IChangeModeHandlerView changeModeHandlerView, IActionOneArgs<Long> onComicsChoosen)
    {
        this.activity = activity;

        this.books = books;
        this.onComicsChoosen = onComicsChoosen;

        this.changeModeHandlerView = changeModeHandlerView;

        booksCount = books.size();
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setParentSize(Size parentSize)
    {
        this.parentSize = parentSize;

        if(parentSize.getWidth() < parentSize.getHeight())
        {
            booksOnShelf=BOOKS_ON_SHELF_VERT;
            shelfCountMin = SHELF_COUNT_MIN_VERT;
        }
        else
        {
            booksOnShelf=BOOKS_ON_SHELF_HORIZ;
            shelfCountMin = SHELF_COUNT_MIN_HORIZ;
        }

        maxComicsWidth = parentSize.getWidth()/booksOnShelf;
    }

    /**
     * Get total items count
     */
    @Override
    public int getCount()
    {
        int result=0;

        if(booksCount <= booksOnShelf * shelfCountMin)
            result = shelfCountMin;                 // Alwais show at least shelfCountMin shelfs + header of bookcase
        else
        {
            result=booksCount / booksOnShelf;

            if(booksCount%booksOnShelf!=0)          // Not full shelf
                result++;
        }

        return result+1;               // + header of bookcase
    }

    /**
     * To prevent white splash when click on shelf
     * @param position
     * @return
     */
    @Override
    public boolean isEnabled(int position)
    {
        return false;
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    /**
     * How many layout types we have got
     */
    @Override
    public int getViewTypeCount()
    {
        return 2;           // Two kind of layout
    }

    /**
     * Returns dependence type of layout from position
     */
    @Override
    public int getItemViewType(int position)
    {
        return  position==0 ? 0 : 1;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View result=null;

        if(position==0)
            result = createHeaderView(convertView);
        else
            result = createBookshelfView(position, convertView);

        return result;
    }

    private View createHeaderView(View convertView)
    {
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.bookshelf_header, null);

            TextView allComicsControl=(TextView)convertView.findViewById(R.id.allComicsText);
            TextView recentComicsControl=(TextView)convertView.findViewById(R.id.recentText);

            allComicsControl.setOnClickListener(v -> { changeModeHandlerView.onAllComicsClicked(allComicsControl, recentComicsControl); });
            recentComicsControl.setOnClickListener(v -> { changeModeHandlerView.onRecentComicsClicked(allComicsControl, recentComicsControl); });

            changeModeHandlerView.initState(allComicsControl, recentComicsControl);
        }
        return convertView;
    }

    private View createBookshelfView(int position, View convertView)
    {
        ViewHolders.ShelfHolder shelfHolder=null;
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.bookshelf_list_item, null);

            AbsListView.LayoutParams p = new AbsListView.LayoutParams(parentSize.getWidth(), parentSize.getHeight() / shelfCountMin);
            convertView.setLayoutParams(p);

            shelfHolder=new ViewHolders.ShelfHolder();

            shelfHolder.loverShelf = (ImageView) convertView.findViewById(R.id.ivShelf);
            shelfHolder.comicsContainer = (LinearLayout) convertView.findViewById(R.id.comicsContainer);

            shelfHolder.loverShelf.setMaxWidth(parentSize.getWidth());
            shelfHolder.loverShelf.setMinimumWidth(parentSize.getWidth());

            convertView.setTag(shelfHolder);
        }
        else
            shelfHolder=(ViewHolders.ShelfHolder)convertView.getTag();

        shelfHolder.comicsContainer.removeAllViews();

        List<BookshelfComicsInfo> comicsOnShelf = getComicsForShelf(position - 1);            // -1 because row with index 0 is a header
        for (BookshelfComicsInfo comicsInfo : comicsOnShelf)             // Create comics for shelves
            shelfHolder.comicsContainer.addView(new ComicsControl(activity, comicsInfo, maxComicsWidth, onComicsChoosen));

        return convertView;
    }

    private List<BookshelfComicsInfo> getComicsForShelf(int shelfIndex)
    {
        ArrayList<BookshelfComicsInfo> result=new ArrayList<BookshelfComicsInfo>();

        if(books.size() > 0)
        {
            int firstBooksIndex=shelfIndex*booksOnShelf;

            int count=0;
            while(firstBooksIndex<books.size() && count<booksOnShelf)
            {
                result.add(books.get(firstBooksIndex));
                firstBooksIndex++;
                count++;
            }
        }

        return result;
    }

    /**
     * Get index of shelf for index of comics
     * @param indexOfComics
     * @return
     */
    public int getShelfForComicsIndex(int indexOfComics)
    {
        if(indexOfComics<booksOnShelf)
            return 1;               // +1 - has header

        indexOfComics++;

        int base = indexOfComics/booksOnShelf;
        if(indexOfComics % booksOnShelf != 0)
            return base+1;
        return base;
    }

    /**
     * Get quantity of shelfs on screen
     */
    public int getShelfsOnScreenCount()
    {
        return shelfCountMin;
    }
}