// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.content_creation.notes;

import android.graphics.Typeface;

import org.monyhar.components.content_creation.notes.models.NoteTemplate;
import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel.WritableObjectPropertyKey;

class NoteProperties {
    /** The view type used by the recycler view to show the notes. */
    public static final int NOTE_VIEW_TYPE = 1;

    /** The template definition.*/
    static final WritableObjectPropertyKey<Boolean> IS_FIRST = new WritableObjectPropertyKey<>();

    /** The template definition.*/
    static final WritableObjectPropertyKey<NoteTemplate> TEMPLATE =
            new WritableObjectPropertyKey<>();

    /** The Typeface instance that has been loaded for the associated template. */
    static final WritableObjectPropertyKey<Typeface> TYPEFACE = new WritableObjectPropertyKey<>();

    static final PropertyKey[] ALL_KEYS = new PropertyKey[] {IS_FIRST, TEMPLATE, TYPEFACE};
}