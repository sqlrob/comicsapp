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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

import java.util.List;

public class Category {
    @Attribute
    private String name;

    @ElementListUnion({
            @ElementList(entry = "Comic", type = DefaultComicsElement.class, inline = true, required = false),
            @ElementList(entry = "OtherComic", type = SkippedComicsElement.class, inline = true, required = false)
    })
    private List<ComicsElement> comics;

    public String getName() {
        return name;
    }

    public List<ComicsElement> getComics() {
        return comics;
    }
}
