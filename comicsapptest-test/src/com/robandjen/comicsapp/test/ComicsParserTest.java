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

package com.robandjen.comicsapp.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.robandjen.comicsapp.ComicsEntry;
import com.robandjen.comicsapp.ComicsParser;
import com.robandjen.comicsapp.R;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class ComicsParserTest extends InstrumentationTestCase {

	XmlPullParser mParser;
	List<ComicsEntry> mList;
	
	@Override
	public void setUp() throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		mParser = factory.newPullParser();
	}

	public void testEmptyParse() throws XmlPullParserException, IOException {
		mParser.setInput(new StringReader(""));
		try {
			mList = ComicsParser.parse(mParser);
			fail();
		}
		catch(XmlPullParserException e) {
			
		}
		assertTrue("List should be empty",mList == null || mList.isEmpty());
	}
	
	public void testSingle() throws XmlPullParserException,IOException {
		mParser.setInput(new StringReader("<ComicsPage><Comics><Category name=\"first\">" +
				"<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" + 
				"</Category></Comics></ComicsPage>"));
		mList = ComicsParser.parse(mParser);
		assertEquals(1,mList.size());
		
		ComicsEntry ce = mList.get(0);
		assertEquals("first",ce.getCategory());
		assertEquals("http://xkcd.com/",ce.getURL());
		assertEquals("XKCD",ce.getName());
		assertEquals("Other",ce.getSource());
	}
	
	public void testMultiple() throws XmlPullParserException,IOException {
		mParser.setInput(new StringReader("<ComicsPage><Comics><Category name=\"first\">" +
				"<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" + 
				"<Comic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</Comic>" +
				"</Category></Comics></ComicsPage>"));
		mList = ComicsParser.parse(mParser);
		assertEquals(2,mList.size());
		
		ComicsEntry ce = mList.get(0);
		assertEquals("first",ce.getCategory());
		assertEquals("http://xkcd.com/",ce.getURL());
		assertEquals("XKCD",ce.getName());
		assertEquals("Other",ce.getSource());
		
		ce = mList.get(1);
		assertEquals("first",ce.getCategory());
		assertEquals("http://www.punchanpie.net/",ce.getURL());
		assertEquals("Punch an' Pie",ce.getName());
		assertEquals("Keenspot",ce.getSource());
	}

	public void testMultipleCategories() throws XmlPullParserException,IOException {
		mParser.setInput(new StringReader("<ComicsPage><Comics><Category name=\"first\">" +
				"<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" + 
				"<Comic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</Comic>" +
				"</Category>" + 
				"<Category name=\"second\">" +
				"<Comic href=\"http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues\" source=\"Washington Post\">Baby Blues</Comic>"+
				"</Category>" +
				"</Comics></ComicsPage>"));
		mList = ComicsParser.parse(mParser);
		assertEquals(3,mList.size());
		
		ComicsEntry ce = mList.get(0);
		assertEquals("first",ce.getCategory());
		assertEquals("http://xkcd.com/",ce.getURL());
		assertEquals("XKCD",ce.getName());
		assertEquals("Other",ce.getSource());
		
		ce = mList.get(1);
		assertEquals("first",ce.getCategory());
		assertEquals("http://www.punchanpie.net/",ce.getURL());
		assertEquals("Punch an' Pie",ce.getName());
		assertEquals("Keenspot",ce.getSource());
		
		ce = mList.get(2);
		assertEquals("second",ce.getCategory());
		assertEquals("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues",ce.getURL());
		assertEquals("Baby Blues",ce.getName());
		assertEquals("Washington Post",ce.getSource());
	}
	
	public void testOther() throws XmlPullParserException,IOException {
		mParser.setInput(new StringReader("<ComicsPage><Comics><Category name=\"first\">" +
				"<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" + 
				"<OtherComic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</OtherComic>" +
				"<Comic href=\"http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues\" source=\"Washington Post\">Baby Blues</Comic>" +
				"</Category></Comics></ComicsPage>"));
		mList = ComicsParser.parse(mParser);
		assertEquals(2,mList.size());
		
		ComicsEntry ce = mList.get(0);
		assertEquals("first",ce.getCategory());
		assertEquals("http://xkcd.com/",ce.getURL());
		assertEquals("XKCD",ce.getName());
		assertEquals("Other",ce.getSource());
		
		ce = mList.get(1);
		assertEquals("first",ce.getCategory());
		assertEquals("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues",ce.getURL());
		assertEquals("Baby Blues",ce.getName());
		assertEquals("Washington Post",ce.getSource());
	}

	public void testDisclaimer() throws XmlPullParserException,IOException {
		mParser.setInput(new StringReader("<ComicsPage><Comics><Category name=\"first\">" +
				"<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" + 
				"</Category></Comics>" +
				"<Disclaimer>" +
				"This sidebar brought to you by" +
				"<a href=\"http://www.robandjen.com/rob\" target=\"_content\">Rob</a> and " +
				"<a href=\"http://www.robandjen.com\" target=\"_content\">Rob &amp; Jen's Place</a><br />" +
				"Updated 2:03 PM 8/15/2012" +
				"</Disclaimer>" +
				"</ComicsPage>"));
		mList = ComicsParser.parse(mParser);
		assertEquals(1,mList.size());
		
		ComicsEntry ce = mList.get(0);
		assertEquals("first",ce.getCategory());
		assertEquals("http://xkcd.com/",ce.getURL());
		assertEquals("XKCD",ce.getName());
		assertEquals("Other",ce.getSource());
	}
	
	public void testDefaultXml() throws XmlPullParserException,IOException
	{
		InputStream is = null;
		Context ctx = getInstrumentation().getTargetContext();
		try {
			is = ctx.getAssets().open("comics.xml");
			mParser.setInput(new BufferedInputStream(is), null);
			mList = ComicsParser.parse(mParser);
		}
		finally {
			if (is != null) {
				is.close();
			}
		}
		
		assertTrue(!mList.isEmpty());
	}

	public void testRawXml() throws XmlPullParserException, IOException {
		Context ctx = getInstrumentation().getContext();
		
		InputStream is = null;
		try {
			is = ctx.getAssets().open("comics-test.xml");
			mParser.setInput(new BufferedInputStream(is), null);
			mList = ComicsParser.parse(mParser);
		}
		finally {
			if (is != null) {
				is.close();
			}
		}
		
		assertTrue(!mList.isEmpty());
	}
}
