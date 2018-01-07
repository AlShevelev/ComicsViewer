package com.syleiman.comicsviewer.Activities.ViewComics;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.syleiman.comicsviewer.Common.Structs.Pair;
import com.syleiman.comicsviewer.Common.Structs.SizeF;


/**
 * Actual renderer class.
 * 
 * @author harism
 */
public class CurlRenderer implements GLSurfaceView.Renderer
{
	// Background fill color.
	private int backgroundColor;
	// Curl meshes used for static and dynamic rendering.
	private Vector<CurlMesh> curlMeshes;
	private RectF margins = new RectF();
	private CurlRenderer.Observer observer;
	// Page rectangles.
	private RectF pageRectLeft;
	private RectF pageRectRight;

	// Screen size.
	private int viewportWidth, viewportHeight;

	// Rect for render area.
	private RectF viewRect = new RectF();

    private SizeF viewAreaSize;             // Size of view area [px]

    private float scale;           // Scale factor by X and Y
    private Pair<Float> dragging;        // Dragging factor by X and Y

	/**
	 * Basic constructor.
	 */
	public CurlRenderer(CurlRenderer.Observer observer)
	{
		this.observer = observer;
		curlMeshes = new Vector<CurlMesh>();
		pageRectLeft = new RectF();
		pageRectRight = new RectF();

        scale = 1f;
        dragging = new Pair<Float>(0f, 0f);
	}

	/**
	 * Adds CurlMesh to this renderer.
	 */
	public synchronized void addCurlMesh(CurlMesh mesh)
	{
		removeCurlMesh(mesh);
		curlMeshes.add(mesh);
	}

	/**
	 * Returns rect reserved for left or right page. Value page should be
	 * PAGE_LEFT or PAGE_RIGHT.
	 */
	public RectF getPageRect(CurlState page)
	{
		if (page == CurlState.Left)
		{
			return pageRectLeft;
		}
		else if (page == CurlState.Right)
		{
			return pageRectRight;
		}
		return null;
	}

	@Override
	public synchronized void onDrawFrame(GL10 gl)
    {
		observer.onDrawFrame();

		gl.glClearColor(Color.red(backgroundColor) / 255f,
				Color.green(backgroundColor) / 255f,
				Color.blue(backgroundColor) / 255f,
				Color.alpha(backgroundColor) / 255f);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

        gl.glScalef(scale, scale, 0);
        gl.glTranslatef(dragging.value1, dragging.value2, 0);

		for (int i = 0; i < curlMeshes.size(); ++i) {
			curlMeshes.get(i).onDrawFrame(gl);
		}
	}

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public void setDragging(Pair<Float> dragging)
    {
        this.dragging = dragging;
    }

	public RendererViewInfo getViewInfo()
	{
		return new RendererViewInfo(viewRect, viewAreaSize);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        gl.glViewport(0, 0, width, height);

		viewportWidth = width;
		viewportHeight = height;

        viewAreaSize = new SizeF(width, height);

		float ratio = (float) width / height;
		viewRect.top = 1.0f;
		viewRect.bottom = -1.0f;
		viewRect.left = -ratio;
		viewRect.right = ratio;

		updatePageRects();
//		requestRender();

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

    	GLU.gluOrtho2D(gl, viewRect.left, viewRect.right,	viewRect.bottom, viewRect.top);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_LINE_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_CULL_FACE);

		observer.onSurfaceCreated();
	}

	/**
	 * Removes CurlMesh from this renderer.
	 */
	public synchronized void removeCurlMesh(CurlMesh mesh) {
		while (curlMeshes.remove(mesh));
	}

	/**
	 * Change background/clear color.
	 */
	public void setBackgroundColor(int color) {
		backgroundColor = color;
	}

	/**
	 * Set margins or padding. Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	public synchronized void setMargins(Margins margins)
    {
		this.margins.left = margins.getLeft();
		this.margins.top = margins.getTop();
		this.margins.right = margins.getRight();
		this.margins.bottom = margins.getBottom();
		updatePageRects();
	}

	/**
	 * Sets visible page count to one or two. Should be either SHOW_ONE_PAGE or
	 * SHOW_TWO_PAGES.
	 */
	public synchronized void setViewMode()
	{
        updatePageRects();
	}

	/**
	 * Translates screen coordinates into view coordinates.
	 */
	public void translate(PointF pt)
    {
		pt.x = viewRect.left + (viewRect.width() * pt.x / viewportWidth);
		pt.y = viewRect.top - (-viewRect.height() * pt.y / viewportHeight);
	}

	/**
	 * Recalculates page rectangles.
	 */
	private void updatePageRects()
    {
		if (viewRect.width() == 0 || viewRect.height() == 0)
			return;
		else
		{
			pageRectRight.set(viewRect);              // Resize and move viewRect for scale and slide image
			pageRectRight.left += viewRect.width() * margins.left;
			pageRectRight.right -= viewRect.width() * margins.right;
			pageRectRight.top += viewRect.height() * margins.top;
			pageRectRight.bottom -= viewRect.height() * margins.bottom;

			pageRectLeft.set(pageRectRight);
			pageRectLeft.offset(-pageRectRight.width(), 0);

			int bitmapW = (int) ((pageRectRight.width() * viewportWidth) / viewRect.width());
			int bitmapH = (int) ((pageRectRight.height() * viewportHeight) / viewRect.height());
			observer.onPageSizeChanged(bitmapW, bitmapH);
		}
	}

	/**
	 * Observer for waiting render engine/state updates.
	 */
	public interface Observer {
		/**
		 * Called from onDrawFrame called before rendering is started. This is
		 * intended to be used for animation purposes.
		 */
		public void onDrawFrame();

		/**
		 * Called once page size is changed. Width and height tell the page size
		 * in pixels making it possible to update textures accordingly.
		 */
		public void onPageSizeChanged(int width, int height);

		/**
		 * Called from onSurfaceCreated to enable texture re-initialization etc
		 * what needs to be done when this happens.
		 */
		public void onSurfaceCreated();
	}
}
