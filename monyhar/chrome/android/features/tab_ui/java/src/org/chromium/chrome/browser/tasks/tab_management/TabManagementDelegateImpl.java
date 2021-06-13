// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tasks.tab_management;

import static org.monyhar.chrome.browser.tasks.tab_management.TabManagementModuleProvider.SYNTHETIC_TRIAL_POSTFIX;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.monyhar.base.SysUtils;
import org.monyhar.base.annotations.UsedByReflection;
import org.monyhar.base.supplier.ObservableSupplier;
import org.monyhar.base.supplier.OneshotSupplier;
import org.monyhar.base.supplier.OneshotSupplierImpl;
import org.monyhar.base.supplier.Supplier;
import org.monyhar.chrome.browser.browser_controls.BrowserControlsStateProvider;
import org.monyhar.chrome.browser.compositor.layouts.Layout;
import org.monyhar.chrome.browser.compositor.layouts.LayoutRenderHost;
import org.monyhar.chrome.browser.compositor.layouts.LayoutUpdateHost;
import org.monyhar.chrome.browser.compositor.layouts.OverviewModeBehavior;
import org.monyhar.chrome.browser.compositor.layouts.content.TabContentManager;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.fullscreen.BrowserControlsManager;
import org.monyhar.chrome.browser.init.ChromeActivityNativeDelegate;
import org.monyhar.chrome.browser.lifecycle.ActivityLifecycleDispatcher;
import org.monyhar.chrome.browser.metrics.UmaSessionStats;
import org.monyhar.chrome.browser.multiwindow.MultiWindowModeStateDispatcher;
import org.monyhar.chrome.browser.omnibox.OmniboxStub;
import org.monyhar.chrome.browser.share.ShareDelegate;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tabmodel.TabCreatorManager;
import org.monyhar.chrome.browser.tabmodel.TabModel;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;
import org.monyhar.chrome.browser.tasks.TasksSurface;
import org.monyhar.chrome.browser.tasks.TasksSurfaceCoordinator;
import org.monyhar.chrome.browser.tasks.tab_groups.TabGroupModelFilter;
import org.monyhar.chrome.browser.tasks.tab_management.suggestions.TabSuggestions;
import org.monyhar.chrome.browser.tasks.tab_management.suggestions.TabSuggestionsOrchestrator;
import org.monyhar.chrome.browser.theme.ThemeColorProvider;
import org.monyhar.chrome.browser.ui.messages.snackbar.SnackbarManager;
import org.monyhar.chrome.features.start_surface.StartSurface;
import org.monyhar.chrome.features.start_surface.StartSurfaceDelegate;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetController;
import org.monyhar.components.browser_ui.widget.MenuOrKeyboardActionController;
import org.monyhar.components.browser_ui.widget.scrim.ScrimCoordinator;
import org.monyhar.ui.base.WindowAndroid;
import org.monyhar.ui.modaldialog.ModalDialogManager;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.resources.dynamics.DynamicResourceLoader;

/**
 * Impl class that will resolve components for tab management.
 */
@UsedByReflection("TabManagementModule")
public class TabManagementDelegateImpl implements TabManagementDelegate {
    @Override
    public TasksSurface createTasksSurface(@NonNull Activity activity,
            @NonNull ScrimCoordinator scrimCoordinator, @NonNull PropertyModel propertyModel,
            @TabSwitcherType int tabSwitcherType, @NonNull Supplier<Tab> parentTabSupplier,
            boolean hasMVTiles, @NonNull WindowAndroid windowAndroid,
            @NonNull ActivityLifecycleDispatcher activityLifecycleDispatcher,
            @NonNull TabModelSelector tabModelSelector, @NonNull SnackbarManager snackbarManager,
            @NonNull Supplier<DynamicResourceLoader> dynamicResourceLoaderSupplier,
            @NonNull TabContentManager tabContentManager,
            @NonNull ModalDialogManager modalDialogManager,
            @NonNull BrowserControlsStateProvider browserControlsStateProvider,
            @NonNull TabCreatorManager tabCreatorManager,
            @NonNull MenuOrKeyboardActionController menuOrKeyboardActionController,
            @NonNull Supplier<ShareDelegate> shareDelegateSupplier,
            @NonNull MultiWindowModeStateDispatcher multiWindowModeStateDispatcher,
            @NonNull ViewGroup rootView) {
        return new TasksSurfaceCoordinator(activity, scrimCoordinator, propertyModel,
                tabSwitcherType, parentTabSupplier, hasMVTiles, windowAndroid,
                activityLifecycleDispatcher, tabModelSelector, snackbarManager,
                dynamicResourceLoaderSupplier, tabContentManager, modalDialogManager,
                browserControlsStateProvider, tabCreatorManager, menuOrKeyboardActionController,
                shareDelegateSupplier, multiWindowModeStateDispatcher, rootView);
    }

