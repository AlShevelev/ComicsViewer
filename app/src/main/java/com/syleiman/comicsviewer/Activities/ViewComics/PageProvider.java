package com.syleiman.comicsviewer.Activities.ViewComics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.syleiman.comicsviewer.Activities.ViewComics.BitmapRepository.BitmapRepository;
import com.syleiman.comicsviewer.Activities.ViewComics.BitmapRepository.IBitmapRepository;
import com.syleiman.comicsviewer.Common.Helpers.CollectionsHelper;
import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Page;

import java.io.IOException;
import java.util.List;

/**
 * Provide textures for pages and update pages
 */
class PageProvider implements IPageProvider
{
    private IBitmapRepository repository;

    PageProvider(long comicsId)
    {
        List<Page> pages = DalFacade.Comics.getPages(comicsId);
        pages = CollectionsHelper.sort(pages, (lhs, rhs) -> { return Long.compare(lhs.order, rhs.order); }, false);

        repository=new BitmapRepository(pages);
    }

    @Override
    public int getPageCount()
    {
        return repository.getPageCount();
    }

    /**
     * Create bitmap for drawing
     * @param width
     * @param height
     * @param index
     * @return
     */
    private Bitmap loadBitmap(int width, int height, int index) throws IOException
    {
        Bitmap bitmap = repository.getByIndex(index, width, height);

        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);     // For memory saving

        b.eraseColor(0xFFFFFFFF);
        Canvas c = new Canvas(b);

        int margin = 7;

        int border = 3;             // Thin frame around image

        Rect r = new Rect(margin, margin, width - margin, height - margin);     // Image's frame

        int imageWidth = r.width() - (border * 2);          // Scale image with saving proportions
        int imageHeight = imageWidth * bitmap.getHeight() / bitmap.getWidth();

        if (imageHeight > r.height() - (border * 2))
        {                                                       // Inscribe image in draw
            imageHeight = r.height() - (border * 2);
            imageWidth = imageHeight * bitmap.getWidth() / bitmap.getHeight();
        }

        r.left += ((r.width() - imageWidth) / 2) - border;
        r.right = r.left + imageWidth + border + border;            // Place image's rect on center
        r.top += ((r.height() - imageHeight) / 2) - border;
        r.bottom = r.top + imageHeight + border + border;


        Paint p = new Paint();
        p.setColor(0xFFC0C0C0);                 // Draw violet frame around image
        c.drawRect(r, p);


        r.left += border;
        r.right -= border;
        r.top += border;
        r.bottom -= border;

//        d.setBounds(r);
//        d.draw(c);

        c.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), r, p);

        return b;
    }

    /**
     * Set bitmap for page - front and back (may be texture or solid color)
     * @param page
     * @param width
     * @param height
     * @param index
     */
    @Override
    public void updatePage(CurlPage page, int width, int height, int index)
    {
        Bitmap bmp = null;
        try
        {
            bmp = loadBitmap(width, height, index);
        }
        catch (Exception e)
        {
            Log.e("CV", "exception", e);
        }

        page.setTexture(bmp, PageSide.Front);
        page.setTexture(bmp, PageSide.Back);

        page.setColor(Color.argb(50, 255, 255, 255), PageSide.Back);
    }
}