// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.customtabs.dependency_injection;

import org.monyhar.chrome.browser.browserservices.trustedwebactivityui.TwaFinishHandler;
import org.monyhar.chrome.browser.browserservices.ui.controller.CurrentPageVerifier;
import org.monyhar.chrome.browser.browserservices.ui.controller.Verifier;
import org.monyhar.chrome.browser.browserservices.ui.splashscreen.SplashController;
import org.monyhar.chrome.browser.browserservices.ui.trustedwebactivity.TrustedWebActivityCoordinator;
import org.monyhar.chrome.browser.customtabs.CustomTabActivityClientConnectionKeeper;
import org.monyhar.chrome.browser.customtabs.CustomTabActivityLifecycleUmaTracker;
import org.monyhar.chrome.browser.customtabs.CustomTabBottomBarDelegate;
import org.monyhar.chrome.browser.customtabs.CustomTabCompositorContentInitializer;
import org.monyhar.chrome.browser.customtabs.CustomTabDelegateFactory;
import org.monyhar.chrome.browser.customtabs.CustomTabIncognitoManager;
import org.monyhar.chrome.browser.customtabs.CustomTabSessionHandler;
import org.monyhar.chrome.browser.customtabs.CustomTabStatusBarColorProvider;
import org.monyhar.chrome.browser.customtabs.CustomTabTabPersistencePolicy;
import org.monyhar.chrome.browser.customtabs.CustomTabTaskDescriptionHelper;
import org.monyhar.chrome.browser.customtabs.ReparentingTaskProvider;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityNavigationController;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityTabController;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityTabFactory;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityTabProvider;
import org.monyhar.chrome.browser.customtabs.content.CustomTabIntentHandler;
import org.monyhar.chrome.browser.customtabs.content.TabObserverRegistrar;
import org.monyhar.chrome.browser.customtabs.features.toolbar.CustomTabToolbarCoordinator;
import org.monyhar.chrome.browser.dependency_injection.ActivityScope;
import org.monyhar.chrome.browser.dependency_injection.ChromeActivityCommonsModule;
import org.monyhar.chrome.browser.dependency_injection.ChromeActivityComponent;
import org.monyhar.chrome.browser.webapps.WebApkActivityCoordinator;
import org.monyhar.chrome.browser.webapps.WebappActivityCoordinator;

import dagger.Subcomponent;

/**
 * Activity-scoped component associated with
 * {@link org.monyhar.chrome.browser.customtabs.CustomTabActivity} and
 * {@link org.monyhar.chrome.browser.webapps.WebappActivity}.
 */
@Subcomponent(modules = {ChromeActivityCommonsModule.class, BaseCustomTabActivityModule.class})
@ActivityScope
public interface BaseCustomTabActivityComponent extends ChromeActivityComponent {
    CurrentPageVerifier resolveCurrentPageVerifier();
    CustomTabActivityClientConnectionKeeper resolveConnectionKeeper();
    CustomTabActivityLifecycleUmaTracker resolveUmaTracker();
    CustomTabActivityNavigationController resolveNavigationController();
    CustomTabActivityTabController resolveTabController();
    CustomTabActivityTabFactory resolveTabFactory();
    CustomTabActivityTabProvider resolveTabProvider();
    CustomTabBottomBarDelegate resolveBottomBarDelegate();
    CustomTabCompositorContentInitializer resolveCompositorContentInitializer();
    CustomTabDelegateFactory resolveTabDelegateFactory();
    CustomTabIncognitoManager resolveCustomTabIncognitoManager();
    CustomTabIntentHandler resolveIntentHandler();
    CustomTabSessionHandler resolveSessionHandler();
    CustomTabStatusBarColorProvider resolveCustomTabStatusBarColorProvider();
    CustomTabTaskDescriptionHelper resolveTaskDescriptionHelper();
    CustomTabToolbarCoordinator resolveToolbarCoordinator();
    TabObserverRegistrar resolveTabObserverRegistrar();
    TwaFinishHandler resolveTwaFinishHandler();
    Verifier resolveVerifier();

    // Webapp & WebAPK only
    WebappActivityCoordinator resolveWebappActivityCoordinator();

    // WebAPK only
    WebApkActivityCoordinator resolveWebApkActivityCoordinator();

    // TWA only
    TrustedWebActivityCoordinator resolveTrustedWebActivityCoordinator();

    // For testing
    CustomTabTabPersistencePolicy resolveTabPersistencePolicy();
    ReparentingTaskProvider resolveReparentingTaskProvider();
    SplashController resolveSplashController();
}
