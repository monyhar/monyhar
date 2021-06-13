// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.autofill_assistant;

import android.content.Context;

import androidx.annotation.NonNull;

import org.monyhar.chrome.browser.ActivityTabProvider;
import org.monyhar.chrome.browser.browser_controls.BrowserControlsStateProvider;
import org.monyhar.chrome.browser.compositor.CompositorViewHolder;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetController;
import org.monyhar.components.module_installer.builder.ModuleInterface;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.ui.base.ActivityKeyboardVisibilityDelegate;
import org.monyhar.ui.base.ApplicationViewportInsetSupplier;

/**
 * Interface between base module and assistant DFM.
 */
@ModuleInterface(module = "autofill_assistant",
        impl = "org.monyhar.chrome.browser.autofill_assistant.AutofillAssistantModuleEntryImpl")
public interface AutofillAssistantModuleEntry {
    /**
     * Creates a concrete {@code AssistantDependencies} object. Its contents are opaque to the
     * outside of the module.
     */
    AssistantDependencies createDependencies(BottomSheetController bottomSheetController,
            BrowserControlsStateProvider browserControls, CompositorViewHolder compositorViewHolder,
            Context context, @NonNull WebContents webContents,
            ActivityKeyboardVisibilityDelegate keyboardVisibilityDelegate,
            ApplicationViewportInsetSupplier bottomInsetProvider,
            ActivityTabProvider activityTabProvider);

    /**
     * Returns a {@link AutofillAssistantActionHandler} instance tied to the activity owning the
     * given bottom sheet, and scrim view.
     *
     * @param context activity context
     * @param bottomSheetController bottom sheet controller instance of the activity
     * @param browserControls provider of browser controls state
     * @param compositorViewHolder compositor view holder of the activity
     * @param activityTabProvider activity tab provider
     */
    AutofillAssistantActionHandler createActionHandler(Context context,
            BottomSheetController bottomSheetController,
            BrowserControlsStateProvider browserControls, CompositorViewHolder compositorViewHolder,
            ActivityTabProvider activityTabProvider);
}
