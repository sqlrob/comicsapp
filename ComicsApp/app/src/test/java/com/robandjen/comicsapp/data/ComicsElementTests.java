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

import static org.assertj.core.api.Assertions.assertThat;


public class ComicsElementTests {
    @Test
    public void equal_defaults_compares_equal() throws Exception {
        DefaultComicsElement first =
                new DefaultComicsElement(new URL("http://www.xkcd.com"), "web", "XKCD");
        DefaultComicsElement second =
                new DefaultComicsElement(new URL("http://www.xkcd.com"), "web", "XKCD");

        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    public void equal_skipped_compares_equal() throws Exception {
        SkippedComicsElement first =
                new SkippedComicsElement(new URL("http://www.xkcd.com"), "web", "XKCD");
        SkippedComicsElement second =
                new SkippedComicsElement(new URL("http://www.xkcd.com"), "web", "XKCD");

        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    public void similar_default_and_skipped_dont_compare_equals() throws Exception {
        SkippedComicsElement first =
                new SkippedComicsElement(new URL("http://www.xkcd.com"), "web", "XKCD");
        DefaultComicsElement second =
                new DefaultComicsElement(new URL("http://www.xkcd.com"), "web", "XKCD");

        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
        assertThat(first.hashCode()).isNotEqualTo(second.hashCode());
    }
}
