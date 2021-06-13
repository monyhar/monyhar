// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.dependency_injection;

import org.monyhar.chrome.browser.AppHooksModule;
import org.monyhar.chrome.browser.browserservices.ClearDataDialogResultRecorder;
import org.monyhar.chrome.browser.browserservices.SessionDataHolder;
import org.monyhar.chrome.browser.browserservices.TrustedWebActivityClient;
import org.monyhar.chrome.browser.browserservices.permissiondelegation.PermissionUpdater;
import org.monyhar.chrome.browser.browserservices.permissiondelegation.TrustedWebActivityPermissionManager;
import org.monyhar.chrome.browser.customtabs.CustomTabsClientFileProcessor;
import org.monyhar.chrome.browser.customtabs.CustomTabsConnection;
import org.monyhar.chrome.browser.customtabs.dependency_injection.BaseCustomTabActivityComponent;
import org.monyhar.chrome.browser.customtabs.dependency_injection.BaseCustomTabActivityModule;
import org.monyhar.chrome.browser.preferences.SharedPreferencesManager;
import org.monyhar.components.externalauth.ExternalAuthUtils;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component representing the Singletons in the main process of the application.
 */
@Component(modules = {ChromeAppModule.class, AppHooksModule.class})
@Singleton
public interface ChromeAppComponent {
    ChromeActivityComponent createChromeActivityComponent(ChromeActivityCommonsModule module);

    BaseCustomTabActivityComponent createBaseCustomTabActivityComponent(
            ChromeActivityCommonsModule module,
            BaseCustomTabActivityModule baseCustomTabActivityModule);

    CustomTabsConnection resolveCustomTabsConnection();
    SharedPreferencesManager resolveSharedPreferencesManager();
    ClearDataDialogResultRecorder resolveTwaClearDataDialogRecorder();
    TrustedWebActivityPermissionManager resolveTwaPermissionManager();
    PermissionUpdater resolveTwaPermissionUpdater();
    TrustedWebActivityClient resolveTrustedWebActivityClient();

    ExternalAuthUtils resolveExternalAuthUtils();

    CustomTabsClientFileProcessor resolveCustomTabsFileProcessor();
    SessionDataHolder resolveSessionDataHolder();
}
