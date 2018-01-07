package com.syleiman.comicsviewer.Activities.ViewComics;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.syleiman.comicsviewer.Activities.ViewComics.Helpers.PointsHelper;
import com.syleiman.comicsviewer.Activities.ViewComics.UserActionsManaging.IUserActionsManaged;
import com.syleiman.comicsviewer.Activities.ViewComics.UserActionsManaging.UserActionManager;
import com.syleiman.comicsviewer.Activities.ViewComics.UserActionsManaging.ViewStateCodes;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionOneArgs;
import com.syleiman.comicsviewer.Common.FuncInterfaces.IActionZeroArgs;
import com.syleiman.comicsviewer.Common.Helpers.ScreenHelper;

/**
 * OpenGL ES View.
 * 
 * @author harism
 */
public class CurlView extends GLSurfaceView implements View.OnTouchListener, CurlRenderer.Observer, IUserActionsManaged
{
    private Context context;

    private CurlState curlState = CurlState.None;           // Curl state. We are flipping none, left or right page.

    private CurlTarget animationTargetEvent = CurlTarget.None;

	private boolean canCurlLastPage = false;         // Can we curl last page

	private boolean animate = false;
	private long animationDurationTime = 300;
	private PointF animationSource = new PointF();
	private long animationStartTime;
	private PointF animationTarget = new PointF();

	private PointF curlDir = new PointF();

	private PointF curlPos = new PointF();

	private int currentPageIndex;           // Current bitmap index. This is always showed as front of right page.

	// Start position for dragging.
	private PointF dragStartPos = new PointF();

	private boolean enableTouchPressure = false;
	// Bitmap size. These are updated from renderer once it's initialized.
	private int pageBitmapHeight = -1;

	private int pageBitmapWidth = -1;
	// Page meshes. Left and right meshes are 'static' while curl is used to
	// show page flipping.
	private CurlMesh pageCurl;

	private CurlMesh pageLeft;
	private IPageProvider pageProvider;
	private CurlMesh pageRight;

	private PointerPosition pointerPos = new PointerPosition();

	private CurlRenderer renderer;
	private boolean renderLeftPage = true;

    private UserActionManager userActionManager;

    private ResizingState resizingState;
    private Float resizingPointsDistance;       // Distance between points while resizing
    private ViewStateCodes viewStateCodes = ViewStateCodes.NotResized;

    private float screenDiagonal;           // Size of screen diagonal in pixels

    private PointF firstDraggingPoint;           // Size of screen diagonal in pixels
    private DraggingState draggingState;

    private IActionOneArgs<Integer> onPageChanged;
    private IActionZeroArgs onShowMenu;             // When we neet show menu

	/**
	 * Default constructor.
	 */
	public CurlView(Context context)
    {
		super(context);
		init(context);
	}

    /**
     * Default constructor.
     */
	public CurlView(Context context, AttributeSet attrs)
    {
		super(context, attrs);
		init(context);
	}

    /**
     * Default constructor.
     */
	public CurlView(Context context, AttributeSet attrs, int defStyle)
    {
		this(context, attrs);
	}

