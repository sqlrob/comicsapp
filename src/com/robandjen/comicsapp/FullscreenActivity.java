/* Copyright 2012-2014, Robert Myers */

/*
 * This file is part of ComicsApp.

    ComicsApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Comics is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ComicsApp.  If not, see <http://www.gnu.org/licenses/>
 */

package com.robandjen.comicsapp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.robandjen.comicsapp.DownloadTask.DownloadResults;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class FullscreenActivity extends Activity implements DownloadResults, URLFragment.URLEvent {

    private List<ComicsEntry> mComicList;
    private int mCurComic = 0;
    private static final String TAG = "ComicsAppWebActiviy";
    
    private static final String CURCOMICKEY = "CurrentComic";
    private static final String CURURLKEY = "CurrentURL";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private URL mDownloadUrl;
    private ExpandableComicListAdapter mAdapter;
    
    private static final String COMICFILE = "comics.xml";
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_fullscreen);

        final WebView v = (WebView) findViewById(R.id.fullscreen_content);
        v.setWebViewClient(new WebViewClient() {
    		@Override
    		public boolean shouldOverrideUrlLoading(WebView view,String url) {
    			updateShare(url);
    			return false;
    		}

			@Override
			public void onPageFinished(WebView view, String url) {
				setProgressBarIndeterminateVisibility(false);
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setProgressBarIndeterminateVisibility(true);
				super.onPageStarted(view, url, favicon);
			}
    	});

    	final WebSettings settings = v.getSettings();
    	settings.setBuiltInZoomControls(true);
    	settings.setJavaScriptEnabled(true);
    	
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer,R.string.open_drawer,R.string.close_drawer);
    	mDrawerLayout.setDrawerListener(mDrawerToggle);
    	
    	getActionBar().setHomeButtonEnabled(true);
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    	
        if (mComicList == null) {
        	
			try {
				InputStream is = openFileInput(COMICFILE);
				if (!loadXml(is)) {
					Log.w(TAG,"Downloaded list not parseable, defaulting to built-in");
				}
			} catch (FileNotFoundException e) {
				Log.i(TAG,"Downloaded list not found, defaulting to built-in");
				
			}
        }
        
        if (mComicList == null) {
        	loadDefaultXML();
        }
        
        if (savedInstanceState != null) {
        	mCurComic = savedInstanceState.getInt(CURCOMICKEY,0);
        	final String url = savedInstanceState.getString(CURURLKEY);        	
        	showCurrentComic(url); 	
        }
        else {
        	do {
        		if (!bSkipOther || !mComicList.get(mCurComic).getOther()) {
        			break;
        		}
        		
        		++mCurComic;
        		mCurComic %= mComicList.size();
        	} while(mCurComic != 0);
        	showCurrentComic();
        }
  
    }

    void showCurrentComic(String url) {
    	if (url == null || url.isEmpty()) {
    		url = mComicList.get(mCurComic).getURL();
    	}
    	
    	final WebView contentView = (WebView) findViewById(R.id.fullscreen_content);
    	contentView.stopLoading();
    	
    	//Load about:blank to clear any extra data and have a well defined URL in history
    	contentView.loadUrl("about:blank");
    	contentView.loadUrl(url);
    	contentView.clearHistory();
    	ActionBar actionBar = getActionBar();
    	if (actionBar != null) {
    		actionBar.setTitle(mComicList.get(mCurComic).getName());
    	}
    	updateShare(url);
    	
    	ExpandableListView elv = (ExpandableListView) findViewById(R.id.comic_drawer);
    	long packedPos = mAdapter.comicsPosToPackedPos(mCurComic);
    	
    	//Selection doesn't work if expandGroup isn't called or group already expanded. ?????
    	elv.expandGroup(ExpandableListView.getPackedPositionGroup(packedPos));
    	elv.setSelectedChild(ExpandableListView.getPackedPositionGroup(packedPos),ExpandableListView.getPackedPositionChild(packedPos),true);
    	elv.setItemChecked(elv.getFlatListPosition(packedPos), true);
    }
    
    void showCurrentComic() {
    	showCurrentComic(mComicList.get(mCurComic).getURL());
    }
    
    final static boolean bSkipOther = true;
    
    void nextComic() {
    	int lastComic = mCurComic;
    	do {
	    	if (++mCurComic >= mComicList.size()) {
	    		mCurComic = 0;
	    	}
	    	
	    	if (!bSkipOther || !mComicList.get(mCurComic).getOther()) {
	    		break;
	    	}
    	} while (lastComic != mCurComic);
    	showCurrentComic();
    }
    
    void previousComic() {
    	int lastComic = mCurComic;
    	do {
	    	if (mCurComic == 0) {
	    		mCurComic = mComicList.size() - 1;
	    	}
	    	else {
	    		--mCurComic;
	    	}
	    	if (!bSkipOther || !mComicList.get(mCurComic).getOther()) {
	    		break;
	    	}
	    } while (lastComic != mCurComic);
    	showCurrentComic();
    }
    
	@Override
    protected void onResume() {
    	super.onResume();
    	
    	final ComicsWebView v = (ComicsWebView) findViewById(R.id.fullscreen_content);
    	v.onActivityResume();
    	v.setListener(new ComicsEvents() {
    		@Override 
    		public void onNextComic(View v) {
    			nextComic();
    		}
    		@Override 
    		public void onPreviousComic(View v) {
    			previousComic();
    		}
    	});
    	
    	final Button next = (Button) findViewById(R.id.next);
    	next.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			nextComic();
    		}
    	});
    	
    	final Button prev = (Button) findViewById(R.id.previous);
    	prev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				previousComic();
			}
		});
    	
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	final ComicsWebView v = (ComicsWebView) findViewById(R.id.fullscreen_content);
    	v.setListener(null);
    	super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
    	super.onSaveInstanceState(bundle);
    	bundle.putInt(CURCOMICKEY, mCurComic);
    	
    	final WebView wv = (WebView) findViewById(R.id.fullscreen_content);
    	if (wv != null) {
    		bundle.putString(CURURLKEY, wv.getUrl());
    	}
    	
    }
    
    
    ShareActionProvider mShareProvider;
    Intent mShareIntent;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	
    	MenuItem shareitem = menu.findItem(R.id.menu_share);
    	mShareProvider = (ShareActionProvider) shareitem.getActionProvider();
    	if (mShareProvider != null) {
	    	mShareIntent = new Intent();
	    	mShareIntent.setAction(Intent.ACTION_SEND);
	    	mShareIntent.setType("text/plain");
	    	mShareProvider.setShareIntent(mShareIntent);
    	}
    	return super.onCreateOptionsMenu(menu);
    }
    
    private void updateShare(String url) {
    	if (mShareIntent != null) {
	    	mShareIntent.putExtra(Intent.EXTRA_TEXT,url);
    	}
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		final int id = item.getItemId();
		
		if (id == R.id.menu_reload) {
			onReload();
			return true;
		}

		if (id == R.id.menu_cancel) {
			onCancel();
			return true;
		}

		if (id == R.id.menu_browser) {
			
			WebView wv = (WebView) findViewById(R.id.fullscreen_content);
			String url = wv.getUrl();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}
	
		if (id == R.id.download) {
			URLFragment uf = URLFragment.newInstance();
			uf.show(getFragmentManager(), "url");
			return true;
		}
		
		if (id == R.id.cleardata) {
			deleteFile(COMICFILE);
			loadDefaultXML();
			return true;
		}
		
		if (id == R.id.settings) {
			Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadDefaultXML() {
		InputStream is;
		try {
			is = getAssets().open(COMICFILE);
			loadXml(is);
		} catch (IOException e) {
			Log.wtf(TAG, "Exception loading default XML",e);
		}
	}

	@Override
	public void onBackPressed() {
		ComicsWebView wv = (ComicsWebView) findViewById(R.id.fullscreen_content);
		if (wv.canGoBack()) {
			
			//I couldn't remove every URL from the list, so stop when I hit
			//about:blank, I'm all the way back then
			WebBackForwardList bdl = wv.copyBackForwardList();
			final int cur = bdl.getCurrentIndex() - 1;
			if (cur >= 0) {
				WebHistoryItem whi = bdl.getItemAtIndex(cur);
				String url = whi.getUrl();
				if (!url.equals("about:blank")) {
					wv.goBack();
				}
			}
			
		}
		
	}

	private void onReload() {
		WebView wv = (WebView) findViewById(R.id.fullscreen_content);
		wv.reload();
	}

	private void onCancel() {
		WebView wv = (WebView) findViewById(R.id.fullscreen_content);
		wv.stopLoading();
	}

	@Override
	public void onDownloadComplete(String results) {
		if (setComicsXml(results)) {
			showCurrentComic();
			SharedPreferences pref = getSharedPreferences(null, 0);
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("url", mDownloadUrl.toString());
			edit.commit();
		}
		
	}

	@Override
	public void onDownloadFailed(int code) {
		String format = getString(R.string.http_error);
		Toast.makeText(this, String.format(format, code), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDownloadFailed(Exception e) {
		if (e instanceof FileNotFoundException) {
			Toast.makeText(this, getString(R.string.http_not_found), Toast.LENGTH_LONG).show();
		} else {
			String format = getString(R.string.http_exception);
			Toast.makeText(this, String.format(format,e.getClass().toString()), Toast.LENGTH_LONG).show();
		}
	}
	
	boolean setComicsXml(InputStream is) {
		try {
	   		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    		XmlPullParser parser = factory.newPullParser();
    		parser.setInput(new BufferedInputStream(is), null);
    		mComicList = ComicsParser.parse(parser);
		}
		catch (Exception e) {
			return false;
		}
		
		//Reset to beginning
		mCurComic = 0;
		
		if (mComicList != null) {
        	ExpandableListView elv = (ExpandableListView) findViewById(R.id.comic_drawer);
        	mAdapter = new ExpandableComicListAdapter(this, mComicList);
        	elv.setAdapter(mAdapter);
        	elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					mCurComic = mAdapter.childToComicsPos(groupPosition, childPosition);
					showCurrentComic();
					mDrawerLayout.closeDrawers();
					return true;
				}
			});
        }
		
		return true;
	}
	
	boolean setComicsXml(String xml) {
		boolean succeeded = setComicsXml(new ByteArrayInputStream(xml.getBytes()));
		OutputStream os = null;
		try {
			os = openFileOutput(COMICFILE, Context.MODE_PRIVATE);
			os.write(xml.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return succeeded;
	}
	
	boolean loadXml(InputStream is) {
		try {
			return setComicsXml(is);
		}
		finally {
			try {
				is.close();
			}
			catch (Exception e) {
				Log.e(TAG,"Unable to close stream",e);
			}
		}
		
	}

	@Override
	public void onURLChosen(URL Url) {
		mDownloadUrl = Url;
		DownloadFragment df = DownloadFragment.newInstance(Url);
		df.show(getFragmentManager(), "download");
	}

	@Override
	protected void onPause() {
		ComicsWebView wv = (ComicsWebView) findViewById(R.id.fullscreen_content);
		wv.onActivityPause();
		super.onPause();
	}
	
	
}
