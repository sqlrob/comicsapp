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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class URLFragment extends DialogFragment implements DialogInterface.OnClickListener, OnClickListener {
	public interface URLEvent {
		void onURLChosen(URL Url);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflator = getActivity().getLayoutInflater();
		builder.setView(inflator.inflate(R.layout.url_layout, null))
			.setPositiveButton(getString(android.R.string.ok), this)
			.setNegativeButton(getString(android.R.string.cancel),this);
		
		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		AlertDialog ad = (AlertDialog) getDialog();
		Button ok = ad.getButton(Dialog.BUTTON_POSITIVE);
		ok.setOnClickListener(this);
		
		SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences(null, 0);
		String url = prefs.getString("url", "");
		EditText edtUrl = (EditText) ad.findViewById(R.id.editURL);
		edtUrl.setText(url);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_NEGATIVE) {
			dialog.cancel();
		}
	}
	
	public static URLFragment newInstance() {
		return new URLFragment();
	}

	@Override
	public void onClick(View arg0) {
		EditText urledit = (EditText) getDialog().findViewById(R.id.editURL);
		String strurl = urledit.getText().toString();
		try {
			URL url = new URL(strurl);
			URLEvent evt = (URLEvent) getActivity();
			if (evt != null) {
				evt.onURLChosen(url);
				dismiss();
			}
		} catch (MalformedURLException e) {
			Toast.makeText(getActivity(), getString(android.R.string.httpErrorBadUrl), Toast.LENGTH_LONG).show();
		}
		
	}
}
