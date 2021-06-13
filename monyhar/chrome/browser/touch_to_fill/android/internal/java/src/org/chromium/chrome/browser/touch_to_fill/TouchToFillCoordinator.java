// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.touch_to_fill;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.touch_to_fill.data.Credential;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetController;
import org.monyhar.components.favicon.LargeIconBridge;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.modelutil.PropertyModelChangeProcessor;
import org.monyhar.url.GURL;

import java.util.List;

/**
 * Creates the TouchToFill component. TouchToFill uses a bottom sheet to let the
 * user select a set of credentials and fills it into the focused form.
 */
public class TouchToFillCoordinator implements TouchToFillComponent {
    private final TouchToFillMediator mMediator = new TouchToFillMediator();
    private final PropertyModel mModel =
            TouchToFillProperties.createDefaultModel(mMediator::onDismissed);

    @Override
    public void initialize(Context context, BottomSheetController sheetController,
            TouchToFillComponent.Delegate delegate) {
        mMediator.initialize(delegate, mModel,
                new LargeIconBridge(Profile.getLastUsedRegularProfile()),
                context.getResources().getDimensionPixelSize(R.dimen.touch_to_fill_favicon_size));
        setUpModelChangeProcessors(mModel, new TouchToFillView(context, sheetController));
    }

    @Override
    public void showCredentials(GURL url, boolean isOriginSecure, List<Credential> credentials) {
        mMediator.showCredentials(url, isOriginSecure, credentials);
    }

    /**
     * Connects the given model with the given view using Model Change Processors.
     * @param model A {@link PropertyModel} built with {@link TouchToFillProperties}.
     * @param view A {@link TouchToFillView}.
     */
    @VisibleForTesting
    static void setUpModelChangeProcessors(PropertyModel model, TouchToFillView view) {
        PropertyModelChangeProcessor.create(
                model, view, TouchToFillViewBinder::bindTouchToFillView);
    }
}
