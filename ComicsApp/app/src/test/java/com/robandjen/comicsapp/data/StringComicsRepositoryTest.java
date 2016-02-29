/*
 *  Copyright 2016, Robert Myers
 *
 *
 *  * This file is part of Comics App.
 *     Comics App is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     Comics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with Comics App.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.robandjen.comicsapp.data;


import org.junit.Test;

import java.net.URL;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

public class StringComicsRepositoryTest {
    @Test
    public void null_results_in_errored_subscription() {
        StringComicsRepository repo = new StringComicsRepository(null);
        TestSubscriber<Comics> subscriber = new TestSubscriber<>();

        Observable<Comics> comicsObservable = repo.getComicsObservable();
        comicsObservable.subscribe(subscriber);

        subscriber.assertError(NullPointerException.class);
    }

    @Test
    public void empty_string_results_in_errored_subscription() {
        StringComicsRepository repo = new StringComicsRepository("");
        TestSubscriber<Comics> subscriber = new TestSubscriber<>();

        Observable<Comics> comicsObservable = repo.getComicsObservable();
        comicsObservable.subscribe(subscriber);

        subscriber.assertError(XMLStreamException.class);
    }

    @Test
    public void bad_xml_results_in_errored_subscription() {
        StringComicsRepository repo = new StringComicsRepository("This isn't XML");
        TestSubscriber<Comics> subscriber = new TestSubscriber<>();

        Observable<Comics> comicsObservable = repo.getComicsObservable();
        comicsObservable.subscribe(subscriber);

        subscriber.assertError(XMLStreamException.class);
    }

    @Test
    public void incorrect_xml_results_in_no_categories() {
        StringComicsRepository repo = new StringComicsRepository("<notcomicsxml></notcomicsxml>");
        TestSubscriber<Comics> subscriber = new TestSubscriber<>();

        Observable<Comics> comicsObservable = repo.getComicsObservable();
        comicsObservable.subscribe(subscriber);

        List<Comics> comics = subscriber.getOnNextEvents();
        assertThat(comics).hasSize(1);
        assertThat(comics.get(0).getCategories()).isNullOrEmpty();
    }

    @Test
    public void proper_xml_supplies_object() throws Exception {
        StringComicsRepository repo = new StringComicsRepository("<ComicsPage><Comics><Category name=\"first\">" +
                "<Comic href=\"http://xkcd.com/\" source=\"Other\">XKCD</Comic>" +
                "</Category></Comics></ComicsPage>");

        TestSubscriber<Comics> subscriber = new TestSubscriber<>();

        Observable<Comics> comicsObservable = repo.getComicsObservable();
        comicsObservable.subscribe(subscriber);

        List<Comics> comicsList = subscriber.getOnNextEvents();
        assertThat(comicsList).hasSize(1);
        Comics comics = comicsList.get(0);
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
}
