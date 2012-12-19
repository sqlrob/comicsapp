package com.robandjen.comicsapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ComicsParser {
	
	public static List<ComicsEntry> parse(XmlPullParser parser) throws XmlPullParserException,IOException {
		
		parser.next();
		parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, "ComicsPage");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, "Comics");
		
		ArrayList<ComicsEntry> curlist = new ArrayList<ComicsEntry>();
		String curCategory = null;
	
		int eventId = parser.nextTag();
		while (eventId != XmlPullParser.END_DOCUMENT) {
			if (eventId == XmlPullParser.START_TAG) {
				if (parser.getName().equals("Category")) {
					curCategory = parser.getAttributeValue(null, "name");
				}
				else if (parser.getName().equals("Comic")) {
					String curSource = parser.getAttributeValue(null, "source");
					String curHref = parser.getAttributeValue(null,"href");
					curlist.add(new ComicsEntry(curCategory,curSource,parser.nextText(),curHref));
				}
				else if (parser.getName().equals("OtherComic")) {
					parser.nextText();
				}
			}
			else if (eventId == XmlPullParser.END_TAG && parser.getName().equals("Comics")) {
				break;
			}
			
			parser.nextTag();
		}

		return curlist;
	}
}
