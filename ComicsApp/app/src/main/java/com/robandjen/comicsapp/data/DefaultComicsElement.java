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

import java.net.URL;

public class DefaultComicsElement extends ComicsElement {

    private DefaultComicsElement() {
        super();
    }

    public DefaultComicsElement(@NonNull URL url, @NonNull String source, @NonNull String name) {
        super(url, source, name);
    }

    @Override
    public Boolean getDefaultSkip() {
        return false;
    }
}
