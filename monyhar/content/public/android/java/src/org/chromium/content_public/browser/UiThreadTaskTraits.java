// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content_public.browser;

import org.monyhar.base.task.TaskTraits;
import org.monyhar.content.browser.UiThreadTaskTraitsImpl;

/**
 * Traits for tasks that need to run on the Browser UI thread. Keep in sync with
 * content::BrowserTaskTraitsExtension.
 *
 * NB if you wish to post to the thread pool then use {@link TaskTraits} instead of {@link
 * UiThreadTaskTraits}.
 */
public class UiThreadTaskTraits {
    private UiThreadTaskTraits() {}

    // These are convenience constants for UI thread tasks at different priority levels.
    public static final TaskTraits DEFAULT = UiThreadTaskTraitsImpl.DEFAULT;
    public static final TaskTraits BEST_EFFORT = UiThreadTaskTraitsImpl.BEST_EFFORT;
    public static final TaskTraits USER_VISIBLE = UiThreadTaskTraitsImpl.USER_VISIBLE;
    public static final TaskTraits USER_BLOCKING = UiThreadTaskTraitsImpl.USER_BLOCKING;
    public static final TaskTraits BOOTSTRAP = UiThreadTaskTraitsImpl.BOOTSTRAP;
}
