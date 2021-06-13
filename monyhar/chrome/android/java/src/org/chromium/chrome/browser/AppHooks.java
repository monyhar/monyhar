// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.chrome.browser.customtabs.CustomTabsConnection;
import org.monyhar.chrome.browser.directactions.DirectActionCoordinator;
import org.monyhar.chrome.browser.feedback.FeedbackReporter;
import org.monyhar.chrome.browser.feedback.HelpAndFeedbackLauncher;
import org.monyhar.chrome.browser.feedback.HelpAndFeedbackLauncherImpl;
import org.monyhar.chrome.browser.gsa.GSAHelper;
import org.monyhar.chrome.browser.historyreport.AppIndexingReporter;
import org.monyhar.chrome.browser.init.ChromeStartupDelegate;
import org.monyhar.chrome.browser.init.ProcessInitializationHandler;
import org.monyhar.chrome.browser.instantapps.InstantAppsHandler;
import org.monyhar.chrome.browser.metrics.VariationsSession;
import org.monyhar.chrome.browser.notifications.chime.ChimeDelegate;
import org.monyhar.chrome.browser.omaha.RequestGenerator;
import org.monyhar.chrome.browser.partnerbookmarks.PartnerBookmark;
import org.monyhar.chrome.browser.partnerbookmarks.PartnerBookmarksProviderIterator;
import org.monyhar.chrome.browser.partnercustomizations.PartnerBrowserCustomizations;
import org.monyhar.chrome.browser.password_manager.GooglePasswordManagerUIProvider;
import org.monyhar.chrome.browser.policy.PolicyAuditor;
import org.monyhar.chrome.browser.rlz.RevenueStats;
import org.monyhar.chrome.browser.signin.ui.GoogleActivityController;
import org.monyhar.chrome.browser.survey.SurveyController;
import org.monyhar.chrome.browser.sync.TrustedVaultClient;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.usage_stats.DigitalWellbeingClient;
import org.monyhar.chrome.browser.webapps.GooglePlayWebApkInstallDelegate;
import org.monyhar.chrome.browser.xsurface.ProcessScope;
import org.monyhar.chrome.browser.xsurface.ProcessScopeDependencyProvider;
import org.monyhar.chrome.modules.image_editor.ImageEditorModuleProvider;
import org.monyhar.components.external_intents.AuthenticatorNavigationInterceptor;
import org.monyhar.components.policy.AppRestrictionsProvider;
import org.monyhar.components.policy.CombinedPolicyProvider;
import org.monyhar.components.signin.AccountManagerDelegate;
import org.monyhar.components.signin.SystemAccountManagerDelegate;
import org.monyhar.components.webapps.AppDetailsDelegate;

import java.util.Collections;
import java.util.List;

/**
 * Base class for defining methods where different behavior is required by downstream targets.
 * The correct version of {@link AppHooksImpl} will be determined at compile time via build rules.
 * See http://crbug/560466.
 */
public abstract class AppHooks {
    private static AppHooksImpl sInstance;

    /**
     * Sets a mocked instance for testing.
     */
    @VisibleForTesting
    public static void setInstanceForTesting(AppHooksImpl instance) {
        sInstance = instance;
    }

    @CalledByNative
    public static AppHooks get() {
        if (sInstance == null) sInstance = new AppHooksImpl();
        return sInstance;
    }

    /**
     * Creates a new {@link AccountManagerDelegate}.
     * @return the created {@link AccountManagerDelegate}.
     */
    public AccountManagerDelegate createAccountManagerDelegate() {
        return new SystemAccountManagerDelegate();
    }

    /**
     * @return An instance of AppDetailsDelegate that can be queried about app information for the
     *         App Banner feature.  Will be null if one is unavailable.
     */
    public AppDetailsDelegate createAppDetailsDelegate() {
        return null;
    }

    /**
     * Creates a new {@link AppIndexingReporter}.
     * @return the created {@link AppIndexingReporter}.
     */
    public AppIndexingReporter createAppIndexingReporter() {
        return new AppIndexingReporter();
    }

    /**
     * Return a {@link AuthenticatorNavigationInterceptor} for the given {@link Tab}.
     * This can be null if there are no applicable interceptor to be built.
     */
    public AuthenticatorNavigationInterceptor createAuthenticatorNavigationInterceptor(Tab tab) {
        return null;
    }

    /**
     * @return An instance of {@link CustomTabsConnection}. Should not be called
     * outside of {@link CustomTabsConnection#getInstance()}.
     */
    public CustomTabsConnection createCustomTabsConnection() {
        return new CustomTabsConnection();
    }

    /**
     * Returns a new {@link DirectActionCoordinator} instance, if available.
     */
    @Nullable
    public DirectActionCoordinator createDirectActionCoordinator() {
        return null;
    }

    /**
     * Creates a new {@link SurveyController}.
     * @return The created {@link SurveyController}.
     */
    public SurveyController createSurveyController() {
        return new SurveyController();
    }

    /**
     * @return An instance of {@link FeedbackReporter} to report feedback.
     */
    public FeedbackReporter createFeedbackReporter() {
        return new FeedbackReporter() {};
    }

    /**
     * @return An instance of GoogleActivityController.
     */
    public GoogleActivityController createGoogleActivityController() {
        return new GoogleActivityController();
    }

    /**
     * @return An instance of {@link GSAHelper} that handles the start point of chrome's integration
     *         with GSA.
     */
    public GSAHelper createGsaHelper() {
        return new GSAHelper();
    }

