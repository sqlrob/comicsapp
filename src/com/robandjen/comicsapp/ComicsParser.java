/* Copyright 2012, Robert Myers */

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class ComicsParser {
	
	public static List<ComicsEntry> parse(XmlPullParser parser) throws XmlPullParserException,IOException {
		
		while (parser.getEventType() == XmlPullParser.START_DOCUMENT) {
			parser.next();
		}
		
		int tstid = parser.getEventType();
		Log.v("test","Test Id=" + tstid);
		parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, "ComicsPage");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, "Comics");
		
		ArrayList<ComicsEntry> curlist = new ArrayList<ComicsEntry>();
		String curCategory = null;
	
		int eventId = parser.nextTag();
		while (!(eventId == XmlPullParser.END_TAG && parser.getName().equals("Comics"))) {
			if (eventId == XmlPullParser.START_TAG) {
				if (parser.getName().equals("Category")) {
					curCategory = parser.getAttributeValue(null, "name");
				}
				else if (parser.getName().equals("Comic")) {
					String curSource = parser.getAttributeValue(null, "source");
					String curHref = parser.getAttributeValue(null,"href");
					curlist.add(new ComicsEntry(curCategory,curSource,parser.nextText(),curHref));
				}
				else if (parser.getName().equals("OtherComic")) { //Currently Ignored
					parser.nextText();
				}
			}
			eventId = parser.nextTag();
		}

		return curlist;
	}
}
