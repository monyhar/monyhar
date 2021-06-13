// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.feed.v2;

import androidx.annotation.Nullable;

import org.monyhar.base.Callback;
import org.monyhar.base.ThreadUtils;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.chrome.browser.xsurface.PersistentKeyValueCache;

/**
 * Implementation of xsurface's PersistentKeyValueCache.
 */
@JNINamespace("feed")
public class FeedPersistentKeyValueCache implements PersistentKeyValueCache {
    @Override
    public void lookup(byte[] key, ValueConsumer consumer) {
        assert ThreadUtils.runningOnUiThread();
        FeedPersistentKeyValueCacheJni.get().lookup(key, new Callback<byte[]>() {
            @Override
            public void onResult(byte[] result) {
                consumer.run(result);
            }
        });
    }

    @Override
    public void put(byte[] key, byte[] value, @Nullable Runnable onComplete) {
        assert ThreadUtils.runningOnUiThread();
        FeedPersistentKeyValueCacheJni.get().put(key, value, onComplete);
    }

    @Override
    public void evict(byte[] key, @Nullable Runnable onComplete) {
        assert ThreadUtils.runningOnUiThread();
        FeedPersistentKeyValueCacheJni.get().evict(key, onComplete);
    }

    @NativeMethods
    interface Natives {
        void lookup(byte[] key, Object consumer);
        void put(byte[] key, byte[] value, Runnable onComplete);
        void evict(byte[] key, Runnable onComplete);
    }
}
