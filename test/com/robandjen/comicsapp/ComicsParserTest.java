package com.robandjen.comicsapp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ComicsParserTest {

	XmlPullParser mParser;
	List<ComicsEntry> mList;
	
	@Before
	public void initParser() throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		mParser = factory.newPullParser();
	}

	@Test(expected=XmlPullParserException.class)
	public void testEmptyParse() throws XmlPullParserException, IOException {
		mParser.setInput(new StringReader(""));
		mList = ComicsParser.parse(mParser);
		assertTrue("List should be empty",mList.isEmpty());
	}
	
	@Test
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
	
	@Test
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

}
