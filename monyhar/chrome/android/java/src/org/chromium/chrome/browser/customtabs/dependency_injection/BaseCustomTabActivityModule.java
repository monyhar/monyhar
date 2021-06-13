// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.customtabs.dependency_injection;

import org.monyhar.chrome.browser.browserservices.ClientAppDataRegister;
import org.monyhar.chrome.browser.browserservices.intents.BrowserServicesIntentDataProvider;
import org.monyhar.chrome.browser.browserservices.trustedwebactivityui.TwaIntentHandlingStrategy;
import org.monyhar.chrome.browser.browserservices.ui.controller.EmptyVerifier;
import org.monyhar.chrome.browser.browserservices.ui.controller.Verifier;
import org.monyhar.chrome.browser.browserservices.ui.controller.trustedwebactivity.TwaVerifier;
import org.monyhar.chrome.browser.browserservices.ui.controller.webapps.AddToHomescreenVerifier;
import org.monyhar.chrome.browser.browserservices.ui.controller.webapps.WebApkVerifier;
import org.monyhar.chrome.browser.browserservices.verification.OriginVerifierFactory;
import org.monyhar.chrome.browser.browserservices.verification.OriginVerifierFactoryImpl;
import org.monyhar.chrome.browser.customtabs.CustomTabNightModeStateController;
import org.monyhar.chrome.browser.customtabs.content.CustomTabIntentHandler.IntentIgnoringCriterion;
import org.monyhar.chrome.browser.customtabs.content.CustomTabIntentHandlingStrategy;
import org.monyhar.chrome.browser.customtabs.content.DefaultCustomTabIntentHandlingStrategy;
import org.monyhar.chrome.browser.flags.ActivityType;
import org.monyhar.chrome.browser.init.StartupTabPreloader;
import org.monyhar.chrome.browser.theme.TopUiThemeColorProvider;
import org.monyhar.chrome.browser.webapps.WebApkPostShareTargetNavigator;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

/**
 * Module for bindings shared between custom tabs and webapps.
 */
@Module
public class BaseCustomTabActivityModule {
    private final BrowserServicesIntentDataProvider mIntentDataProvider;
    private final StartupTabPreloader mStartupTabPreloader;
    private final @ActivityType int mActivityType;
    private final CustomTabNightModeStateController mNightModeController;
    private final IntentIgnoringCriterion mIntentIgnoringCriterion;
    private final TopUiThemeColorProvider mTopUiThemeColorProvider;

    public BaseCustomTabActivityModule(BrowserServicesIntentDataProvider intentDataProvider,
            StartupTabPreloader startupTabPreloader,
            CustomTabNightModeStateController nightModeController,
            IntentIgnoringCriterion intentIgnoringCriterion,
            TopUiThemeColorProvider topUiThemeColorProvider) {
        mIntentDataProvider = intentDataProvider;
        mStartupTabPreloader = startupTabPreloader;
        mActivityType = intentDataProvider.getActivityType();
        mNightModeController = nightModeController;
        mIntentIgnoringCriterion = intentIgnoringCriterion;
        mTopUiThemeColorProvider = topUiThemeColorProvider;
    }

    @Provides
    public BrowserServicesIntentDataProvider providesBrowserServicesIntentDataProvider() {
        return mIntentDataProvider;
    }

    @Provides
    public CustomTabIntentHandlingStrategy provideIntentHandler(
            Lazy<DefaultCustomTabIntentHandlingStrategy> defaultHandler,
            Lazy<TwaIntentHandlingStrategy> twaHandler) {
        return (mActivityType == ActivityType.TRUSTED_WEB_ACTIVITY
                       || mActivityType == ActivityType.WEB_APK)
                ? twaHandler.get()
                : defaultHandler.get();
    }

    @Provides
    public StartupTabPreloader provideStartupTabPreloader() {
        return mStartupTabPreloader;
    }

    @Provides
    public Verifier provideVerifier(Lazy<WebApkVerifier> webApkVerifier,
            Lazy<AddToHomescreenVerifier> addToHomescreenVerifier, Lazy<TwaVerifier> twaVerifier,
            Lazy<EmptyVerifier> emptyVerifier) {
        switch (mActivityType) {
            case ActivityType.WEB_APK:
                return webApkVerifier.get();
            case ActivityType.WEBAPP:
                return addToHomescreenVerifier.get();
            case ActivityType.TRUSTED_WEB_ACTIVITY:
                return twaVerifier.get();
            default:
                return emptyVerifier.get();
        }
    }

    @Provides
    public IntentIgnoringCriterion provideIntentIgnoringCriterion() {
        return mIntentIgnoringCriterion;
    }

    @Provides
    public TopUiThemeColorProvider provideTopUiThemeColorProvider() {
        return mTopUiThemeColorProvider;
    }

    @Provides
    public CustomTabNightModeStateController provideNightModeController() {
        return mNightModeController;
    }

    @Provides
    @Reusable
    public WebApkPostShareTargetNavigator providePostShareTargetNavigator() {
        return new WebApkPostShareTargetNavigator();
    }

    @Provides
    public ClientAppDataRegister provideClientAppDataRegister() {
        return new ClientAppDataRegister();
    }

    @Provides
    @Reusable
    public OriginVerifierFactory providesOriginVerifierFactory() {
        return new OriginVerifierFactoryImpl();
    }
}
