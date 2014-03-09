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

public class ComicsEntry {
	private String mCategory;
	private String mURL;
	private String mName;
	private String mSource;
	private boolean mOther;
	
	public String getCategory() {
		return mCategory;
	}
	
	public String getURL() {
		return mURL;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getSource() {
		return mSource;
	}
	
	public ComicsEntry(String category,String source,String name,String url, boolean bOther) {
		mCategory = category;
		mSource = source;
		mName = name;
		mURL = url;
		mOther = bOther;
	}

	public boolean getOther() {
		return mOther;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mCategory == null) ? 0 : mCategory.hashCode());
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		result = prime * result + ((mSource == null) ? 0 : mSource.hashCode());
		result = prime * result + ((mURL == null) ? 0 : mURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComicsEntry other = (ComicsEntry) obj;
		if (mCategory == null) {
			if (other.mCategory != null)
				return false;
		} else if (!mCategory.equals(other.mCategory))
			return false;
		if (mName == null) {
			if (other.mName != null)
				return false;
		} else if (!mName.equals(other.mName))
			return false;
		if (mSource == null) {
			if (other.mSource != null)
				return false;
		} else if (!mSource.equals(other.mSource))
			return false;
		if (mURL == null) {
			if (other.mURL != null)
				return false;
		} else if (!mURL.equals(other.mURL))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return mName;
	}
	
}
