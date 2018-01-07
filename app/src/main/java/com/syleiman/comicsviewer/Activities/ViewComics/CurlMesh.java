package com.syleiman.comicsviewer.Activities.ViewComics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLUtils;

/**
 * Class implementing actual curl/page rendering.
 * 
 * @author harism
 */
public class CurlMesh
{
	// Flag for rendering some lines used for developing. Shows
	// curl position and one for the direction from the
	// position given. Comes handy once playing around with different
	// ways for following pointer.
	private static final boolean DRAW_CURL_POSITION = false;
	// Flag for drawing polygon outlines. Using this flag crashes on emulator
	// due to reason unknown to me. Leaving it here anyway as seeing polygon
	// outlines gives good insight how original rectangle is divided.
	private static final boolean DRAW_POLYGON_OUTLINES = false;
	// Flag for enabling shadow rendering.
	private static final boolean DRAW_SHADOW = true;
	// Flag for texture rendering. While this is likely something you
	// don't want to do it's been used for development purposes as texture
	// rendering is rather slow on emulator.
	private static final boolean DRAW_TEXTURE = true;

	// Colors for shadow. Inner one is the color drawn next to surface where
	// shadowed area starts and outer one is color shadow ends to.
	private static final float[] SHADOW_INNER_COLOR = { 0f, 0f, 0f, .5f };
	private static final float[] SHADOW_OUTER_COLOR = { 0f, 0f, 0f, .0f };

	// Let's avoid using 'new' as much as possible. Meaning we introduce arrays
	// once here and reuse them on runtime. Doesn't really have very much effect
	// but avoids some garbage collections from happening.
	private Array<ShadowVertex> arrDropShadowVertices;
	private Array<Vertex> arrIntersections;
	private Array<Vertex> arrOutputVertices;
	private Array<Vertex> arrRotatedVertices;
	private Array<Double> arrScanLines;
	private Array<ShadowVertex> arrSelfShadowVertices;
	private Array<ShadowVertex> arrTempShadowVertices;
	private Array<Vertex> arrTempVertices;

	// Buffers for feeding rasterizer.
	private FloatBuffer bufColors;
	private FloatBuffer bufCurlPositionLines;
	private FloatBuffer bufShadowColors;
	private FloatBuffer bufShadowVertices;
	private FloatBuffer bufTexCoords;
	private FloatBuffer bufVertices;

	private int curlPositionLinesCount;
	private int dropShadowCount;

	// Boolean for 'flipping' texture sideways.
	private boolean flipTexture = false;
	// Maximum number of split lines used for creating a curl.
	private int maxCurlSplits;

	// Bounding rectangle for this mesh. mRectagle[0] = top-left corner,
	// rectangle[1] = bottom-left, rectangle[2] = top-right and rectangle[3]
	// bottom-right.
	private final Vertex[] rectangle = new Vertex[4];
	private int selfShadowCount;

	private boolean textureBack = false;
	// Texture ids and other variables.
	private int[] textureIds = null;
	private final CurlPage texturePage = new CurlPage();
	private final RectF textureRectBack = new RectF();
	private final RectF textureRectFront = new RectF();

	private int verticesCountBack;
	private int verticesCountFront;