    @Override
    public TabSwitcher createGridTabSwitcher(@NonNull Activity activity,
            @NonNull ActivityLifecycleDispatcher activityLifecycleDispatcher,
            @NonNull TabModelSelector tabModelSelector,
            @NonNull TabContentManager tabContentManager,
            @NonNull BrowserControlsStateProvider browserControlsStateProvider,
            @NonNull TabCreatorManager tabCreatorManager,
            @NonNull MenuOrKeyboardActionController menuOrKeyboardActionController,
            @NonNull ViewGroup containerView,
            @NonNull Supplier<ShareDelegate> shareDelegateSupplier,
            @NonNull MultiWindowModeStateDispatcher multiWindowModeStateDispatcher,
            @NonNull ScrimCoordinator scrimCoordinator, @NonNull ViewGroup rootView) {
        if (UmaSessionStats.isMetricsServiceAvailable()) {
            UmaSessionStats.registerSyntheticFieldTrial(
                    ChromeFeatureList.TAB_GRID_LAYOUT_ANDROID + SYNTHETIC_TRIAL_POSTFIX,
                    "Downloaded_Enabled");
        }

        return new TabSwitcherCoordinator(activity, activityLifecycleDispatcher, tabModelSelector,
                tabContentManager, browserControlsStateProvider, tabCreatorManager,
                menuOrKeyboardActionController, containerView, shareDelegateSupplier,
                multiWindowModeStateDispatcher, scrimCoordinator,
                TabUiFeatureUtilities.isTabGroupsAndroidContinuationEnabled()
                                && SysUtils.isLowEndDevice()
                        ? TabListCoordinator.TabListMode.LIST
                        : TabListCoordinator.TabListMode.GRID,
                rootView);
    }

    @Override
    public TabSwitcher createCarouselTabSwitcher(@NonNull Activity activity,
            @NonNull ActivityLifecycleDispatcher lifecycleDispatcher,
            @NonNull TabModelSelector tabModelSelector,
            @NonNull TabContentManager tabContentManager,
            @NonNull BrowserControlsStateProvider browserControls,
            @NonNull TabCreatorManager tabCreatorManager,
            @NonNull MenuOrKeyboardActionController menuOrKeyboardActionController,
            @NonNull ViewGroup containerView,
            @NonNull Supplier<ShareDelegate> shareDelegateSupplier,
            @NonNull MultiWindowModeStateDispatcher multiWindowModeStateDispatcher,
            @NonNull ScrimCoordinator scrimCoordinator, @NonNull ViewGroup rootView) {
        return new TabSwitcherCoordinator(activity, lifecycleDispatcher, tabModelSelector,
                tabContentManager, browserControls, tabCreatorManager,
                menuOrKeyboardActionController, containerView, shareDelegateSupplier,
                multiWindowModeStateDispatcher, scrimCoordinator,
                TabListCoordinator.TabListMode.CAROUSEL, rootView);
    }

