// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.test.util.browser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * We use this annotation to tell where WPR archive file to load from for each
 * test cases. Typically the folder should be called wpr_tests.
 * Note the archive is a relative path from src/.
 *
 * New tests should also annotate with "WPRRecordReplayTest" in @Feature.
 *
 * For instance, if file_foo is used in test A, file_bar is used
 * in test B.
 *
 *     @Feature("WPRRecordReplayTest")
 *     @WPRArchiveDirectory("/path_of_file_foo")
 *     public void test_A() {
 *        // Write the test case here.
 *     }
 *
 *     @Feature("WPRRecordReplayTest")
 *     @WPRArchiveDirectory("/path_of_file_bar")
 *     public void test_B() {
 *        // Write the test case here.
 *     }
 *
 * During gClient runhooks, the files in /path_of_file_foo and /path_of_file_bar
 * are downloaded from GCS. Once the files are downloaded, it will be used in
 * tests as isolated.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WPRArchiveDirectory {
    /**
     * @return one WPRArchiveDirectory.
     */
    public String value();
}
