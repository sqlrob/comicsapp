/* Copyright 2016, Robert Myers */

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
package com.robandjen.comicsapp.data;

import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.stream.XMLStreamException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class ComicsTest {

    Persister persister;

    @Before
    public void Setup() {
        persister = new Persister();
    }

    @Test
    public void no_data_throws() throws Exception {
        try {
            persister.read(Comics.class, "");
            failBecauseExceptionWasNotThrown(XMLStreamException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(XMLStreamException.class);
        }
    }


    @Test
    public void single_comic_in_single_category() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "</Category></Comics></ComicsPage>");
        DefaultComicsElement expectedComic = new DefaultComicsElement(new URL("http://xkcd.com/"), "Other", "XKCD");
        Category expectedCategory = new Category("first", Collections.singletonList((ComicsElement) expectedComic));
        Comics expected = new Comics(Collections.singletonList(expectedCategory));

        assertThat(comics).isEqualTo(expected);
        assertThat(comics.hashCode()).isEqualTo(expected.hashCode());
    }

    @Test
    public void multiple_comics_in_single_category() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "<Comic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</Comic>" +
                "</Category></Comics></ComicsPage>");

        Comics expected = new Comics(
                Collections.singletonList(new Category("first", Arrays.<ComicsElement>asList(
                        new DefaultComicsElement(new URL("http://xkcd.com/"), "Other", "XKCD"),
                        new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie"))
                ))
        );

        assertThat(comics).isEqualTo(expected);

    }

    @Test
    public void multiple_categories() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "<Comic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</Comic>" +
                "</Category>" +
                "<Category name=\"second\">" +
                "<Comic href=\"http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues\" source=\"Washington Post\">Baby Blues</Comic>" +
                "</Category>" +
                "</Comics></ComicsPage>");

        Comics expected = new Comics(Arrays.asList(
                new Category("first", Arrays.<ComicsElement>asList(
                        new DefaultComicsElement(new URL("http://xkcd.com/"), "Other", "XKCD"),
                        new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie"))
                ),
                new Category("second", Collections.singletonList(
                        (ComicsElement) new DefaultComicsElement(new URL("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues"), "Washington Post", "Baby Blues")
                ))
        ));

        assertThat(comics).isEqualTo(expected);
    }

    @Test
    public void other_comic_is_default_skipped() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "<OtherComic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</OtherComic>" +
                "<Comic href=\"http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues\" source=\"Washington Post\">Baby Blues</Comic>" +
                "</Category></Comics></ComicsPage>");

        Comics expected = new Comics(Collections.singletonList(new Category("first", Arrays.asList(
                new DefaultComicsElement(new URL("http://xkcd.com/"), "Other", "XKCD"),
                new SkippedComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie"),
                new DefaultComicsElement(new URL("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues"), "Washington Post", "Baby Blues")
        ))));

        assertThat(comics).isEqualTo(expected);
    }

    @Test
    public void still_parsed_if_disclaimer_present() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "</Category></Comics>" +
                "<Disclaimer>" +
                "This sidebar brought to you by" +
                "<a href=\"http://www.robandjen.com/rob\" target=\"_content\">Rob</a> and " +
                "<a href=\"http://www.robandjen.com\" target=\"_content\">Rob &amp; Jen's Place</a><br />" +
                "Updated 2:03 PM 8/15/2012" +
                "</Disclaimer>" +
                "</ComicsPage>");

        Comics expected = new Comics(Collections.singletonList(new Category("first", Collections.singletonList(
                (ComicsElement) new DefaultComicsElement(new URL("http://xkcd.com/"), "Other", "XKCD")
        ))));

        assertThat(comics).isEqualTo(expected);
    }

    @Test
    public void empty_categories_are_allowed() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "</Category></Comics></ComicsPage>");

        Comics expected = new Comics(Collections.singletonList(new Category("first", null)));
        assertThat(comics.getCategories()).hasSize(1);

        Category category = comics.getCategories().get(0);
        assertThat(category.getName()).isEqualTo("first");
        assertThat(category.getComics()).isNullOrEmpty();
        assertThat(comics).isEqualTo(expected);
    }

    @Test
    public void parses_actual_xml() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("comics-test.xml");
        Comics comics = persister.read(Comics.class, stream);

        assertThat(comics.getCategories()).isNotEmpty();
    }
}