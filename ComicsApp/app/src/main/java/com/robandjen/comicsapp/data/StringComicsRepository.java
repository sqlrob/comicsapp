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

import android.support.annotation.NonNull;

import org.simpleframework.xml.core.Persister;

import rx.Observable;
import rx.functions.Func0;

public class StringComicsRepository implements IComicsRepository {

    private final String rawXml;

    public StringComicsRepository(@NonNull String rawXml)
    {
        this.rawXml = rawXml;
    }

    //TODO: Get retrolambda working
    @Override
    public Observable<Comics> getComicsObservable() {
        return Observable.defer(new Func0<Observable<Comics>>() {
            @Override
            public Observable<Comics> call() {
                try
                {
                    Persister persister = new Persister();
                    return Observable.just(persister.read(Comics.class,rawXml));
                }
                catch(Exception e)
                {
                    return Observable.error(e);
                }
            }
        });
    }
}
