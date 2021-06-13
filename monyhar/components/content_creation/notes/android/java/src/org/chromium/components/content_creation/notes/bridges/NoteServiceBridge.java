// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.content_creation.notes.bridges;

import org.monyhar.base.Callback;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.components.content_creation.notes.NoteService;
import org.monyhar.components.content_creation.notes.models.NoteTemplate;

import java.util.List;

/**
 * Bridge class in charge of forwarding requests to the native counterpart of
 * the note service bridge.
 */
@JNINamespace("content_creation")
public class NoteServiceBridge implements NoteService {
    private long mNativeNoteServiceBridge;

    @CalledByNative
    private static NoteServiceBridge create(long nativePtr) {
        return new NoteServiceBridge(nativePtr);
    }

    private NoteServiceBridge(long nativePtr) {
        mNativeNoteServiceBridge = nativePtr;
    }

    @CalledByNative
    private void clearNativePtr() {
        mNativeNoteServiceBridge = 0;
    }

    @Override
    public void getTemplates(Callback<List<NoteTemplate>> callback) {
        if (mNativeNoteServiceBridge == 0) return;
        NoteServiceBridgeJni.get().getTemplates(mNativeNoteServiceBridge, this, callback);
    }

    @NativeMethods
    interface Natives {
        void getTemplates(long nativeNoteServiceBridge, NoteServiceBridge caller,
                Callback<List<NoteTemplate>> callback);
    }
}