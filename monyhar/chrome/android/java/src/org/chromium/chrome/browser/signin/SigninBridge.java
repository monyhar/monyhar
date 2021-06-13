// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.signin;

import android.accounts.Account;
import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.monyhar.base.ThreadUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.supplier.Supplier;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.signin.services.IdentityServicesProvider;
import org.monyhar.chrome.browser.signin.services.SigninManager;
import org.monyhar.chrome.browser.signin.services.SigninMetricsUtils;
import org.monyhar.chrome.browser.signin.services.SigninPreferencesManager;
import org.monyhar.chrome.browser.signin.services.WebSigninBridge;
import org.monyhar.chrome.browser.signin.ui.account_picker.AccountPickerBottomSheetCoordinator;
import org.monyhar.chrome.browser.signin.ui.account_picker.AccountPickerDelegateImpl;
import org.monyhar.chrome.browser.signin.ui.account_picker.AccountPickerFeatureUtils;
import org.monyhar.chrome.browser.sync.settings.AccountManagementFragment;
import org.monyhar.chrome.browser.tabmodel.TabModel;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;
import org.monyhar.chrome.browser.tabmodel.TabModelSelectorSupplier;
import org.monyhar.chrome.browser.tabmodel.TabModelUtils;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetController;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetControllerProvider;
import org.monyhar.components.signin.AccountManagerFacadeProvider;
import org.monyhar.components.signin.AccountUtils;
import org.monyhar.components.signin.GAIAServiceType;
import org.monyhar.components.signin.metrics.AccountConsistencyPromoAction;
import org.monyhar.components.signin.metrics.SigninAccessPoint;
import org.monyhar.ui.base.WindowAndroid;

import java.util.List;

/**
 * The bridge regroups methods invoked by native code to interact with Android Signin UI.
 */
final class SigninBridge {
    /**
     * Launches {@link SyncConsentActivity}.
     * @param windowAndroid WindowAndroid from which to get the Context.
     * @param accessPoint for metrics purposes.
     */
    @CalledByNative
    private static void launchSigninActivity(
            WindowAndroid windowAndroid, @SigninAccessPoint int accessPoint) {
        final Context context = windowAndroid.getContext().get();
        if (context != null) {
            SyncConsentActivityLauncherImpl.get().launchActivityIfAllowed(context, accessPoint);
        }
    }

    /**
     * Opens account management screen.
     */
    @CalledByNative
    private static void openAccountManagementScreen(
            WindowAndroid windowAndroid, @GAIAServiceType int gaiaServiceType) {
        ThreadUtils.assertOnUiThread();
        final Context context = windowAndroid.getContext().get();
        if (context != null) {
            AccountManagementFragment.openAccountManagementScreen(context, gaiaServiceType);
        }
    }

    /**
     * Opens account picker bottom sheet.
     */
    @VisibleForTesting
    @CalledByNative
    static void openAccountPickerBottomSheet(WindowAndroid windowAndroid, String continueUrl) {
        ThreadUtils.assertOnUiThread();
        SigninManager signinManager = IdentityServicesProvider.get().getSigninManager(
                Profile.getLastUsedRegularProfile());
        if (!signinManager.isSignInAllowed()) {
            SigninMetricsUtils.logAccountConsistencyPromoAction(
                    AccountConsistencyPromoAction.SUPPRESSED_SIGNIN_NOT_ALLOWED);
            return;
        }
        final List<Account> accounts = AccountUtils.getAccountsIfFulfilledOrEmpty(
                AccountManagerFacadeProvider.getInstance().getAccounts());
        if (accounts.isEmpty()) {
            // TODO(https://crbug.com/1119720): Show the bottom sheet when no accounts on device
            //  in the future. This disabling is only temporary.
            SigninMetricsUtils.logAccountConsistencyPromoAction(
                    AccountConsistencyPromoAction.SUPPRESSED_NO_ACCOUNTS);
            return;
        }
        if (SigninPreferencesManager.getInstance().getAccountPickerBottomSheetActiveDismissalCount()
                >= AccountPickerFeatureUtils.getDismissLimit()) {
            SigninMetricsUtils.logAccountConsistencyPromoAction(
                    AccountConsistencyPromoAction.SUPPRESSED_CONSECUTIVE_DISMISSALS);
            return;
        }
        BottomSheetController bottomSheetController =
                BottomSheetControllerProvider.from(windowAndroid);
        if (bottomSheetController == null) {
            // The bottomSheetController can be null when google.com is just opened inside a
            // bottom sheet for example. In this case, it's better to disable the account picker
            // bottom sheet.
            return;
        }
        // To close the current regular tab after the user clicks on "Continue" in the incognito
        // interstitial.
        final Supplier<TabModelSelector> tabModelSelectorSupplier =
                TabModelSelectorSupplier.from(windowAndroid);
        assert tabModelSelectorSupplier.hasValue() : "No TabModelSelector available.";
        final TabModel regularTabModel =
                tabModelSelectorSupplier.get().getModel(/*incognito=*/false);
        new AccountPickerBottomSheetCoordinator(windowAndroid.getActivity().get(),
                bottomSheetController,
                new AccountPickerDelegateImpl(windowAndroid,
                        TabModelUtils.getCurrentTab(regularTabModel), new WebSigninBridge.Factory(),
                        continueUrl));
    }

    private SigninBridge() {}
}
