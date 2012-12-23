package com.robandjen.comicsapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ComicsWebView extends WebView {

	GestureDetector mGestureDetect;
	ComicsEvents mEvents;
	static final float MAXTHRESHOLDY = 500f;
	static final float MINTHRESHOLDX = 500f;
	public void setListener(ComicsEvents listener) {
		mEvents = listener;
	}
	private void initDetector() {
		mGestureDetect = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1,MotionEvent e2,float velocityX,float velocityY) {
				boolean rc = false;
				if (mEvents != null) {
					if (Math.abs(velocityY) < Math.abs(velocityX) && Math.abs(velocityX) > MINTHRESHOLDX) {
						if (velocityX < 0) {
							mEvents.onNextComic(ComicsWebView.this);
						}
						else {
							mEvents.onPreviousComic(ComicsWebView.this);
						}
						rc = true;
					}
				}
				return rc;
			}
		}
		);
	}
	
	public ComicsWebView(Context context) {
		super(context);
		initDetector();
	}

	public ComicsWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDetector();
	}

	public ComicsWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initDetector();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean superrc = super.onTouchEvent(event);
		boolean gesturerc = mGestureDetect.onTouchEvent(event);
		
		return superrc || gesturerc;
	}

}
