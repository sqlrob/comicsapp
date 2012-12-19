package com.robandjen.comicsapp;

public class ComicsEntry {
	private String mCategory;
	private String mURL;
	private String mName;
	private String mSource;
	
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
	
	public ComicsEntry(String category,String source,String name,String url) {
		mCategory = category;
		mSource = source;
		mName = name;
		mURL = url;
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
	
}
