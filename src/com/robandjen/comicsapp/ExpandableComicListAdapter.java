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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ExpandableComicListAdapter extends BaseExpandableListAdapter {

	Context mContext;
	List<ComicsEntry> mComics;
	
	ExpandableComicListAdapter(Context ctx,List<ComicsEntry> comics) {
		mContext = ctx;
		mComics = comics;
		updateIndex();
	}
	
	static class CategoryIndexEntry {
		String mCategory;
		int mOffset;
		int mSize = 0;
		
		public CategoryIndexEntry(String category,int offset) {
			mCategory = category;
			mOffset = offset;
		}
		
		public void setSize(int size) {
			mSize = size;
		}
		
		public String getCategory() {
			return mCategory;
		}
		
		public int getOffset() {
			return mOffset;
		}
		
		public int getSize() { 
			return mSize;
		}
		
		@Override
		public String toString() {
			return mCategory;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CategoryIndexEntry other = (CategoryIndexEntry) obj;
			if (mCategory == null) {
				if (other.mCategory != null)
					return false;
			} else if (!mCategory.equals(other.mCategory))
				return false;
			if (mOffset != other.mOffset)
				return false;
			if (mSize != other.mSize)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((mCategory == null) ? 0 : mCategory.hashCode());
			result = prime * result + mOffset;
			result = prime * result + mSize;
			return result;
		}
	}
	
	List<CategoryIndexEntry> mCategoryIndex = new ArrayList<CategoryIndexEntry>();
	
	static boolean categoryEquals(String lhs,String rhs) {
		if (lhs != null) {
			return lhs.equals(rhs);
		}
		
		return rhs == null;
	}
	
	void updateIndex() {
		mCategoryIndex.clear();
		if (mComics == null || mComics.size() == 0) {
			return;
		}
		
		String curCategory = mComics.get(0).getCategory();
		CategoryIndexEntry curEntry = new CategoryIndexEntry(curCategory,0);
		int curIdx = 1;
		int size = mComics.size();
		for (curIdx = 1; curIdx < size; ++curIdx) {
			ComicsEntry ce = mComics.get(curIdx);
			String nextCategory = ce.getCategory();
			if (!categoryEquals(curCategory,nextCategory)) {
				curEntry.setSize(curIdx - curEntry.getOffset());
				mCategoryIndex.add(curEntry);
				curCategory = nextCategory;
				curEntry = new CategoryIndexEntry(curCategory,curIdx);
			}
		}
		
		curEntry.setSize(curIdx - curEntry.getOffset());
		mCategoryIndex.add(curEntry);
	}
	
	@Override
	public Object getChild(int groupPos, int childPos) {
		return mComics.get(mCategoryIndex.get(groupPos).getOffset() + childPos);
	}

	@Override
	public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.comic_view, parent,false);
		}
		
		TextView tv = (TextView) convertView;
		tv.setText(getChild(groupPos, childPos).toString());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPos) {
		return mCategoryIndex.get(groupPos).getSize();
	}

	@Override
	public Object getGroup(int groupPos) {
		return mCategoryIndex.get(groupPos);
	}

	@Override
	public int getGroupCount() {
		return mCategoryIndex.size();
	}

	@Override
	public long getGroupId(int groupPos) {
		return groupPos;
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.group_view,parent,false);
		}
		
		TextView tv = (TextView) convertView;
		tv.setText(getGroup(groupPos).toString());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPos, int childPos) {
		return true;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	public int childToComicsPos(int groupPos,int childPos) {
		return mCategoryIndex.get(groupPos).getOffset() + childPos;
	}
	
	public long comicsPosToPackedPos(int comicsPos) {
		int groupPos = 0;
		int childPos = -1;
		
		for (CategoryIndexEntry cie : mCategoryIndex) {
			final int offset = cie.getOffset();
			if (comicsPos >= offset && comicsPos < offset + cie.getSize()) {
				childPos = comicsPos - offset;
				break;
			}
			++groupPos;
		}
		
		if (childPos == -1) {
			throw new IndexOutOfBoundsException();
		}
		
		return ExpandableListView.getPackedPositionForChild(groupPos, childPos);
	}
}
