package com.syleiman.comicsviewer.Activities.ViewComics;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.syleiman.comicsviewer.R;
import com.syleiman.comicsviewer.Activities.ActivityCodes;
import com.syleiman.comicsviewer.Activities.ActivityResultCodes;
import com.syleiman.comicsviewer.Activities.PagesMap.PagesMapActivity;
import com.syleiman.comicsviewer.Common.Dialogs.GotoPageDialog;
import com.syleiman.comicsviewer.Dal.DalFacade;
import com.syleiman.comicsviewer.Dal.Dto.Comics;

/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
public class CurlActivity extends Activity
{
	private CurlView curlView;

	private long comicsId;

	/**
	 * Start activity
	 */
	public static void start(Activity parentActivity, long comicsId)
	{
		Intent intent = new Intent(parentActivity, CurlActivity.class);              // Start view comics

		Bundle b = new Bundle();
		b.putLong(ActivityParamCodes.IdOfComics, comicsId);
		intent.putExtras(b);

		parentActivity.startActivityForResult(intent, ActivityCodes.CurlActivity);
	}

	/**
	 * Parse result of activity
	 */
	public static long parseResult(Intent data)
	{
		return data.getLongExtra(ActivityResultCodes.idOfComics, 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		comicsId = getIntent().getExtras().getLong(ActivityParamCodes.IdOfComics);

		setContentView(R.layout.activity_view_comics);

		int currentPageIndex= DalFacade.Comics.getComicsById(comicsId).lastViewedPageIndex;

		curlView = (CurlView) findViewById(R.id.curl);
		curlView.setPageProvider(new PageProvider(comicsId));
		curlView.initCurrentPageIndex(currentPageIndex);
		curlView.setBackgroundColor(0xFF202830);
		curlView.setCallbackHandlers((pageIndex) -> onPageChanged(pageIndex), ()->onShowMenu());

		// This is something somewhat experimental. Before uncommenting next
		// line, please see method comments in CurlView.
		// curlView.setEnableTouchPressure(true);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		curlView.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		curlView.onResume();
	}

	/**
	 * When user curled to new page
	 * @param currentPageIndex new page index
	 */
	private void onPageChanged(int currentPageIndex)
	{
		DalFacade.Comics.updateLastViewedPageIndex(comicsId, currentPageIndex);
	}

	/**
	 * When we need show menu
	 */
	private void onShowMenu()
	{
		CharSequence colors[] = new CharSequence[] {getString(R.string.menuitem_gotopage), getString(R.string.menuitem_pages), getString(R.string.menuitem_close_comics)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setItems(colors, (baseDialog, which) -> {
			switch (which)
			{
				case 0: { processGotoPage(); break;	}			// Goto page
				case 1: { processPages(); break; }			// Pages
				case 2: { onBackPressed(); break; }			// Close comics
			}
        });
		Dialog dialog=builder.show();
		dialog.setCanceledOnTouchOutside(true);
	}

	/**
	 * Goto page action
	 */
	private void processGotoPage()
	{
		Comics comics=DalFacade.Comics.getComicsById(comicsId);

		GotoPageDialog gotoPageDialog=new GotoPageDialog(this,
			(result)->
			{
				if(result.pageNumber!=comics.lastViewedPageIndex)
					curlView.setCurrentPageIndex(result.pageNumber);
			},
				new GotoPageDialog.InputModel(comics.lastViewedPageIndex, comics.totalPages));

		gotoPageDialog.show();
	}

	private void processPages()
	{
		PagesMapActivity.start(this, comicsId);				// Start view pages
	}

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent();
		intent.putExtra(ActivityResultCodes.idOfComics, comicsId);
		setResult(RESULT_OK, intent);

		super.onBackPressed();              // call finish() and close acivity
	}

	/**
	 *  Processing result from child activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (data == null || resultCode!=RESULT_OK)
			return;

		if(requestCode==ActivityCodes.PagesMapActivity)
			curlView.setCurrentPageIndex(PagesMapActivity.parseResult(data));
	}
}