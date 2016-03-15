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

import android.support.annotation.NonNull;

import com.robandjen.comicsapp.data.ComicAndCategory;
import com.robandjen.comicsapp.data.Comics;
import com.robandjen.comicsapp.data.ComicsElement;
import com.robandjen.comicsapp.data.IComicsRepository;

import java.util.List;

import rx.Observable;
import rx.subjects.ReplaySubject;

public class ComicsNavigator implements IComicsNavigator {

    List<ComicAndCategory> comicsAsList;
    private Comics comics;
    private int curIndex = -1;
    private boolean skipOther = true;
    private ReplaySubject<ComicAndCategory> replaySubject = ReplaySubject.createWithSize(1);

    public ComicsNavigator(@NonNull IComicsRepository comicsRepository) {
        comicsRepository.getComicsObservable()
                .subscribeOn(AppSchedulers.io())
                .observeOn(AppSchedulers.mainThread())
                .subscribe(this::updateComics);
    }

    private boolean checkListSection(int start, int end, int step) {
        for (int next = start; next != end; next += step) {
            ComicAndCategory cur = comicsAsList.get(next);
            if (!skipOther || !cur.getComic().getDefaultSkip()) {
                curIndex = next;
                replaySubject.onNext(cur);
                return true;
            }
        }
        return false;
    }

    private void step(boolean forward) {
        if (curIndex == -1) return;

        final int step = forward ? 1 : -1;
        int start = curIndex + step;
        int end = forward ? comicsAsList.size() : -1;
        if (checkListSection(start, end, step)) return;

        //Couldn't find it, wrap around
        start = forward ? 0 : comicsAsList.size() - 1;
        end = curIndex;
        checkListSection(start, end, step);
    }

    private void updateComics(Comics comics) {
        comicsAsList = Observable.from(comics.getCategories())
                .flatMap(category ->
                                Observable.<ComicsElement>from(category.getComics()).map(comic ->
                                                new ComicAndCategory(comic, category)
                                )
                ).toList().toBlocking().single();
        checkListSection(0, comicsAsList.size(), 1);
        this.comics = comics;
    }

    @Override
    public void next() {
        step(true);
    }

    @Override
    public void previous() {
        step(false);
    }

    @Override
    @NonNull
    public Observable<ComicAndCategory> getObservable() {
        return replaySubject.asObservable();
    }

    @Override
    public Comics getComics() {
        return comics;
    }

    @Override
    public boolean getSkipOther() {
        return skipOther;
    }

    @Override
    public void setSkipOther(boolean skipOther) {
        this.skipOther = skipOther;
    }
}
