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