    /**
     * Returns a new instance of HelpAndFeedbackLauncher.
     */
    public HelpAndFeedbackLauncher createHelpAndFeedbackLauncher() {
        return new HelpAndFeedbackLauncherImpl();
    }

    public InstantAppsHandler createInstantAppsHandler() {
        return new InstantAppsHandler();
    }

    /**
     * @return An instance of {@link GooglePasswordManagerUIProvider}. Will be null if one is not
     *         available.
     */
    public GooglePasswordManagerUIProvider createGooglePasswordManagerUIProvider() {
        return null;
    }

    /**
     * @return An instance of RequestGenerator to be used for Omaha XML creation.  Will be null if
     *         a generator is unavailable.
     */
    public RequestGenerator createOmahaRequestGenerator() {
        return null;
    }

    /**
     * @return a new {@link ProcessInitializationHandler} instance.
     */
    public ProcessInitializationHandler createProcessInitializationHandler() {
        return new ProcessInitializationHandler();
    }

    /**
     * @return An instance of RevenueStats to be installed as a singleton.
     */
    public RevenueStats createRevenueStatsInstance() {
        return new RevenueStats();
    }

    /**
     * Returns a new instance of VariationsSession.
     */
    public VariationsSession createVariationsSession() {
        return new VariationsSession();
    }

    /** Returns the singleton instance of GooglePlayWebApkInstallDelegate. */
    public GooglePlayWebApkInstallDelegate getGooglePlayWebApkInstallDelegate() {
        return null;
    }

    /**
     * @return An instance of PolicyAuditor that notifies the policy system of the user's activity.
     * Only applicable when the user has a policy active, that is tracking the activity.
     */
    public PolicyAuditor getPolicyAuditor() {
        // This class has a protected constructor to prevent accidental instantiation.
        return new PolicyAuditor() {};
    }

    public void registerPolicyProviders(CombinedPolicyProvider combinedProvider) {
        combinedProvider.registerProvider(
                new AppRestrictionsProvider(ContextUtils.getApplicationContext()));
    }

    /**
     * @return A list of allowlisted apps that are allowed to receive notification when the
     * set of offlined pages downloaded on their behalf has changed. Apps are listed by their
     * package name.
     */
    public List<String> getOfflinePagesCctAllowlist() {
        return Collections.emptyList();
    }

    /**
     * @return A list of allowlisted app package names whose completed notifications
     * we should suppress.
     */
    public List<String> getOfflinePagesSuppressNotificationPackages() {
        return Collections.emptyList();
    }

    /**
     * @return An iterator of partner bookmarks.
     */
    @Nullable
    public PartnerBookmark.BookmarkIterator getPartnerBookmarkIterator() {
        return PartnerBookmarksProviderIterator.createIfAvailable();
    }

    /**
     * @return An instance of PartnerBrowserCustomizations.Provider that provides customizations
     * specified by partners.
     */
    public PartnerBrowserCustomizations.Provider getCustomizationProvider() {
        return new PartnerBrowserCustomizations.ProviderPackage();
    }

    /**
     * @return A new {@link DigitalWellbeingClient} instance.
     */
    public DigitalWellbeingClient createDigitalWellbeingClient() {
        return new DigitalWellbeingClient();
    }

    /**
     * Checks the Google Play services availability on the this device.
     *
     * This is a workaround for the
     * versioned API of {@link GoogleApiAvailability#isGooglePlayServicesAvailable()}. The current
     * Google Play services SDK version doesn't have this API yet.
     *
     * TODO(zqzhang): Remove this method after the SDK is updated.
     *
     * @return status code indicating whether there was an error. The possible return values are the
     * same as {@link GoogleApiAvailability#isGooglePlayServicesAvailable()}.
     */
    public int isGoogleApiAvailableWithMinApkVersion(int minApkVersion) {
        try {
            PackageInfo gmsPackageInfo =
                    ContextUtils.getApplicationContext().getPackageManager().getPackageInfo(
                            GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, /* flags= */ 0);
            int apkVersion = gmsPackageInfo.versionCode;
            if (apkVersion >= minApkVersion) return ConnectionResult.SUCCESS;
        } catch (PackageManager.NameNotFoundException e) {
            return ConnectionResult.SERVICE_MISSING;
        }
        return ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;
    }

    /**
     * Returns a new {@link TrustedVaultClient.Backend} instance.
     */
    public TrustedVaultClient.Backend createSyncTrustedVaultClientBackend() {
        return new TrustedVaultClient.EmptyBackend();
    }

    /**
     * Returns a new {@link SurfaceRenderer} if the xsurface implementation is included in the
     * apk. Otherwise null is returned.
     */
    public @Nullable ProcessScope getExternalSurfaceProcessScope(
            ProcessScopeDependencyProvider dependencies) {
        return null;
    }

    /**
     * Returns the URL to the WebAPK creation/update server.
     */
    public String getWebApkServerUrl() {
        return "";
    }

    /**
     * Returns a Chime Delegate if the chime module is defined.
     */
    public ChimeDelegate getChimeDelegate() {
        return new ChimeDelegate();
    }

    public @Nullable ImageEditorModuleProvider getImageEditorModuleProvider() {
        return null;
    }

    public ChromeStartupDelegate createChromeStartupDelegate() {
        return new ChromeStartupDelegate();
    }

    public boolean canStartForegroundServiceWhileInvisible() {
        return true;
    }

    public String getDefaultQueryTilesServerUrl() {
        return "";
    }
}
