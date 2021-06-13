// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;

/**
 * Triggered when Android's locale changes.
 */
@JNINamespace("weblayer::i18n")
public class LocaleChangedBroadcastReceiver extends BroadcastReceiver {
    private final Context mContext;

    public LocaleChangedBroadcastReceiver(Context context) {
        mContext = context;
        mContext.registerReceiver(this, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
    }

    public void destroy() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) return;
        LocaleChangedBroadcastReceiverJni.get().localeChanged();
        WebLayerNotificationChannels.onLocaleChanged();
    }

    @NativeMethods
    interface Natives {
        void localeChanged();
    }
}
