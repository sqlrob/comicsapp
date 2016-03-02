/* Copyright 2012, Robert Myers */

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
import org.simpleframework.xml.Text;

import java.net.URL;

public abstract class ComicsElement {

    @Attribute(name = "href")
    private URL url;

    @Attribute(name = "source")
    private String source;

    @Text
    private String name;

    public URL getHref() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public abstract Boolean getDefaultSkip();

    protected ComicsElement() {
        //Do Nothing, used by SimpleXml
    }

    public ComicsElement(URL url, String source, String name) {
        this.url = url;
        this.source = source;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComicsElement)) return false;

        ComicsElement that = (ComicsElement) o;

        if (!url.equals(that.url)) return false;
        if (!getSource().equals(that.getSource())) return false;
        if (!getName().equals(that.getName())) return false;
        return getDefaultSkip().equals(that.getDefaultSkip());
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + getSource().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getDefaultSkip() ? 0 : 1);
        return result;
    }
}
