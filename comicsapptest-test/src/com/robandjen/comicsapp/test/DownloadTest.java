package com.robandjen.comicsapp.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import com.robandjen.comicsapp.DownloadTask;

import android.test.InstrumentationTestCase;

public class DownloadTest extends InstrumentationTestCase {
	CountDownLatch latch;
	int httpcode;
	Exception ex;
	String result;
	
	public void testDownload() throws Throwable {
		latch = new CountDownLatch(1);
		httpcode = -1;
		ex = null;
		result = null;
		result = null;
		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					DownloadTask task = new DownloadTask(new URL("http://www.robandjen.com/comics/comics.xml"), new DownloadTask.DownloadResults()  {
						
						@Override
						public void onDownloadFailed(Exception e) {
							ex = e;
							latch.countDown();
						}
						
						@Override
						public void onDownloadFailed(int code) {
							httpcode = code;
							latch.countDown();
						}
						
						@Override
						public void onDownloadComplete(String results) {
							result = results;
							latch.countDown();
						}
					});
					
					task.execute();
				} catch (MalformedURLException e) {
					fail("Unparseable URL");
				}
				
			}
			
		});
		
		latch.await();
		assertEquals(-1, httpcode);
		assertEquals(null,ex);
		assertTrue(result.length() >= 1);
	}
}