	/**
	 * Initialize method.
	 */
	private void init(Context ctx)
    {
        context =ctx;

        userActionManager = new UserActionManager(this, ScreenHelper.getScreenSize((Activity)context));

		renderer = new CurlRenderer(this);
		setRenderer(renderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setOnTouchListener(this);

		// Even though left and right pages are static we have to allocate room
		// for curl on them too as we are switching meshes. Another way would be
		// to swap texture ids only.
		pageLeft = new CurlMesh(10);
		pageRight = new CurlMesh(10);
		pageCurl = new CurlMesh(10);
		pageLeft.setFlipTexture(true);
		pageRight.setFlipTexture(false);

        draggingState = new DraggingState(ResizingState.MIN_MARGIN, ResizingState.MAX_MARGIN);
	}

	@Override
	public void onDrawFrame()
    {
		// We are not animating.
		if (animate == false)
			return;

		long currentTime = System.currentTimeMillis();
		// If animation is done.
		if (currentTime >= animationStartTime + animationDurationTime)
        {
			if (animationTargetEvent == CurlTarget.ToRight)
            {
				// Switch curled page to right.
				CurlMesh right = pageCurl;
				CurlMesh curl = pageRight;
				right.setRect(renderer.getPageRect(CurlState.Right));
				right.setFlipTexture(false);
				right.reset();
				renderer.removeCurlMesh(curl);
				pageCurl = curl;
				pageRight = right;
				// If we were curling left page update current index.
				if (curlState == CurlState.Left)
					--currentPageIndex;
			}
            else
                if (animationTargetEvent == CurlTarget.ToLeft)
                {
                    // Switch curled page to left.
                    CurlMesh left = pageCurl;
                    CurlMesh curl = pageLeft;
                    left.setRect(renderer.getPageRect(CurlState.Left));
                    left.setFlipTexture(true);
                    left.reset();
                    renderer.removeCurlMesh(curl);
                    if (!renderLeftPage) {
                        renderer.removeCurlMesh(left);
                    }
                    pageCurl = curl;
                    pageLeft = left;
                    // If we were curling right page update current index.
                    if (curlState == CurlState.Right)
                        ++currentPageIndex;
                }
                curlState = CurlState.None;
                animate = false;
                onPageChanged.process(currentPageIndex);
                requestRender();
		}
        else
        {
			pointerPos.mPos.set(animationSource);
			float t = 1f - ((float) (currentTime - animationStartTime) / animationDurationTime);
			t = 1f - (t * t * t * (3 - 2 * t));
			pointerPos.mPos.x += (animationTarget.x - animationSource.x) * t;
			pointerPos.mPos.y += (animationTarget.y - animationSource.y) * t;
			updateCurlPos(pointerPos);
		}
	}

	@Override
	public void onPageSizeChanged(int width, int height)
    {
		pageBitmapWidth = width;
		pageBitmapHeight = height;
		updatePages();
        //requestRender();
	}

	@Override
	public void onSizeChanged(int w, int h, int ow, int oh)
    {
		super.onSizeChanged(w, h, ow, oh);
        reset();
        screenDiagonal = (float)Math.sqrt(Math.pow(w, 2)+Math.pow(h, 2));
	}

    /**
     * Reset state of current page - cancel resizing and so on
     */
    private void reset()
    {
        renderer.setDragging(draggingState.reset());        // Reset dragging

        requestRender();

        pageLeft.setFlipTexture(true);
        renderer.setViewMode();
        requestRender();

        resizingState=new ResizingState(new Margins(0f, 0f, 0f, 0f), 1f);           // Original size
        renderer.setMargins(resizingState.getMargins());
        renderer.setScale(resizingState.getScaleFactor());

        viewStateCodes = resizingState.isResized() ? ViewStateCodes.Resized : ViewStateCodes.NotResized;
    }

	@Override
	public void onSurfaceCreated()
    {
		// In case surface is recreated, let page meshes drop allocated texture
		// ids and ask for new ones. There's no need to set textures here as
		// onPageSizeChanged should be called later on.
		pageLeft.resetTexture();
		pageRight.resetTexture();
		pageCurl.resetTexture();
	}

    private void memorizePoint(float x, float y, float pressure)
    {
        pointerPos.mPos.set(x, y);
        renderer.translate(pointerPos.mPos);

        if (enableTouchPressure)
            pointerPos.mPressure = pressure;
        else
            pointerPos.mPressure = 0.8f;
    }

    @Override
    public void startCurving(PointF point, float pressure)
    {
        RectF rightRect = renderer.getPageRect(CurlState.Right);

        memorizePoint(point.x, point.y, pressure);


        // Once we receive pointer down event its position is mapped to
        // right or left edge of page and that'll be the position from where
        // user is holding the paper to make curl happen.
        dragStartPos.set(pointerPos.mPos);

        // First we make sure it's not over or below page. Pages are
        // supposed to be same height so it really doesn't matter do we use
        // left or right one.
        if (dragStartPos.y > rightRect.top) {
            dragStartPos.y = rightRect.top;
        } else if (dragStartPos.y < rightRect.bottom) {
            dragStartPos.y = rightRect.bottom;
        }

        // Then we have to make decisions for the user whether curl is going
        // to happen from left or right, and on which page.
        float halfX = (rightRect.right + rightRect.left) / 2;
        if (dragStartPos.x < halfX && currentPageIndex > 0)
        {
            dragStartPos.x = rightRect.left;
            startCurl(CurlState.Left);
        }
        else
        if (dragStartPos.x >= halfX && currentPageIndex < pageProvider.getPageCount())
        {
            dragStartPos.x = rightRect.right;
            if (!canCurlLastPage && currentPageIndex >= pageProvider.getPageCount() - 1)
                return;

            startCurl(CurlState.Right);
        }

        // If we have are in curl state, let this case clause flow through
        // to next one. We have pointer position and drag position defined
        // and this will setDiskItems first render request given these points.
        if (curlState == CurlState.None)
            return;
    }

    @Override
    public void curving(PointF point, float pressure)
    {
        memorizePoint(point.x, point.y, pressure);
        updateCurlPos(pointerPos);
    }

    @Override
    public void completeCurving(PointF point, float pressure)
    {
        RectF rightRect = renderer.getPageRect(CurlState.Right);
        RectF leftRect = renderer.getPageRect(CurlState.Left);

        memorizePoint(point.x, point.y, pressure);

        if (curlState == CurlState.Left || curlState == CurlState.Right)
        {
            // Animation source is the point from where animation starts.
            // Also it's handled in a way we actually simulate touch events
            // meaning the output is exactly the same as if user drags the
            // page to other side. While not producing the best looking
            // result (which is easier done by altering curl position and/or
            // direction directly), this is done in a hope it made code a
            // bit more readable and easier to maintain.
            animationSource.set(pointerPos.mPos);
            animationStartTime = System.currentTimeMillis();

            // Given the explanation, here we decide whether to simulate
            // drag to left or right end.
            if (pointerPos.mPos.x > (rightRect.left + rightRect.right) / 2)
            {
                // On right side target is always right page's right border.
                animationTarget.set(dragStartPos);
                animationTarget.x = renderer.getPageRect(CurlState.Right).right;
                animationTargetEvent = CurlTarget.ToRight;
            }
            else
            {
                // On left side target depends on visible pages.
                animationTarget.set(dragStartPos);
                if (curlState == CurlState.Right)
                    animationTarget.x = leftRect.left;

                else
                    animationTarget.x = rightRect.left;
                animationTargetEvent = CurlTarget.ToLeft;
            }
            animate = true;

            renderer.setDragging(draggingState.reset());
            requestRender();
        }
    }

    @Override
    public void cancelCurving(PointF point, float pressure)
    {
        if(curlState == CurlState.None)
            return;

        memorizePoint(point.x, point.y, pressure);

        animationTarget.set(dragStartPos);

        if (curlState == CurlState.Left)
        {
            animationTarget.x = renderer.getPageRect(CurlState.Left).left;
            animationTargetEvent = CurlTarget.ToLeft;
        }
        else if(curlState == CurlState.Right)
        {
            animationTarget.x = renderer.getPageRect(CurlState.Right).right;
            animationTargetEvent = CurlTarget.ToRight;
        }

        animate = true;
        Log.d("DRAGGING", "cancelCurving - reset");
        renderer.setDragging(draggingState.reset());
        requestRender();
    }

    @Override
    public void startResizing()
    {
        resizingPointsDistance=null;
        viewStateCodes = resizingState.isResized() ? ViewStateCodes.Resized : ViewStateCodes.NotResized;

        renderer.setMargins(new Margins(0f, 0f, 0f, 0f));
        renderer.setScale(resizingState.getScaleFactor());

        if(viewStateCodes==ViewStateCodes.Resized)
            renderer.setDragging(draggingState.reset());            // Place to center
    }

    @Override
    public void resizing(PointF[] points)
    {
        Float newResizingPointsDistance = PointsHelper.getDistance(points);
        if(resizingPointsDistance==null && newResizingPointsDistance > 0f)
            resizingPointsDistance = newResizingPointsDistance;
        else
        {
            float resizingFactor = calculateResizingFactor(resizingPointsDistance, newResizingPointsDistance);
            resizingState.updateScaleFactor(resizingFactor);

            viewStateCodes = resizingState.isResized() ? ViewStateCodes.Resized : ViewStateCodes.NotResized;
            resizingPointsDistance = newResizingPointsDistance;

            renderer.setScale(resizingState.getScaleFactor());

            requestRender();            // Update frame
        }
    }

    @Override
    public void completeResizing()
    {
        resizingPointsDistance=null;
        viewStateCodes = resizingState.isResized() ? ViewStateCodes.Resized : ViewStateCodes.NotResized;

        resizingState.recalculateMarginsByScaleFactor();

        Margins margins = resizingState.getMargins();
        renderer.setMargins(margins);               // too heavy
        draggingState.setCurrentMargins(margins);
        renderer.setScale(1f);
        requestRender();

        if(viewStateCodes == ViewStateCodes.NotResized)
        {
            Log.d("DRAGGING", "completeResizing - reset");
            renderer.setDragging(draggingState.reset());               // Place to center
            requestRender();
        }
    }

    @Override
    public void startDragging(PointF point)
    {
        firstDraggingPoint = point;
        draggingState.setViewInfo(renderer.getViewInfo());
        draggingState.startDragging();
    }

    @Override
    public void dragging(PointF point)
    {
        float deltaX=point.x- firstDraggingPoint.x;
        float deltaY=point.y- firstDraggingPoint.y;

        Log.d("DRAGGING", "dragging");
        renderer.setDragging(draggingState.processDragging(deltaX, deltaY));
        requestRender();            // Update frame
    }

    @Override
    public void completeDragging(PointF point)
    {
        draggingState.completeDragging();
    }

    @Override
    public void showMenu()
    {
        onShowMenu.process();
    }

    @Override
	public boolean onTouch(View view, MotionEvent me)
    {
		if (animate || pageProvider == null)
			return false;

        userActionManager.Process(me, viewStateCodes);

		return true;
	}

    /**
     *  Calculate factor for changing margins while resizing;
     */
    private float calculateResizingFactor(float oldPointsDistance, float newPointsDistance)
    {
        final float resizingMultiplier=6f;

        float delta = newPointsDistance - oldPointsDistance;
        float result= resizingMultiplier * (delta/screenDiagonal);

        return result;
    }

	/**
	 * Allow the last page to curl.
	 */
	public void setAllowLastPageCurl(boolean allowLastPageCurl) {
		canCurlLastPage = allowLastPageCurl;
	}

	/**
	 * Sets background color - or OpenGL clear color to be more precise. Color
	 * is a 32bit value consisting of 0xAARRGGBB and is extracted using
	 * android.graphics.Color eventually.
	 */
	@Override
	public void setBackgroundColor(int color) {
		renderer.setBackgroundColor(color);
		requestRender();
	}

	/**
	 * Sets pageCurl curl position.
	 */
	private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {

		// First reposition curl so that page doesn't 'rip off' from book.
		if (curlState == CurlState.Right || curlState == CurlState.Left)
        {
			RectF pageRect = renderer.getPageRect(CurlState.Right);
			if (curlPos.x >= pageRect.right)
            {
				pageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x < pageRect.left)
            {
				curlPos.x = pageRect.left;
			}
			if (curlDir.y != 0)
            {
				float diffX = curlPos.x - pageRect.left;
				float leftY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && leftY < pageRect.top) {
					curlDir.x = curlPos.y - pageRect.top;
					curlDir.y = pageRect.left - curlPos.x;
				} else if (curlDir.y > 0 && leftY > pageRect.bottom) {
					curlDir.x = pageRect.bottom - curlPos.y;
					curlDir.y = curlPos.x - pageRect.left;
				}
			}
		}
        else if (curlState == CurlState.Left)
        {
			RectF pageRect = renderer.getPageRect(CurlState.Left);
			if (curlPos.x <= pageRect.left)
            {
				pageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x > pageRect.right)
            {
				curlPos.x = pageRect.right;
			}
			if (curlDir.y != 0)
            {
				float diffX = curlPos.x - pageRect.right;
				float rightY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && rightY < pageRect.top)
                {
					curlDir.x = pageRect.top - curlPos.y;
					curlDir.y = curlPos.x - pageRect.right;
				} else if (curlDir.y > 0 && rightY > pageRect.bottom)
                {
					curlDir.x = curlPos.y - pageRect.bottom;
					curlDir.y = pageRect.right - curlPos.x;
				}
			}
		}

