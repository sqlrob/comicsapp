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

import android.support.annotation.NonNull;

public class ComicAndCategory {
    private final Category category;
    private final ComicsElement comic;

    public ComicAndCategory(@NonNull ComicsElement comic, @NonNull Category category) {
        this.category = category;
        this.comic = comic;
    }

    public @NonNull Category getCategory() {
        return category;
    }

    public @NonNull ComicsElement getComic() {
        return comic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComicAndCategory that = (ComicAndCategory) o;

        if (!category.equals(that.category)) return false;
        return comic.equals(that.comic);

    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + comic.hashCode();
        return result;
    }
}
