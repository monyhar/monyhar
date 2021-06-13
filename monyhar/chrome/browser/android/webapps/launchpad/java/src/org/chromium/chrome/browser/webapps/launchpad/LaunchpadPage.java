// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps.launchpad;

import android.app.Activity;

import androidx.annotation.VisibleForTesting;

import org.monyhar.base.supplier.Supplier;
import org.monyhar.chrome.browser.ui.native_page.BasicNativePage;
import org.monyhar.chrome.browser.ui.native_page.NativePageHost;
import org.monyhar.components.browser_ui.settings.SettingsLauncher;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.ui.modaldialog.ModalDialogManager;

import java.util.List;

/**
 * Native page for launching WebApks.
 */
public class LaunchpadPage extends BasicNativePage {
    private LaunchpadCoordinator mLaunchpadCoordinator;
    private String mTitle;

    /**
     * Create a new instance of the app launcher page.
     * @param activity The activity to get context and launch apps.
     * @param host A NativePageHost to load URLs.
     * @param items The list of LaunchpadItems to be displayed.
     */
    public LaunchpadPage(Activity activity, NativePageHost host,
            Supplier<ModalDialogManager> modalDialogManagerSupplier,
            SettingsLauncher settingsLauncher, List<LaunchpadItem> items) {
        super(host);

        mTitle = host.getContext().getResources().getString(R.string.launchpad_title);
        mLaunchpadCoordinator = new LaunchpadCoordinator(activity, modalDialogManagerSupplier,
                settingsLauncher, items, false /* isSeparateActivity */);

        initWithView(mLaunchpadCoordinator.getView());
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getHost() {
        return UrlConstants.LAUNCHPAD_HOST;
    }

    @Override
    public void destroy() {
        mLaunchpadCoordinator.destroy();
        mLaunchpadCoordinator = null;
        super.destroy();
    }

    @VisibleForTesting
    LaunchpadCoordinator getCoordinatorForTesting() {
        return mLaunchpadCoordinator;
    }
}