    @Override
    public TabGroupUi createTabGroupUi(@NonNull Activity activity, @NonNull ViewGroup parentView,
            @NonNull ThemeColorProvider themeColorProvider,
            @NonNull ScrimCoordinator scrimCoordinator,
            @NonNull ObservableSupplier<Boolean> omniboxFocusStateSupplier,
            @NonNull BottomSheetController bottomSheetController,
            @NonNull ActivityLifecycleDispatcher activityLifecycleDispatcher,
            @NonNull Supplier<Boolean> isWarmOnResumeSupplier, TabModelSelector tabModelSelector,
            @NonNull TabContentManager tabContentManager, ViewGroup rootView,
            @NonNull Supplier<DynamicResourceLoader> dynamicResourceLoaderSupplier,
            @NonNull TabCreatorManager tabCreatorManager,
            @NonNull Supplier<ShareDelegate> shareDelegateSupplier,
            @NonNull OneshotSupplier<OverviewModeBehavior> overviewModeBehaviorSupplier,
            @NonNull SnackbarManager snackbarManager) {
        return new TabGroupUiCoordinator(activity, parentView, themeColorProvider, scrimCoordinator,
                omniboxFocusStateSupplier, bottomSheetController, activityLifecycleDispatcher,
                isWarmOnResumeSupplier, tabModelSelector, tabContentManager, rootView,
                dynamicResourceLoaderSupplier, tabCreatorManager, shareDelegateSupplier,
                overviewModeBehaviorSupplier, snackbarManager);
    }

    @Override
    public Layout createStartSurfaceLayout(Context context, LayoutUpdateHost updateHost,
            LayoutRenderHost renderHost, StartSurface startSurface) {
        return StartSurfaceDelegate.createStartSurfaceLayout(
                context, updateHost, renderHost, startSurface);
    }

    @Override
    public StartSurface createStartSurface(@NonNull Activity activity,
            @NonNull ScrimCoordinator scrimCoordinator,
            @NonNull BottomSheetController sheetController,
            @NonNull OneshotSupplierImpl<StartSurface> startSurfaceOneshotSupplier,
            @NonNull Supplier<Tab> parentTabSupplier, boolean hadWarmStart,
            @NonNull WindowAndroid windowAndroid, @NonNull ViewGroup containerView,
            @NonNull Supplier<DynamicResourceLoader> dynamicResourceLoaderSupplier,
            @NonNull TabModelSelector tabModelSelector,
            @NonNull BrowserControlsManager browserControlsManager,
            @NonNull SnackbarManager snackbarManager,
            @NonNull Supplier<ShareDelegate> shareDelegateSupplier,
            @NonNull Supplier<OmniboxStub> omniboxStubSupplier,
            @NonNull TabContentManager tabContentManager,
            @NonNull ModalDialogManager modalDialogManager,
            @NonNull ChromeActivityNativeDelegate chromeActivityNativeDelegate,
            @NonNull ActivityLifecycleDispatcher activityLifecycleDispatcher,
            @NonNull TabCreatorManager tabCreatorManager,
            @NonNull MenuOrKeyboardActionController menuOrKeyboardActionController,
            @NonNull MultiWindowModeStateDispatcher multiWindowModeStateDispatcher) {
        return StartSurfaceDelegate.createStartSurface(activity, scrimCoordinator, sheetController,
                startSurfaceOneshotSupplier, parentTabSupplier, hadWarmStart, windowAndroid,
                containerView, dynamicResourceLoaderSupplier, tabModelSelector,
                browserControlsManager, snackbarManager, shareDelegateSupplier, omniboxStubSupplier,
                tabContentManager, modalDialogManager, chromeActivityNativeDelegate,
                activityLifecycleDispatcher, tabCreatorManager, menuOrKeyboardActionController,
                multiWindowModeStateDispatcher);
    }

    @Override
    public TabGroupModelFilter createTabGroupModelFilter(TabModel tabModel) {
        return new TabGroupModelFilter(
                tabModel, TabUiFeatureUtilities.ENABLE_TAB_GROUP_AUTO_CREATION.getValue());
    }

    @Override
    public TabSuggestions createTabSuggestions(@NonNull TabModelSelector tabModelSelector,
            @NonNull ActivityLifecycleDispatcher activityLifecycleDispatcher) {
        return new TabSuggestionsOrchestrator(tabModelSelector, activityLifecycleDispatcher);
    }
}
