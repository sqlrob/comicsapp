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
