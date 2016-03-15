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

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/* This is based on code from http://blog-gmw.rhcloud.com/2016/01/15/testing-observables-in-android/
 */
public class AppSchedulers {
    static SchedulerProvider instance = new DefaultSchedulerProvider();

    public static Scheduler mainThread() {
        return instance.mainThread();
    }

    public static Scheduler io() {
        return instance.io();
    }

    public static Scheduler newThread() {
        return instance.newThread();
    }

    public static SchedulerProvider setProvider(SchedulerProvider provider) {
        instance = provider;
        return instance;
    }

    public static void resetProvider() {
        instance = new DefaultSchedulerProvider();
    }

    public interface SchedulerProvider {
        Scheduler mainThread();

        Scheduler io();

        Scheduler newThread();
    }

    private static class DefaultSchedulerProvider implements SchedulerProvider {

        @Override
        public Scheduler mainThread() {
            return AndroidSchedulers.mainThread();
        }

        @Override
        public Scheduler io() {
            return Schedulers.io();
        }

        @Override
        public Scheduler newThread() {
            return Schedulers.newThread();
        }
    }
}
