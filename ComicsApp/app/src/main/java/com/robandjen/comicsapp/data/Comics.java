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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ComicsPage", strict = false)
public class Comics {
    @ElementList(inline = true, entry = "Category")
    @Path("Comics")
    private List<Category> categories;

    private Comics() {
        //For Simple XML
    }

    public Comics(@NonNull List<Category> categories) {
        this.categories = categories;
    }

    @NonNull public List<Category> getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comics comics = (Comics) o;

        return categories.equals(comics.categories);

    }

    @Override
    public int hashCode() {
        return categories.hashCode();
    }
}
