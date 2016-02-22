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

        assertThat(comics.getCategories()).hasSize(1);
        Category category = comics.getCategories().get(0);
        assertThat(category.getName()).isEqualTo("first");
        assertThat(category.getComics()).hasSize(1);
        ComicsElement comic = category.getComics().get(0);
        assertThat(comic.getDefaultSkip()).isFalse();
        assertThat(comic.getName()).isEqualTo("XKCD");
        assertThat(comic.getSource()).isEqualTo("Other");
        assertThat(comic.getHref()).isEqualTo(new URL("http://xkcd.com/"));
    }

    @Test
    public void multiple_comics_in_single_category() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "<Comic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</Comic>" +
                "</Category></Comics></ComicsPage>");

        assertThat(comics.getCategories()).hasSize(1);
        Category category = comics.getCategories().get(0);
        assertThat(category.getName()).isEqualTo("first");
        assertThat(category.getComics()).hasSize(2);

        ComicsElement ce = category.getComics().get(0);
        assertThat(ce.getDefaultSkip()).isFalse();
        assertThat(ce.getName()).isEqualTo("XKCD");
        assertThat(ce.getHref()).isEqualTo(new URL("http://xkcd.com/"));
        assertThat(ce.getSource()).isEqualTo("Other");

        ce = category.getComics().get(1);
        assertThat(ce.getDefaultSkip()).isFalse();
        assertThat(ce.getName()).isEqualTo("Punch an' Pie");
        assertThat(ce.getHref()).isEqualTo(new URL("http://www.punchanpie.net/"));
        assertThat(ce.getSource()).isEqualTo("Keenspot");

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

        assertThat(comics.getCategories()).hasSize(2);
        Category category = comics.getCategories().get(0);
        assertThat(category.getComics()).hasSize(2);
        assertThat(category.getName()).isEqualTo("first");

        category = comics.getCategories().get(1);
        assertThat(category.getName()).isEqualTo("second");
        assertThat(category.getComics()).hasSize(1);
    }

    @Test
    public void other_comic_is_default_skipped() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "<OtherComic href=\"http://www.punchanpie.net/\" source=\"Keenspot\">Punch an' Pie</OtherComic>" +
                "<Comic href=\"http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues\" source=\"Washington Post\">Baby Blues</Comic>" +
                "</Category></Comics></ComicsPage>");

        Category category = comics.getCategories().get(0);
        assertThat(category.getComics()).hasSize(3);
        ComicsElement ce = category.getComics().get(0);
        assertThat(ce.getDefaultSkip()).isFalse();
        assertThat(ce.getName()).isEqualTo("XKCD");

        ce = category.getComics().get(1);
        assertThat(ce.getDefaultSkip()).isTrue();
        assertThat(ce.getName()).isEqualTo("Punch an' Pie");

        ce = category.getComics().get(2);
        assertThat(ce.getDefaultSkip()).isFalse();
        assertThat(ce.getName()).isEqualTo("Baby Blues");
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

        assertThat(comics.getCategories()).hasSize(1);
    }

    @Test
    public void empty_categories_are_allowed() throws Exception {
        Comics comics = persister.read(Comics.class, "<ComicsPage><Comics><Category name=\"first\">" +
                "</Category></Comics></ComicsPage>");

        assertThat(comics.getCategories()).hasSize(1);

        Category category = comics.getCategories().get(0);
        assertThat(category.getName()).isEqualTo("first");
        assertThat(category.getComics()).isNullOrEmpty();
    }

    @Test
    public void parses_actual_xml() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("comics-test.xml");
        Comics comics = persister.read(Comics.class, stream);

        assertThat(comics.getCategories()).isNotEmpty();
    }
}