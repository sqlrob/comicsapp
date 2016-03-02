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

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTests {
    @Test
    public void identity_handles_null_list() {
        Category first = new Category("category", null);
        Category second = new Category("category", null);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }
}