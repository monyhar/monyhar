// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.content_creation.notes;

import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.components.content_creation.notes.NoteService;

/**
 * Basic factory that creates and returns a {@link NoteService} that is
 * attached natively to the given {@link Profile}.
 */
public class NoteServiceFactory {
    /**
     * Used to get access to the note service backend.
     */
    public static NoteService getForProfile(Profile profile) {
        return NoteServiceFactoryJni.get().getForProfile(profile);
    }

    @NativeMethods
    interface Natives {
        NoteService getForProfile(Profile profile);
    }
}