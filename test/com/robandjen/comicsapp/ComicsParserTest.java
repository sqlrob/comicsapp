package com.robandjen.comicsapp;

import static org.junit.Assert.*;

import java.io.FileReader;
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

	@Test
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
	
	@Test
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

	@Test
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
	
	@Test
	public void testDefaultXml() throws XmlPullParserException,IOException
	{
		FileReader fr = new FileReader("res/xml/comics.xml");
		mParser.setInput(fr);
		mList = ComicsParser.parse(mParser);
		assertTrue(!mList.isEmpty());
	}
	
}
