// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download.home;

import org.monyhar.base.Callback;
import org.monyhar.base.ObserverList;
import org.monyhar.base.supplier.ObservableSupplier;
import org.monyhar.chrome.browser.offlinepages.prefetch.PrefetchConfiguration;
import org.monyhar.chrome.browser.preferences.Pref;
import org.monyhar.chrome.browser.preferences.PrefChangeRegistrar;
import org.monyhar.chrome.browser.profiles.ProfileKey;

/**
 * Helper class to determine whether or not the prefetch setting is enabled for Chrome.
 * This class does not require an explicit destroy call, but needs all observers to be
 * unregistered for full clean up.
 */
class PrefetchEnabledSupplier implements ObservableSupplier<Boolean> {
    private final ObserverList<Callback<Boolean>> mObservers = new ObserverList<>();
    private PrefChangeRegistrar mPrefChangeRegistrar;

    // ObservableSupplier implementation.
    @Override
    public Boolean get() {
        return isPrefetchEnabled();
    }

    @Override
    public Boolean addObserver(Callback<Boolean> obs) {
        if (mObservers.isEmpty()) startTrackingPref();

        mObservers.addObserver(obs);

        return isPrefetchEnabled();
    }

    @Override
    public void removeObserver(Callback<Boolean> obs) {
        mObservers.removeObserver(obs);
        if (mObservers.isEmpty()) stopTrackingPref();
    }

    private void startTrackingPref() {
        if (mPrefChangeRegistrar != null) return;
        mPrefChangeRegistrar = new PrefChangeRegistrar();
        mPrefChangeRegistrar.addObserver(Pref.USER_SETTING_ENABLED, this::notifyObservers);
    }

    private void stopTrackingPref() {
        if (mPrefChangeRegistrar == null) return;
        mPrefChangeRegistrar.destroy();
        mPrefChangeRegistrar = null;
    }

    private void notifyObservers() {
        boolean enabled = isPrefetchEnabled();
        for (Callback<Boolean> obs : mObservers) obs.onResult(enabled);
    }

    private static boolean isPrefetchEnabled() {
        // Since this class is a helper that observes the settings changes, it is safe to use always
        // regular profile for PrefetchConfiguration.
        return PrefetchConfiguration.isPrefetchingFlagEnabled()
                && PrefetchConfiguration.isPrefetchingEnabledInSettings(
                        ProfileKey.getLastUsedRegularProfileKey());
    }
}