// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.crash;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import static org.monyhar.chrome.browser.crash.LogcatExtractionRunnable.BEGIN_MICRODUMP;
import static org.monyhar.chrome.browser.crash.LogcatExtractionRunnable.END_MICRODUMP;
import static org.monyhar.chrome.browser.crash.LogcatExtractionRunnable.SNIPPED_MICRODUMP;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import org.monyhar.base.test.BaseRobolectricTestRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * junit tests for {@link LogcatExtractionRunnable}.
 */
@RunWith(BaseRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LogcatExtractionRunnableUnitTest {
    private static final int MAX_LINES = 5;

    @Test
    public void testLogTagNotElided() {
        List<String> original = Arrays.asList(new String[] {"I/cr_FooBar(123): Some message"});
        assertEquals(original, LogcatExtractionRunnable.elideLogcat(original));
    }

    @Test
    public void testLogcatEmpty() {
        final List<String> original = new LinkedList<>();
        assertLogcatLists(original, original);
    }

    @Test
    public void testLogcatWithoutBeginOrEnd_smallLogcat() {
        final List<String> original =
                Arrays.asList("Line 1", "Line 2", "Line 3", "Line 4", "Line 5");
        assertLogcatLists(original, original);
    }

    @Test
    public void testLogcatWithoutBeginOrEnd_largeLogcat() {
        final List<String> original = Arrays.asList("Trimmed Line 1", "Trimmed Line 2", "Line 3",
                "Line 4", "Line 5", "Line 6", "Line 7");
        final List<String> expected =
                Arrays.asList("Line 3", "Line 4", "Line 5", "Line 6", "Line 7");
        assertLogcatLists(expected, original);
    }

    @Test
    public void testLogcatBeginsWithBegin() {
        final List<String> original = Arrays.asList(BEGIN_MICRODUMP, "a", "b", "c", "d", "e");
        final List<String> expected = Arrays.asList(SNIPPED_MICRODUMP);
        assertLogcatLists(expected, original);
    }

    @Test
    public void testLogcatWithBegin() {
        final List<String> original =
                Arrays.asList("Line 1", "Line 2", BEGIN_MICRODUMP, "a", "b", "c", "d", "e");
        final List<String> expected = Arrays.asList("Line 1", "Line 2", SNIPPED_MICRODUMP);
        assertLogcatLists(expected, original);
    }

    @Test
    public void testLogcatWithEnd() {
        final List<String> original = Arrays.asList("Line 1", "Line 2", END_MICRODUMP);
        assertLogcatLists(original, original);
    }

    @Test
    public void testLogcatWithBeginAndEnd_smallLogcat() {
        final List<String> original = Arrays.asList(
                "Line 1", "Line 2", BEGIN_MICRODUMP, "a", "b", "c", "d", "e", END_MICRODUMP);
        final List<String> expected = Arrays.asList("Line 1", "Line 2", SNIPPED_MICRODUMP);
        assertLogcatLists(expected, original);
    }

    @Test
    public void testLogcatWithBeginAndEnd_splitLogcat() {
        final List<String> original = Arrays.asList("Line 1", "Line 2", BEGIN_MICRODUMP, "a", "b",
                "c", "d", "e", END_MICRODUMP, "Trimmed Line 3", "Trimmed Line 4");
        final List<String> expected = Arrays.asList("Line 1", "Line 2", SNIPPED_MICRODUMP);
        assertLogcatLists(expected, original);
    }

    @Test
    public void testLogcatWithBeginAndEnd_largeLogcat() {
        final List<String> original = Arrays.asList("Trimmed Line 1", "Trimmed Line 2", "Line 3",
                "Line 4", "Line 5", "Line 6", BEGIN_MICRODUMP, "a", "b", "c", "d", "e",
                END_MICRODUMP, "Trimmed Line 7", "Trimmed Line 8");
        final List<String> expected =
                Arrays.asList("Line 3", "Line 4", "Line 5", "Line 6", SNIPPED_MICRODUMP);
        assertLogcatLists(expected, original);
    }

    @Test
    public void testLogcatWithEndAndBegin_smallLogcat() {
        final List<String> original = Arrays.asList(
                END_MICRODUMP, "Line 1", "Line 2", BEGIN_MICRODUMP, "a", "b", "c", "d", "e");
        final List<String> expected =
                Arrays.asList(END_MICRODUMP, "Line 1", "Line 2", SNIPPED_MICRODUMP);
        assertLogcatLists(expected, original);
    }

    @Test
    public void testLogcatWithEndAndBegin_largeLogcat() {
        final List<String> original =
                Arrays.asList(END_MICRODUMP, "Line 1", "Line 2", BEGIN_MICRODUMP, "a", "b", "c",
                        "d", "e", END_MICRODUMP, "Trimmed Line 3", "Trimmed Line 4");
        final List<String> expected =
                Arrays.asList(END_MICRODUMP, "Line 1", "Line 2", SNIPPED_MICRODUMP);
        assertLogcatLists(expected, original);
    }

    private void assertLogcatLists(List<String> expected, List<String> original) {
        // trimLogcat() expects a modifiable list as input.
        LinkedList<String> rawLogcat = new LinkedList<String>(original);
        List<String> actualLogcat = LogcatExtractionRunnable.trimLogcat(rawLogcat, MAX_LINES);
        assertArrayEquals(expected.toArray(), actualLogcat.toArray());
    }
}
