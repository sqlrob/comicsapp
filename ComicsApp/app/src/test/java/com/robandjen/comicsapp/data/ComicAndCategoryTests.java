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
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ComicAndCategoryTests {
    @Test
    public void members_are_set() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://www.xkcd.com"), "Other", "XKCD");
        Category category = new Category("first", Collections.singletonList(comic));

        ComicAndCategory cut = new ComicAndCategory(comic, category);

        assertThat(cut.getCategory()).isEqualTo(category);
        assertThat(cut.getComic()).isEqualTo(comic);
    }

    @Test
    public void identity() throws Exception {
        ComicsElement comic = new DefaultComicsElement(new URL("http://www.xkcd.com"), "Other", "XKCD");
        Category category = new Category("first", Collections.singletonList(comic));

        ComicAndCategory first = new ComicAndCategory(comic, category);
        ComicAndCategory second = new ComicAndCategory(comic, category);
        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());

        second = new ComicAndCategory(comic, new Category("second", Collections.singletonList(comic)));
        assertThat(first).isNotEqualTo(second);
        assertThat(first.hashCode()).isNotEqualTo(second.hashCode());

    }
}