	/**
	 * Constructor for mesh object.
	 * 
	 * @param maxCurlSplits
	 *            Maximum number curl can be divided into. The bigger the value
	 *            the smoother curl will be. With the cost of having more
	 *            polygons for drawing.
	 */
	public CurlMesh(int maxCurlSplits)
    {
		// There really is no use for 0 splits.
		this.maxCurlSplits = maxCurlSplits < 1 ? 1 : maxCurlSplits;

		arrScanLines = new Array<Double>(maxCurlSplits + 2);
		arrOutputVertices = new Array<Vertex>(7);
		arrRotatedVertices = new Array<Vertex>(4);
		arrIntersections = new Array<Vertex>(2);
		arrTempVertices = new Array<Vertex>(7 + 4);
		for (int i = 0; i < 7 + 4; ++i)
			arrTempVertices.add(new Vertex());

		if (DRAW_SHADOW)
		{
			arrSelfShadowVertices = new Array<ShadowVertex>((this.maxCurlSplits + 2) * 2);
			arrDropShadowVertices = new Array<ShadowVertex>((this.maxCurlSplits + 2) * 2);
			arrTempShadowVertices = new Array<ShadowVertex>((this.maxCurlSplits + 2) * 2);

			for (int i = 0; i < (this.maxCurlSplits + 2) * 2; ++i)
				arrTempShadowVertices.add(new ShadowVertex());
		}

		// Rectangle consists of 4 vertices. Index 0 = top-left, index 1 =
		// bottom-left, index 2 = top-right and index 3 = bottom-right.
		for (int i = 0; i < 4; ++i)
			rectangle[i] = new Vertex();

		// Set up shadow penumbra direction to each vertex. We do fake 'self
		// shadow' calculations based on this information.
		rectangle[0].mPenumbraX = rectangle[1].mPenumbraX = rectangle[1].mPenumbraY = rectangle[3].mPenumbraY = -1;
		rectangle[0].mPenumbraY = rectangle[2].mPenumbraX = rectangle[2].mPenumbraY = rectangle[3].mPenumbraX = 1;

		if (DRAW_CURL_POSITION)
		{
			curlPositionLinesCount = 3;
			ByteBuffer hvbb = ByteBuffer.allocateDirect(curlPositionLinesCount * 2 * 2 * 4);
			hvbb.order(ByteOrder.nativeOrder());
			bufCurlPositionLines = hvbb.asFloatBuffer();
			bufCurlPositionLines.position(0);
		}

		// There are 4 vertices from bounding rect, max 2 from adding split line
		// to two corners and curl consists of max maxCurlSplits lines each
		// outputting 2 vertices.
		int maxVerticesCount = 4 + 2 + (2 * this.maxCurlSplits);
		ByteBuffer vbb = ByteBuffer.allocateDirect(maxVerticesCount * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		bufVertices = vbb.asFloatBuffer();
		bufVertices.position(0);

		if (DRAW_TEXTURE)
		{
			ByteBuffer tbb = ByteBuffer.allocateDirect(maxVerticesCount * 2 * 4);
			tbb.order(ByteOrder.nativeOrder());
			bufTexCoords = tbb.asFloatBuffer();
			bufTexCoords.position(0);
		}

		ByteBuffer cbb = ByteBuffer.allocateDirect(maxVerticesCount * 4 * 4);
		cbb.order(ByteOrder.nativeOrder());
		bufColors = cbb.asFloatBuffer();
		bufColors.position(0);

		if (DRAW_SHADOW)
		{
			int maxShadowVerticesCount = (this.maxCurlSplits + 2) * 2 * 2;
			ByteBuffer scbb = ByteBuffer.allocateDirect(maxShadowVerticesCount * 4 * 4);
			scbb.order(ByteOrder.nativeOrder());
			bufShadowColors = scbb.asFloatBuffer();
			bufShadowColors.position(0);

			ByteBuffer sibb = ByteBuffer.allocateDirect(maxShadowVerticesCount * 3 * 4);
			sibb.order(ByteOrder.nativeOrder());
			bufShadowVertices = sibb.asFloatBuffer();
			bufShadowVertices.position(0);

			dropShadowCount = selfShadowCount = 0;
		}
	}

	/**
	 * Adds vertex to buffers.
	 */
	private void addVertex(Vertex vertex)
	{
		bufVertices.put((float) vertex.mPosX);
		bufVertices.put((float) vertex.mPosY);
		bufVertices.put((float) vertex.mPosZ);
		bufColors.put(vertex.mColorFactor * Color.red(vertex.mColor) / 255f);
		bufColors.put(vertex.mColorFactor * Color.green(vertex.mColor) / 255f);
		bufColors.put(vertex.mColorFactor * Color.blue(vertex.mColor) / 255f);
		bufColors.put(Color.alpha(vertex.mColor) / 255f);

		if (DRAW_TEXTURE)
		{
			bufTexCoords.put((float) vertex.mTexX);
			bufTexCoords.put((float) vertex.mTexY);
		}
	}

	/**
	 * Sets curl for this mesh.
	 * 
	 * @param curlPos  Position for curl 'center'. Can be any point on line collinear to curl.
	 * @param curlDir Curl direction, should be normalized.
	 * @param radius Radius of curl.
	 */
	public synchronized void curl(PointF curlPos, PointF curlDir, double radius)
	{
		if (DRAW_CURL_POSITION)			// First add some 'helper' lines used for development.
		{
			bufCurlPositionLines.position(0);

			bufCurlPositionLines.put(curlPos.x);
			bufCurlPositionLines.put(curlPos.y - 1.0f);
			bufCurlPositionLines.put(curlPos.x);
			bufCurlPositionLines.put(curlPos.y + 1.0f);
			bufCurlPositionLines.put(curlPos.x - 1.0f);
			bufCurlPositionLines.put(curlPos.y);
			bufCurlPositionLines.put(curlPos.x + 1.0f);
			bufCurlPositionLines.put(curlPos.y);

			bufCurlPositionLines.put(curlPos.x);
			bufCurlPositionLines.put(curlPos.y);
			bufCurlPositionLines.put(curlPos.x + curlDir.x * 2);
			bufCurlPositionLines.put(curlPos.y + curlDir.y * 2);

			bufCurlPositionLines.position(0);
		}

		// Actual 'curl' implementation starts here.
		bufVertices.position(0);
		bufColors.position(0);

		if (DRAW_TEXTURE)
			bufTexCoords.position(0);

		// Calculate curl angle from direction.
		double curlAngle = Math.acos(curlDir.x);
		curlAngle = curlDir.y > 0 ? -curlAngle : curlAngle;

		// Initiate rotated rectangle which's is translated to curlPos and
		// rotated so that curl direction heads to right (1,0). Vertices are
		// ordered in ascending order based on value1 -coordinate at the same time.
		// And using value2 -coordinate in very rare case in which two vertices have
		// same value1 -coordinate.
		arrTempVertices.addAll(arrRotatedVertices);
		arrRotatedVertices.clear();

		for (int i = 0; i < 4; ++i)
		{
			Vertex v = arrTempVertices.remove(0);
			v.set(rectangle[i]);
			v.translate(-curlPos.x, -curlPos.y);
			v.rotateZ(-curlAngle);
			int j = 0;
			for (; j < arrRotatedVertices.size(); ++j)
			{
				Vertex v2 = arrRotatedVertices.get(j);
				if (v.mPosX > v2.mPosX)
					break;

				if (v.mPosX == v2.mPosX && v.mPosY > v2.mPosY)
					break;
			}
			arrRotatedVertices.add(j, v);
		}

		// Rotated rectangle lines/vertex indices. We need to find bounding
		// lines for rotated rectangle. After sorting vertices according to
		// their value1 -coordinate we don't have to worry about vertices at indices
		// 0 and 1. But due to inaccuracy it's possible vertex 3 is not the
		// opposing corner from vertex 0. So we are calculating distance from
		// vertex 0 to vertices 2 and 3 - and altering line indices if needed.
		// Also vertices/lines are given in an order first one has value1 -coordinate
		// at least the latter one. This property is used in getIntersections to
		// see if there is an intersection.
		int lines[][] = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 2, 3 } };
		{
			// TODO: There really has to be more 'easier' way of doing this -
			// not including extensive use of sqrt.
			Vertex v0 = arrRotatedVertices.get(0);
			Vertex v2 = arrRotatedVertices.get(2);
			Vertex v3 = arrRotatedVertices.get(3);
			double dist2 = Math.sqrt((v0.mPosX - v2.mPosX)
					* (v0.mPosX - v2.mPosX) + (v0.mPosY - v2.mPosY)
					* (v0.mPosY - v2.mPosY));
			double dist3 = Math.sqrt((v0.mPosX - v3.mPosX)
					* (v0.mPosX - v3.mPosX) + (v0.mPosY - v3.mPosY)
					* (v0.mPosY - v3.mPosY));
			if (dist2 > dist3)
			{
				lines[1][1] = 3;
				lines[2][1] = 2;
			}
		}

