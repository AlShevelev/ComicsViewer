package com.syleiman.comicsviewer.Activities.ViewComics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

/**
 * Storage class for page textures, blend colors and possibly some other values
 * in the future.
 * 
 * @author harism
 */
public class CurlPage
{
	private int colorBack;
	private int colorFront;

	private Bitmap textureBack;
	private Bitmap textureFront;

	private boolean texturesChanged;

	/**
	 * Default constructor.
	 */
	public CurlPage()
	{
		reset();
	}

	/**
	 * Getter for color.
	 */
	public int getColor(PageSide side)
	{
		switch (side)
		{
			case Front: return colorFront;
			default: return colorBack;
		}
	}

	/**
	 * Calculates the next highest power of 2(two) for a given integer.
	 */
	private int getNextHighestPO2(int n)
	{
		n -= 1;
		n = n | (n >> 1);
		n = n | (n >> 2);
		n = n | (n >> 4);
		n = n | (n >> 8);
		n = n | (n >> 16);
		n = n | (n >> 32);
		return n + 1;
	}

	/**
	 * Create texture from given bitmap
	 * -----------------------------------------------
	 * Generates nearest power of two sized Bitmap for give Bitmap. Returns this
	 * new Bitmap using default return statement + original texture coordinates
	 * are stored into RectF.
	 */
	private Bitmap getTexture(Bitmap bitmap, RectF textureRect)
	{
		// Bitmap original size.
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		// Bitmap size expanded to next power of two. This is done due to
		// the requirement on many devices, texture width and height should
		// be power of two.
		int newW = getNextHighestPO2(w);
		int newH = getNextHighestPO2(h);

		// TODO: Is there another way to setDiskItems a bigger Bitmap and copy
		// original Bitmap to it more efficiently? Immutable bitmap anyone?
//		Bitmap bitmapTex = Bitmap.createBitmap(newW, newH, bitmap.getConfig());
        Bitmap bitmapTex = Bitmap.createBitmap(newW, newH, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bitmapTex);
		c.drawBitmap(bitmap, 0, 0, null);

		// Calculate final texture coordinates.
		float texX = (float) w / newW;
		float texY = (float) h / newH;
		textureRect.set(0f, 0f, texX, texY);

		return bitmapTex;
	}

	/**
	 * Getter for textures. Creates Bitmap sized to nearest power of two, copies
	 * original Bitmap into it and returns it. RectF given as parameter is
	 * filled with actual texture coordinates in this new upscaled texture
	 * Bitmap.
	 */
	public Bitmap getTexture(RectF textureRect, PageSide side)
	{
		switch (side)
		{
			case Front: return getTexture(textureFront, textureRect);
			default: return getTexture(textureBack, textureRect);
		}
	}

	/**
	 * Returns true if textures have changed.
	 */
	public boolean getTexturesChanged() {
		return texturesChanged;
	}

	/**
	 * Returns true if back siding texture exists and it differs from front
	 * facing one.
	 */
	public boolean hasBackTexture() {
		return !textureFront.equals(textureBack);
	}

	/**
	 * Recycles and frees underlying Bitmaps.
	 */
	public void recycle()
	{
		if (textureFront != null)
			textureFront.recycle();			// Free memory
		textureFront = createSolidTexture(colorFront);	// Create bitmap as small as possible filled with solid color

		if (textureBack != null)
			textureBack.recycle();
		textureBack = createSolidTexture(colorBack);

		texturesChanged = false;
	}

	/**
	 * Create small texture filled by solid color
	 * @param color
	 * @return
	 */
	private Bitmap createSolidTexture(int color)
	{
		Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
		bmp.eraseColor(color);
		return bmp;
	}

	/**
	 * Resets this CurlPage into its initial state.
	 */
	public void reset()
	{
		colorBack = Color.WHITE;
		colorFront = Color.WHITE;
		recycle();
	}

	/**
	 * Setter blend color.
	 */
	public void setColor(int color, PageSide side)
	{
		switch (side)
		{
			case Front: colorFront = color; break;
			case Back: colorBack = color; break;
			default: colorFront = colorBack = color; break;
		}
	}

	/**
	 * Setter for textures.
	 */
	public void setTexture(Bitmap texture, PageSide side)
	{
		if (texture == null)
			texture = createSolidTexture(side == PageSide.Back ? colorBack : colorFront);

		switch (side)
		{
			case Front:
			{
				if (textureFront != null)
					textureFront.recycle();
				textureFront = texture;
				break;
			}
			case Back:
			{
				if (textureBack != null)
					textureBack.recycle();
				textureBack = texture;
				break;
			}
			case Both:
			{
				setTexture(texture, PageSide.Front);
				setTexture(texture, PageSide.Back);
				break;
			}
		}
		texturesChanged = true;
	}
}