		// Finally normalize direction vector and do rendering.
		double dist = Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);
		if (dist != 0) {
			curlDir.x /= dist;
			curlDir.y /= dist;
			pageCurl.curl(curlPos, curlDir, radius);
		} else {
			pageCurl.reset();
		}

		requestRender();
	}

	/**
	 * Set current page index first time
	 */
	public void initCurrentPageIndex(int currentPageIndex)
    {
        this.currentPageIndex = currentPageIndex;

		updatePages();
		requestRender();
	}

    /**
     * Change index of current page and switch to this page
     */
    public void setCurrentPageIndex(int currentPageIndex)
    {
        this.currentPageIndex = currentPageIndex;

        updatePages();
        reset();

        onPageChanged.process(currentPageIndex);
    }

	/**
	 * Update/set page provider.
	 */
	public void setPageProvider(IPageProvider pageProvider)
    {
		this.pageProvider = pageProvider;
	}

    /**
     * Set callback handlers
     */
    public void setCallbackHandlers(IActionOneArgs<Integer> onPageChanged, IActionZeroArgs onShowMenu)
    {
        this.onPageChanged = onPageChanged;
        this.onShowMenu = onShowMenu;
    }

	/**
	 * Switches meshes and loads new bitmaps if available. Updated to support 2
	 * pages in landscape
	 */
	private void startCurl(CurlState curlState)
    {
		switch (curlState)
        {
            // Once right side page is curled, first right page is assigned into
            // curled page. And if there are more bitmaps available new bitmap is
            // loaded into right side mesh.
            case Right:
            {
                // Remove meshes from renderer.
                renderer.removeCurlMesh(pageLeft);
                renderer.removeCurlMesh(pageRight);
                renderer.removeCurlMesh(pageCurl);

                // We are curling right page.
                CurlMesh curl = pageRight;
                pageRight = pageCurl;
                pageCurl = curl;

                if (currentPageIndex > 0)
                {
                    pageLeft.setFlipTexture(true);
                    pageLeft.setRect(renderer.getPageRect(CurlState.Left));
                    pageLeft.reset();
                    if (renderLeftPage)
                        renderer.addCurlMesh(pageLeft);
                }
                if (currentPageIndex < pageProvider.getPageCount() - 1)
                {
                    updatePage(pageRight.getTexturePage(), currentPageIndex + 1);
                    pageRight.setRect(renderer.getPageRect(CurlState.Right));
                    pageRight.setFlipTexture(false);
                    pageRight.reset();
                    renderer.addCurlMesh(pageRight);
                }

                // Add curled page to renderer.
                pageCurl.setRect(renderer.getPageRect(CurlState.Right));
                pageCurl.setFlipTexture(false);
                pageCurl.reset();
                renderer.addCurlMesh(pageCurl);

                this.curlState = CurlState.Right;
                break;
		    }

		// On left side curl, left page is assigned to curled page. And if
		// there are more bitmaps available before currentPageIndex, new bitmap
		// is loaded into left page.
            case Left:
            {
                // Remove meshes from renderer.
                renderer.removeCurlMesh(pageLeft);
                renderer.removeCurlMesh(pageRight);
                renderer.removeCurlMesh(pageCurl);

                // We are curling left page.
                CurlMesh curl = pageLeft;
                pageLeft = pageCurl;
                pageCurl = curl;

                if (currentPageIndex > 1)
                {
                    updatePage(pageLeft.getTexturePage(), currentPageIndex - 2);
                    pageLeft.setFlipTexture(true);
                    pageLeft.setRect(renderer.getPageRect(CurlState.Left));
                    pageLeft.reset();
                    if (renderLeftPage)
                        renderer.addCurlMesh(pageLeft);
                }

                // If there is something to show on right page add it to renderer.
                if (currentPageIndex < pageProvider.getPageCount())
                {
                    pageRight.setFlipTexture(false);
                    pageRight.setRect(renderer.getPageRect(CurlState.Right));
                    pageRight.reset();
                    renderer.addCurlMesh(pageRight);
                }

                // How dragging previous page happens depends on view mode.
                pageCurl.setRect(renderer.getPageRect(CurlState.Right));
                pageCurl.setFlipTexture(false);

                pageCurl.reset();
                renderer.addCurlMesh(pageCurl);

                this.curlState = CurlState.Left;
                break;
		    }
		}
	}

	/**
	 * Updates curl position.
	 */
	private void updateCurlPos(PointerPosition pointerPos) {

		// Default curl radius.
		double radius = renderer.getPageRect(CurlState.Right).width() / 3;
		// TODO: This is not an optimal solution. Based on feedback received so
		// far; pressure is not very accurate, it may be better not to map
		// coefficient to range [0f, 1f] but something like [.2f, 1f] instead.
		// Leaving it as is until get my hands on a real device. On emulator
		// this doesn't work anyway.
		radius *= Math.max(1f - pointerPos.mPressure, 0f);
		// NOTE: Here we set pointerPos to curlPos. It might be a bit confusing
		// later to see e.g "curlPos.value1 - dragStartPos.value1" used. But it's
		// actually pointerPos we are doing calculations against. Why? Simply to
		// optimize code a bit with the cost of making it unreadable. Otherwise
		// we had to this in both of the next if-else branches.
		curlPos.set(pointerPos.mPos);

		// If curl happens on right page, or on left page on two page mode,
		// we'll calculate curl position from pointerPos.
		if (curlState == CurlState.Right)
        {
			curlDir.x = curlPos.x - dragStartPos.x;
			curlDir.y = curlPos.y - dragStartPos.y;
			float dist = (float) Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);

			// Adjust curl radius so that if page is dragged far enough on
			// opposite side, radius gets closer to zero.
			float pageWidth = renderer.getPageRect(CurlState.Right).width();
			double curlLen = radius * Math.PI;
			if (dist > (pageWidth * 2) - curlLen)
            {
				curlLen = Math.max((pageWidth * 2) - dist, 0f);
				radius = curlLen / Math.PI;
			}

			// Actual curl position calculation.
			if (dist >= curlLen)
            {
				double translate = (dist - curlLen) / 2;

                float pageLeftX = renderer.getPageRect(CurlState.Right).left;
                radius = Math.max(Math.min(curlPos.x - pageLeftX, radius),	0f);

				curlPos.y -= curlDir.y * translate / dist;
			}
            else
            {
				double angle = Math.PI * Math.sqrt(dist / curlLen);
				double translate = radius * Math.sin(angle);
				curlPos.x += curlDir.x * translate / dist;
				curlPos.y += curlDir.y * translate / dist;
			}
		}
		// Otherwise we'll let curl follow pointer position.
		else if (curlState == CurlState.Left)
        {
			// Adjust radius regarding how close to page edge we are.
			float pageLeftX = renderer.getPageRect(CurlState.Right).left;
			radius = Math.max(Math.min(curlPos.x - pageLeftX, radius), 0f);

			float pageRightX = renderer.getPageRect(CurlState.Right).right;
			curlPos.x -= Math.min(pageRightX - curlPos.x, radius);
			curlDir.x = curlPos.x + dragStartPos.x;
			curlDir.y = curlPos.y - dragStartPos.y;
		}

		setCurlPos(curlPos, curlDir, radius);
	}

	/**
	 * Updates given CurlPage via PageProvider for page located at index.
	 */
	private void updatePage(CurlPage page, int index) {
		// First reset page to initial state.
		page.reset();
		// Ask page provider to fill it up with bitmaps and colors.
		pageProvider.updatePage(page, pageBitmapWidth, pageBitmapHeight, index);
	}

	/**
	 * Updates bitmaps for page meshes.
	 */
	private void updatePages()
    {
		if (pageProvider == null || pageBitmapWidth <= 0 || pageBitmapHeight <= 0)
			return;

		// Remove meshes from renderer.
		renderer.removeCurlMesh(pageLeft);
		renderer.removeCurlMesh(pageRight);
		renderer.removeCurlMesh(pageCurl);

		int leftIdx = currentPageIndex - 1;
		int rightIdx = currentPageIndex;
		int curlIdx = -1;
		if (curlState == CurlState.Left)
        {
			curlIdx = leftIdx;
			--leftIdx;
		}
        else if (curlState == CurlState.Right)
        {
			curlIdx = rightIdx;
			++rightIdx;
		}

		if (rightIdx >= 0 && rightIdx < pageProvider.getPageCount())
        {
			updatePage(pageRight.getTexturePage(), rightIdx);
			pageRight.setFlipTexture(false);
			pageRight.setRect(renderer.getPageRect(CurlState.Right));
			pageRight.reset();
			renderer.addCurlMesh(pageRight);
		}
		if (leftIdx >= 0 && leftIdx < pageProvider.getPageCount())
        {
			updatePage(pageLeft.getTexturePage(), leftIdx);
			pageLeft.setFlipTexture(true);
			pageLeft.setRect(renderer.getPageRect(CurlState.Left));
			pageLeft.reset();
			if (renderLeftPage)
				renderer.addCurlMesh(pageLeft);
		}
		if (curlIdx >= 0 && curlIdx < pageProvider.getPageCount())
        {
			updatePage(pageCurl.getTexturePage(), curlIdx);

			if (curlState == CurlState.Right)
            {
				pageCurl.setFlipTexture(true);
				pageCurl.setRect(renderer.getPageRect(CurlState.Right));
			}
            else
            {
				pageCurl.setFlipTexture(false);
				pageCurl.setRect(renderer.getPageRect(CurlState.Left));
			}

			pageCurl.reset();
			renderer.addCurlMesh(pageCurl);
		}
	}
}