		verticesCountFront = verticesCountBack = 0;

		if (DRAW_SHADOW)
		{
			arrTempShadowVertices.addAll(arrDropShadowVertices);
			arrTempShadowVertices.addAll(arrSelfShadowVertices);
			arrDropShadowVertices.clear();
			arrSelfShadowVertices.clear();
		}

		// Length of 'curl' curve.
		double curlLength = Math.PI * radius;
		// Calculate scan lines.
		// TODO: Revisit this code one day. There is room for optimization here.
		arrScanLines.clear();
		if (maxCurlSplits > 0)
			arrScanLines.add((double) 0);

		for (int i = 1; i < maxCurlSplits; ++i)
			arrScanLines.add((-curlLength * i) / (maxCurlSplits - 1));

		// As mRotatedVertices is ordered regarding value1 -coordinate, adding
		// this scan line produces scan area picking up vertices which are
		// rotated completely. One could say 'until infinity'.
		arrScanLines.add(arrRotatedVertices.get(3).mPosX - 1);

		// Start from right most vertex. Pretty much the same as first scan area
		// is starting from 'infinity'.
		double scanXmax = arrRotatedVertices.get(0).mPosX + 1;

		for (int i = 0; i < arrScanLines.size(); ++i)
		{
			// Once we have scanXmin and scanXmax we have a scan area to start
			// working with.
			double scanXmin = arrScanLines.get(i);
			// First iterate 'original' rectangle vertices within scan area.
			for (int j = 0; j < arrRotatedVertices.size(); ++j)
			{
				Vertex v = arrRotatedVertices.get(j);
				// Test if vertex lies within this scan area.
				// TODO: Frankly speaking, can't remember why equality check was
				// added to both ends. Guessing it was somehow related to case
				// where radius=0f, which, given current implementation, could
				// be handled much more effectively anyway.
				if (v.mPosX >= scanXmin && v.mPosX <= scanXmax)
				{
					// Pop out a vertex from temp vertices.
					Vertex n = arrTempVertices.remove(0);
					n.set(v);
					// This is done solely for triangulation reasons. Given a
					// rotated rectangle it has max 2 vertices having
					// intersection.
					Array<Vertex> intersections = getIntersections(
							arrRotatedVertices, lines, n.mPosX);
					// In a sense one could say we're adding vertices always in
					// two, positioned at the ends of intersecting line. And for
					// triangulation to work properly they are added based on value2
					// -coordinate. And this if-else is doing it for us.
					if (intersections.size() == 1 && intersections.get(0).mPosY > v.mPosY)
					{
						// In case intersecting vertex is higher add it first.
						arrOutputVertices.addAll(intersections);
						arrOutputVertices.add(n);
					}
					else if (intersections.size() <= 1)
					{
						// Otherwise add original vertex first.
						arrOutputVertices.add(n);
						arrOutputVertices.addAll(intersections);
					}
					else
					{
						// There should never be more than 1 intersecting
						// vertex. But if it happens as a fallback simply skip
						// everything.
						arrTempVertices.add(n);
						arrTempVertices.addAll(intersections);
					}
				}
			}

			// Search for scan line intersections.
			Array<Vertex> intersections = getIntersections(arrRotatedVertices,
					lines, scanXmin);

			// We expect to get 0 or 2 vertices. In rare cases there's only one
			// but in general given a scan line intersecting rectangle there
			// should be 2 intersecting vertices.
			if (intersections.size() == 2)
			{
				// There were two intersections, add them based on value2
				// -coordinate, higher first, lower last.
				Vertex v1 = intersections.get(0);
				Vertex v2 = intersections.get(1);
				if (v1.mPosY < v2.mPosY)
				{
					arrOutputVertices.add(v2);
					arrOutputVertices.add(v1);
				}
				else
				{
					arrOutputVertices.addAll(intersections);
				}
			}
			else if (intersections.size() != 0)
			{
				// This happens in a case in which there is a original vertex
				// exactly at scan line or something went very much wrong if
				// there are 3+ vertices. What ever the reason just return the
				// vertices to temp vertices for later use. In former case it
				// was handled already earlier once iterating through
				// mRotatedVertices, in latter case it's better to avoid doing
				// anything with them.
				arrTempVertices.addAll(intersections);
			}

			// Add vertices found during this iteration to vertex etc buffers.
			while (arrOutputVertices.size() > 0)
			{
				Vertex v = arrOutputVertices.remove(0);
				arrTempVertices.add(v);

				// Local texture front-facing flag.
				boolean textureFront;

				// Untouched vertices.
				if (i == 0)
				{
					textureFront = true;
					verticesCountFront++;
				}
				// 'Completely' rotated vertices.
				else if (i == arrScanLines.size() - 1 || curlLength == 0)
				{
					v.mPosX = -(curlLength + v.mPosX);
					v.mPosZ = 2 * radius;
					v.mPenumbraX = -v.mPenumbraX;

					textureFront = false;
					verticesCountBack++;
				}
				// Vertex lies within 'curl'.
				else
				{
					// Even though it's not obvious from the if-else clause,
					// here v.mPosX is between [-curlLength, 0]. And we can do
					// calculations around a half cylinder.
					double rotY = Math.PI * (v.mPosX / curlLength);
					v.mPosX = radius * Math.sin(rotY);
					v.mPosZ = radius - (radius * Math.cos(rotY));
					v.mPenumbraX *= Math.cos(rotY);
					// Map color multiplier to [.1f, 1f] range.
					v.mColorFactor = (float) (.1f + .9f * Math.sqrt(Math
							.sin(rotY) + 1));

					if (v.mPosZ >= radius)
					{
						textureFront = false;
						verticesCountBack++;
					}
					else
					{
						textureFront = true;
						verticesCountFront++;
					}
				}

				// We use local textureFront for flipping backside texture
				// locally. Plus additionally if mesh is in flip texture mode,
				// we'll make the procedure "backwards". Also, until this point,
				// texture coordinates are within [0, 1] range so we'll adjust
				// them to final texture coordinates too.
				if (textureFront != flipTexture)
				{
					v.mTexX *= textureRectFront.right;
					v.mTexY *= textureRectFront.bottom;
					v.mColor = texturePage.getColor(PageSide.Front);
				}
				else
				{
					v.mTexX *= textureRectBack.right;
					v.mTexY *= textureRectBack.bottom;
					v.mColor = texturePage.getColor(PageSide.Back);
				}

				// Move vertex back to 'world' coordinates.
				v.rotateZ(curlAngle);
				v.translate(curlPos.x, curlPos.y);
				addVertex(v);

				// Drop shadow is cast 'behind' the curl.
				if (DRAW_SHADOW && v.mPosZ > 0 && v.mPosZ <= radius)
				{
					ShadowVertex sv = arrTempShadowVertices.remove(0);
					sv.mPosX = v.mPosX;
					sv.mPosY = v.mPosY;
					sv.mPosZ = v.mPosZ;
					sv.mPenumbraX = (v.mPosZ / 2) * -curlDir.x;
					sv.mPenumbraY = (v.mPosZ / 2) * -curlDir.y;
					sv.mPenumbraColor = v.mPosZ / radius;
					int idx = (arrDropShadowVertices.size() + 1) / 2;
					arrDropShadowVertices.add(idx, sv);
				}
				// Self shadow is cast partly over mesh.
				if (DRAW_SHADOW && v.mPosZ > radius)
				{
					ShadowVertex sv = arrTempShadowVertices.remove(0);
					sv.mPosX = v.mPosX;
					sv.mPosY = v.mPosY;
					sv.mPosZ = v.mPosZ;
					sv.mPenumbraX = ((v.mPosZ - radius) / 3) * v.mPenumbraX;
					sv.mPenumbraY = ((v.mPosZ - radius) / 3) * v.mPenumbraY;
					sv.mPenumbraColor = (v.mPosZ - radius) / (2 * radius);
					int idx = (arrSelfShadowVertices.size() + 1) / 2;
					arrSelfShadowVertices.add(idx, sv);
				}
			}

			// Switch scanXmin as scanXmax for next iteration.
			scanXmax = scanXmin;
		}

