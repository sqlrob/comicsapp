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

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class DownloadFragment extends DialogFragment implements DownloadTask.DownloadResults, DialogInterface.OnClickListener {

	DownloadTask mDownloadTask;

	public static DownloadFragment newInstance(URL url) {
		DownloadFragment frag = new DownloadFragment();
		
		Bundle args = new Bundle();
		args.putString("url", url.toExternalForm());
		frag.setArguments(args);
		return frag;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mDownloadTask = new DownloadTask(new URL(getArguments().getString("url")), this);
			mDownloadTask.execute();
		} catch (MalformedURLException e) {
			onDownloadFailed(e);
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mDownloadTask != null) {
			mDownloadTask.cancel(true);
		}
		super.onCancel(dialog);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog rc = new ProgressDialog(getActivity());
		rc.setIndeterminate(true);
		rc.setCancelable(true);
		rc.setButton(AlertDialog.BUTTON_NEGATIVE,getString(android.R.string.cancel),this);
		rc.setMessage(getString(R.string.in_progress));
		rc.setCanceledOnTouchOutside(false);
		return rc;
	}

	private DownloadTask.DownloadResults getResults() {
		Activity activity = getActivity();
		if (activity == null) {
			return null;
		}
		
		try {
			return (DownloadTask.DownloadResults) activity;
		}
		catch (Exception e) {
			return null;
		}
	}
	@Override
	public void onDownloadComplete(String results) {
		DownloadTask.DownloadResults delegate = getResults();
		if (delegate != null) {
			delegate.onDownloadComplete(results);
		}
		
		dismiss();
	}

	@Override
	public void onDownloadFailed(int code) {
		DownloadTask.DownloadResults delegate = getResults();
		if (delegate != null) {
			delegate.onDownloadFailed(code);
		}
		
		dismiss();
	}

	@Override
	public void onDownloadFailed(Exception e) {
		DownloadTask.DownloadResults delegate = getResults();
		if (delegate != null) {
			delegate.onDownloadFailed(e);
		}
		
		dismiss();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		//Only the cancel button is created
		getDialog().cancel();
		
	}
	
}
