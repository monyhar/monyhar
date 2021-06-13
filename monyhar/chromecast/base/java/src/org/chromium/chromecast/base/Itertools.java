// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.base;

import java.util.Iterator;

/**
 * Utility methods for working with Iterables and Iterators.
 */
public class Itertools {
    /**
     * Create an Iterable from an Iterator.
     *
     * Wrap an expression that returns an Iterator with this method to use it in a for-each loop.
     */
    public static <T> Iterable<T> fromIterator(Iterator<T> iterator) {
        return () -> iterator;
    }
}