		bufVertices.position(0);
		bufColors.position(0);
		if (DRAW_TEXTURE)
			bufTexCoords.position(0);

		// Add shadow Vertices.
		if (DRAW_SHADOW)
		{
			bufShadowColors.position(0);
			bufShadowVertices.position(0);
			dropShadowCount = 0;

			for (int i = 0; i < arrDropShadowVertices.size(); ++i)
			{
				ShadowVertex sv = arrDropShadowVertices.get(i);
				bufShadowVertices.put((float) sv.mPosX);
				bufShadowVertices.put((float) sv.mPosY);
				bufShadowVertices.put((float) sv.mPosZ);
				bufShadowVertices.put((float) (sv.mPosX + sv.mPenumbraX));
				bufShadowVertices.put((float) (sv.mPosY + sv.mPenumbraY));
				bufShadowVertices.put((float) sv.mPosZ);

				for (int j = 0; j < 4; ++j)
				{
					double color = SHADOW_OUTER_COLOR[j] + (SHADOW_INNER_COLOR[j] - SHADOW_OUTER_COLOR[j]) * sv.mPenumbraColor;
					bufShadowColors.put((float) color);
				}
				bufShadowColors.put(SHADOW_OUTER_COLOR);
				dropShadowCount += 2;
			}
			selfShadowCount = 0;
			for (int i = 0; i < arrSelfShadowVertices.size(); ++i)
			{
				ShadowVertex sv = arrSelfShadowVertices.get(i);
				bufShadowVertices.put((float) sv.mPosX);
				bufShadowVertices.put((float) sv.mPosY);
				bufShadowVertices.put((float) sv.mPosZ);
				bufShadowVertices.put((float) (sv.mPosX + sv.mPenumbraX));
				bufShadowVertices.put((float) (sv.mPosY + sv.mPenumbraY));
				bufShadowVertices.put((float) sv.mPosZ);
				for (int j = 0; j < 4; ++j)
				{
					double color = SHADOW_OUTER_COLOR[j] + (SHADOW_INNER_COLOR[j] - SHADOW_OUTER_COLOR[j]) * sv.mPenumbraColor;
					bufShadowColors.put((float) color);
				}
				bufShadowColors.put(SHADOW_OUTER_COLOR);
				selfShadowCount += 2;
			}
			bufShadowColors.position(0);
			bufShadowVertices.position(0);
		}
	}

	/**
	 * Calculates intersections for given scan line.
	 */
	private Array<Vertex> getIntersections(Array<Vertex> vertices, int[][] lineIndices, double scanX)
	{
		arrIntersections.clear();
		// Iterate through rectangle lines each re-presented as a pair of
		// vertices.
		for (int j = 0; j < lineIndices.length; j++)
		{
			Vertex v1 = vertices.get(lineIndices[j][0]);
			Vertex v2 = vertices.get(lineIndices[j][1]);
			// Here we expect that v1.mPosX >= v2.mPosX and wont do intersection
			// test the opposite way.
			if (v1.mPosX > scanX && v2.mPosX < scanX)
			{
				// There is an intersection, calculate coefficient telling 'how
				// far' scanX is from v2.
				double c = (scanX - v2.mPosX) / (v1.mPosX - v2.mPosX);
				Vertex n = arrTempVertices.remove(0);
				n.set(v2);
				n.mPosX = scanX;
				n.mPosY += (v1.mPosY - v2.mPosY) * c;
				if (DRAW_TEXTURE)
				{
					n.mTexX += (v1.mTexX - v2.mTexX) * c;
					n.mTexY += (v1.mTexY - v2.mTexY) * c;
				}
				if (DRAW_SHADOW)
				{
					n.mPenumbraX += (v1.mPenumbraX - v2.mPenumbraX) * c;
					n.mPenumbraY += (v1.mPenumbraY - v2.mPenumbraY) * c;
				}
				arrIntersections.add(n);
			}
		}
		return arrIntersections;
	}

	/**
	 * Getter for textures page for this mesh.
	 */
	public synchronized CurlPage getTexturePage()
	{
		return texturePage;
	}

	/**
	 * Renders our page curl mesh.
	 */
	public synchronized void onDrawFrame(GL10 gl)
	{
		// First allocate texture if there is not one yet.
		if (DRAW_TEXTURE && textureIds == null)
		{
			// Generate texture.
			textureIds = new int[2];
			gl.glGenTextures(2, textureIds, 0);
			for (int textureId : textureIds)
			{
				// Set texture attributes.
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			}
		}

		if (DRAW_TEXTURE && texturePage.getTexturesChanged())
		{
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
			Bitmap texture = texturePage.getTexture(textureRectFront, PageSide.Front);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
			texture.recycle();

			textureBack = texturePage.hasBackTexture();
			if (textureBack)
			{
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[1]);
				texture = texturePage.getTexture(textureRectBack, PageSide.Back);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
				texture.recycle();
			}
			else
				textureRectBack.set(textureRectFront);

			texturePage.recycle();
			reset();
		}

		// Some 'global' settings.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// TODO: Drop shadow drawing is done temporarily here to hide some
		// problems with its calculation.
		if (DRAW_SHADOW)
		{
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, bufShadowColors);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufShadowVertices);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, dropShadowCount);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisable(GL10.GL_BLEND);
		}

		if (DRAW_TEXTURE)
		{
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufTexCoords);
		}

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufVertices);
		// Enable color array.
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, bufColors);

		// Draw front facing blank vertices.
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, verticesCountFront);

		// Draw front facing texture.
		if (DRAW_TEXTURE)
		{
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_TEXTURE_2D);

			if (!flipTexture || !textureBack)
			{
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
			}
			else
			{
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[1]);
			}

			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, verticesCountFront);

			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}

		int backStartIdx = Math.max(0, verticesCountFront - 2);
		int backCount = verticesCountFront + verticesCountBack - backStartIdx;

		// Draw back facing blank vertices.
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount);

		// Draw back facing texture.
		if (DRAW_TEXTURE)
		{
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_TEXTURE_2D);

			if (flipTexture || !textureBack)
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
			else
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[1]);

			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount);

			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}

		// Disable textures and color array.
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		if (DRAW_POLYGON_OUTLINES)
		{
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glLineWidth(1.0f);
			gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufVertices);
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, verticesCountFront);
			gl.glDisable(GL10.GL_BLEND);
		}

		if (DRAW_CURL_POSITION)
		{
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glLineWidth(1.0f);
			gl.glColor4f(1.0f, 0.5f, 0.5f, 1.0f);
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufCurlPositionLines);
			gl.glDrawArrays(GL10.GL_LINES, 0, curlPositionLinesCount * 2);
			gl.glDisable(GL10.GL_BLEND);
		}

		if (DRAW_SHADOW)
		{
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, bufShadowColors);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufShadowVertices);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, dropShadowCount,
					selfShadowCount);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisable(GL10.GL_BLEND);
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	/**
	 * Resets mesh to 'initial' state. Meaning this mesh will draw a plain
	 * textured rectangle after call to this method.
	 */
	public synchronized void reset()
	{
		bufVertices.position(0);
		bufColors.position(0);

		if (DRAW_TEXTURE)
			bufTexCoords.position(0);

		for (int i = 0; i < 4; ++i)
		{
			Vertex tmp = arrTempVertices.get(0);
			tmp.set(rectangle[i]);

			if (flipTexture)
			{
				tmp.mTexX *= textureRectBack.right;
				tmp.mTexY *= textureRectBack.bottom;
				tmp.mColor = texturePage.getColor(PageSide.Back);
			}
			else
			{
				tmp.mTexX *= textureRectFront.right;
				tmp.mTexY *= textureRectFront.bottom;
				tmp.mColor = texturePage.getColor(PageSide.Front);
			}

			addVertex(tmp);
		}

		verticesCountFront = 4;
		verticesCountBack = 0;
		bufVertices.position(0);
		bufColors.position(0);
		if (DRAW_TEXTURE) {
			bufTexCoords.position(0);
		}

		dropShadowCount = selfShadowCount = 0;
	}

	/**
	 * Resets allocated texture id forcing creation of new one. After calling
	 * this method you most likely want to set bitmap too as it's lost. This
	 * method should be called only once e.g GL context is re-created as this
	 * method does not release previous texture id, only makes sure new one is
	 * requested on next render.
	 */
	public synchronized void resetTexture()
	{
		textureIds = null;
	}

	/**
	 * If true, flips texture sideways.
	 */
	public synchronized void setFlipTexture(boolean flipTexture)
	{
		this.flipTexture = flipTexture;
		if (flipTexture)
		{
			setTexCoords(1f, 0f, 0f, 1f);
		}
		else
		{
			setTexCoords(0f, 0f, 1f, 1f);
		}
	}

	/**
	 * Update mesh bounds.
	 */
	public void setRect(RectF r)
	{
		rectangle[0].mPosX = r.left;
		rectangle[0].mPosY = r.top;
		rectangle[1].mPosX = r.left;
		rectangle[1].mPosY = r.bottom;
		rectangle[2].mPosX = r.right;
		rectangle[2].mPosY = r.top;
		rectangle[3].mPosX = r.right;
		rectangle[3].mPosY = r.bottom;
	}

	/**
	 * Sets texture coordinates to rectangle vertices.
	 */
	private synchronized void setTexCoords(float left, float top, float right, float bottom)
	{
		rectangle[0].mTexX = left;
		rectangle[0].mTexY = top;
		rectangle[1].mTexX = left;
		rectangle[1].mTexY = bottom;
		rectangle[2].mTexX = right;
		rectangle[2].mTexY = top;
		rectangle[3].mTexX = right;
		rectangle[3].mTexY = bottom;
	}

	/**
	 * Simple fixed size array implementation.
	 */
	private class Array<T>
	{
		private Object[] mArray;
		private int mCapacity;
		private int mSize;

		public Array(int capacity)
		{
			mCapacity = capacity;
			mArray = new Object[capacity];
		}

		public void add(int index, T item)
		{
			if (index < 0 || index > mSize || mSize >= mCapacity)
				throw new IndexOutOfBoundsException();

			for (int i = mSize; i > index; --i)
				mArray[i] = mArray[i - 1];

			mArray[index] = item;
			++mSize;
		}

		public void add(T item)
		{
			if (mSize >= mCapacity)
				throw new IndexOutOfBoundsException();

			mArray[mSize++] = item;
		}

		public void addAll(Array<T> array)
		{
			if (mSize + array.size() > mCapacity)
				throw new IndexOutOfBoundsException();

			for (int i = 0; i < array.size(); ++i)
				mArray[mSize++] = array.get(i);
		}

		public void clear() {
			mSize = 0;
		}

		@SuppressWarnings("unchecked")
		public T get(int index)
		{
			if (index < 0 || index >= mSize)
				throw new IndexOutOfBoundsException();

			return (T) mArray[index];
		}

		@SuppressWarnings("unchecked")
		public T remove(int index)
		{
			if (index < 0 || index >= mSize)
				throw new IndexOutOfBoundsException();

			T item = (T) mArray[index];
			for (int i = index; i < mSize - 1; ++i)
				mArray[i] = mArray[i + 1];

			--mSize;
			return item;
		}

		public int size() {
			return mSize;
		}
	}
}
