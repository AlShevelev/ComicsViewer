package com.syleiman.comicsviewer.Activities.ViewComics;

public class ResizingState
{
    private Margins margins;        // Margins of OGL-view - for smooth image

    private float currentScaleFactor;     // Zoom of OGL-camera - for fast resizing (1: in-screen image; 2: zoom value1 2)

    private boolean resized;

    public static final float MAX_MARGIN = 0f;
    public static final float MIN_MARGIN = -0.5f;

    public static final float MIN_SCALE = 1f;
    public static final float MAX_SCALE = 2f;

    public ResizingState(Margins margins, float scaleFactor)
    {
        setMargins(margins);
        setScaleFactor(scaleFactor);
    }

    public float getScaleFactor()
    {
        return currentScaleFactor;
    }

    public void setScaleFactor(float scaleFactor)
    {
        currentScaleFactor = rangeValue(scaleFactor, MIN_SCALE, MAX_SCALE);

        if(currentScaleFactor <= MIN_SCALE + (MAX_SCALE-MIN_SCALE)*0.05f)
            currentScaleFactor = MIN_SCALE;

        resized = this.currentScaleFactor!=MIN_SCALE;
    }

    public Margins getMargins()
    {
        return this.margins;
    }

    public void setMargins(Margins margins)
    {
        this.margins = new Margins(rangeValue(margins.getLeft(), MIN_MARGIN, MAX_MARGIN),
                                   rangeValue(margins.getTop(), MIN_MARGIN, MAX_MARGIN),
                                   rangeValue(margins.getRight(), MIN_MARGIN, MAX_MARGIN),
                                   rangeValue(margins.getBottom(), MIN_MARGIN, MAX_MARGIN));
    }

    public void recalculateMarginsByScaleFactor()
    {
        float oneMargin= MIN_MARGIN * ((currentScaleFactor-MIN_SCALE) / (MAX_SCALE-MIN_SCALE));
        this.margins = new Margins(oneMargin, oneMargin, oneMargin, oneMargin);
    }

    public boolean isResized()
    {
        return resized;
    }

    private float rangeValue(float value, float minValue, float maxValue)
    {
        if(value < minValue)
            return minValue;

        if(value > maxValue)
            return maxValue;

        return value;
    }

    public void updateScaleFactor(float value)
    {
        setScaleFactor(this.currentScaleFactor+value);
    }
}
