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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.OperationCanceledException;

public class DownloadTask extends AsyncTask<Void,Void,Void>{

	public interface DownloadResults {
		void onDownloadComplete(String results);
		void onDownloadFailed(int code);
		void onDownloadFailed(Exception e);
	}
	
	URL mUrl;
	
	DownloadResults mResults;
	String mBody = null;
	int mCode = -1;
	Exception mException = null;
	
	public DownloadTask(URL url,DownloadResults results) {
		mResults=results;
		mUrl = url;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		HttpURLConnection conn = null;
		BufferedInputStream in = null;
		try {
			conn = (HttpURLConnection) mUrl.openConnection();
			mCode = conn.getResponseCode();
			in = new BufferedInputStream(conn.getInputStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead;
			do {
				bytesRead = in.read(buffer);
				if (bytesRead > 0) {
					out.write(buffer,0,bytesRead);
				}
				if (isCancelled()) {
					throw new OperationCanceledException();
				}
			}
			while (bytesRead > 0);
			
			mBody = out.toString();
			
		} catch (Exception e) {
			mException = e;
			return null;
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (mResults == null) {
			return;
		}
		
		if (mException != null) {
			mResults.onDownloadFailed(mException);
		} else if (mBody != null) {
			mResults.onDownloadComplete(mBody);
		} else {
			mResults.onDownloadFailed(mCode);
		}
	}
	
	@Override
	protected void onCancelled(Void result) {
		if (mResults == null) {
			return;
		}
		
		mResults.onDownloadFailed(new OperationCanceledException());
	}
}
