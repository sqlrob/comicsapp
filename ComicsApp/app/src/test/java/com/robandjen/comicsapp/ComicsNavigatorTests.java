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

package com.robandjen.comicsapp;

import com.robandjen.comicsapp.data.Category;
import com.robandjen.comicsapp.data.ComicAndCategory;
import com.robandjen.comicsapp.data.Comics;
import com.robandjen.comicsapp.data.ComicsElement;
import com.robandjen.comicsapp.data.DefaultComicsElement;
import com.robandjen.comicsapp.data.IComicsRepository;
import com.robandjen.comicsapp.data.SkippedComicsElement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComicsNavigatorTests {
    @Rule
    public ExternalResource scheduler = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            AppSchedulers.setProvider(new AppSchedulers.SchedulerProvider() {
                @Override
                public Scheduler mainThread() {
                    return Schedulers.immediate();
                }

                @Override
                public Scheduler io() {
                    return Schedulers.immediate();
                }

                @Override
                public Scheduler newThread() {
                    return Schedulers.immediate();
                }
            });
            super.before();
        }

        @Override
        protected void after() {
            super.after();
            AppSchedulers.resetProvider();
        }
    };

    @Test
    public void first_element_is_observed() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        Category category = new Category("category", Collections.singletonList(comic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);

        subscriber.assertValue(new ComicAndCategory(comic, category));
    }

    @Test
    public void next_goes_to_next_element() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        Category category = new Category("category", Arrays.asList(comic, nextComic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.next();

        subscriber.assertValues(new ComicAndCategory(comic, category), new ComicAndCategory(nextComic, category));
    }

    @Test
    public void next_wraps_around() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        Category category = new Category("category", Arrays.asList(comic, nextComic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.next();
        cut.next();

        subscriber.assertValues(new ComicAndCategory(comic, category),
                new ComicAndCategory(nextComic, category),
                new ComicAndCategory(comic, category));

    }

    @Test
    public void next_does_nothing_when_one_element() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        Category category = new Category("category", Collections.singletonList(comic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.next();
        subscriber.assertValue(new ComicAndCategory(comic, category));

    }

    @Test
    public void next_skips_other_by_default() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        ComicsElement skippedComic = new SkippedComicsElement(new URL("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues"), "Washington Post", "Baby Blues");
        Category category = new Category("category", Arrays.asList(comic, skippedComic, nextComic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.next();
        cut.next();

        subscriber.assertValues(new ComicAndCategory(comic, category),
                new ComicAndCategory(nextComic, category),
                new ComicAndCategory(comic, category));
        assertThat(cut.getSkipOther()).isTrue();

    }

    @Test
    public void next_doesnt_skip_other_if_option_set() throws Exception {

        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        ComicsElement skippedComic = new SkippedComicsElement(new URL("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues"), "Washington Post", "Baby Blues");
        Category category = new Category("category", Arrays.asList(comic, skippedComic, nextComic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.setSkipOther(false);
        cut.next();

        subscriber.assertValues(new ComicAndCategory(comic, category),
                new ComicAndCategory(skippedComic, category));
        assertThat(cut.getSkipOther()).isFalse();
    }

    @Test
    public void next_crosses_categories() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        Category category = new Category("first", Collections.singletonList(comic));
        Category nextCategory = new Category("second", Collections.singletonList(nextComic));
        Comics comics = new Comics(Arrays.asList(category, nextCategory));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.next();

        subscriber.assertValues(new ComicAndCategory(comic, category), new ComicAndCategory(nextComic, nextCategory));

    }

    @Test
    public void previous_is_inverse_of_next() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        ComicsElement lastComic = new DefaultComicsElement(new URL("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues"), "Washington Post", "Baby Blues");
        Category category = new Category("category", Arrays.asList(comic, nextComic, lastComic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.next();
        cut.previous();

        subscriber.assertValues(new ComicAndCategory(comic, category),
                new ComicAndCategory(nextComic, category),
                new ComicAndCategory(comic, category));

    }

    @Test
    public void previous_wraps_around() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        ComicsElement lastComic = new DefaultComicsElement(new URL("http://www.washingtonpost.com/wp-srv/artsandliving/comics/king.html?name=Baby_Blues"), "Washington Post", "Baby Blues");
        Category category = new Category("category", Arrays.asList(comic, nextComic, lastComic));
        Comics comics = new Comics(Collections.singletonList(category));
        IComicsRepository mockRepo = mock(IComicsRepository.class);
        when(mockRepo.getComicsObservable()).thenReturn(Observable.just(comics));
        ComicsNavigator cut = new ComicsNavigator(mockRepo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();

        cut.getObservable().subscribe(subscriber);
        cut.previous();

        subscriber.assertValues(new ComicAndCategory(comic, category),
                new ComicAndCategory(lastComic, category));

    }

    @Test
    public void repo_updates_propagated() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://xkcd.com"), "Other", "XKCD");
        Category category = new Category("first", Collections.singletonList(comic));
        Comics firstComics = new Comics(Collections.singletonList(category));
        ComicsElement nextComic = new DefaultComicsElement(new URL("http://www.punchanpie.net/"), "Keenspot", "Punch an' Pie");
        Category nextCategory = new Category("second", Collections.singletonList(nextComic));
        Comics nextComics = new Comics(Collections.singletonList(nextCategory));
        ReplaySubject<Comics> subject = ReplaySubject.<Comics>create();
        IComicsRepository repo = mock(IComicsRepository.class);
        when(repo.getComicsObservable()).thenReturn(subject.asObservable());
        ComicsNavigator cut = new ComicsNavigator(repo);
        TestSubscriber<ComicAndCategory> subscriber = new TestSubscriber<>();
        cut.getObservable().subscribe(subscriber);
        subject.onNext(firstComics);

        subject.onNext(nextComics);

        assertThat(cut.getComics()).isEqualTo(nextComics);
        subscriber.assertValues(new ComicAndCategory(comic, category), new ComicAndCategory(nextComic, nextCategory));

    }
}
