// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download.home;

import android.app.Activity;
import android.content.Context;

import org.monyhar.base.Callback;
import org.monyhar.base.DiscardableReferencePool;
import org.monyhar.base.supplier.ObservableSupplier;
import org.monyhar.chrome.browser.ui.messages.snackbar.SnackbarManager;
import org.monyhar.components.feature_engagement.Tracker;
import org.monyhar.components.offline_items_collection.OfflineContentProvider;
import org.monyhar.components.prefs.PrefService;
import org.monyhar.ui.modaldialog.ModalDialogManager;

/** Factory class to build a DownloadManagerCoordinator instance. */
public class DownloadManagerCoordinatorFactory {
    private DownloadManagerCoordinatorFactory() {}

    /** Builds a {@link DownloadManagerCoordinatorImpl} instance. */
    public static DownloadManagerCoordinator create(Activity activity,
            DownloadManagerUiConfig config, ObservableSupplier<Boolean> isPrefetchEnabledSupplier,
            Callback<Context> settingsLauncher, SnackbarManager snackbarManager,
            ModalDialogManager modalDialogManager, PrefService prefService, Tracker tracker,
            FaviconProvider faviconProvider, OfflineContentProvider provider,
            LegacyDownloadProvider legacyProvider,
            DiscardableReferencePool discardableReferencePool) {
        return new DownloadManagerCoordinatorImpl(activity, config, isPrefetchEnabledSupplier,
                settingsLauncher, snackbarManager, modalDialogManager, prefService, tracker,
                faviconProvider, provider, legacyProvider, discardableReferencePool);
    }
}