// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.test.util.browser.signin;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.junit.Assert;

import org.monyhar.base.ThreadUtils;
import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.chrome.browser.SyncFirstSetupCompleteSource;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.signin.services.IdentityServicesProvider;
import org.monyhar.chrome.browser.signin.services.SigninManager;
import org.monyhar.chrome.browser.sync.SyncService;
import org.monyhar.components.signin.AccountUtils;
import org.monyhar.components.signin.base.CoreAccountInfo;
import org.monyhar.components.signin.identitymanager.ConsentLevel;
import org.monyhar.components.signin.metrics.SigninAccessPoint;
import org.monyhar.components.signin.metrics.SignoutReason;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;

import java.util.concurrent.TimeoutException;

/**
 * Utility class for test signin functionality.
 */
public final class SigninTestUtil {
    /**
     * Returns the currently signed in coreAccountInfo.
     */
    static CoreAccountInfo getCurrentAccount() {
        return TestThreadUtils.runOnUiThreadBlockingNoException(() -> {
            return IdentityServicesProvider.get()
                    .getIdentityManager(Profile.getLastUsedRegularProfile())
                    .getPrimaryAccountInfo(ConsentLevel.SYNC);
        });
    }

    /**
     * Signs the user into the given account.
     */
    static void signin(CoreAccountInfo coreAccountInfo) {
        CallbackHelper callbackHelper = new CallbackHelper();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            SigninManager signinManager = IdentityServicesProvider.get().getSigninManager(
                    Profile.getLastUsedRegularProfile());
            signinManager.onFirstRunCheckDone(); // Allow sign-in
            signinManager.signin(coreAccountInfo, new SigninManager.SignInCallback() {
                @Override
                public void onSignInComplete() {
                    callbackHelper.notifyCalled();
                }

                @Override
                public void onSignInAborted() {
                    Assert.fail("Sign-in was aborted");
                }
            });
        });
        try {
            callbackHelper.waitForFirst();
        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting for callback", e);
        }
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            Assert.assertEquals(coreAccountInfo,
                    IdentityServicesProvider.get()
                            .getIdentityManager(Profile.getLastUsedRegularProfile())
                            .getPrimaryAccountInfo(ConsentLevel.SIGNIN));
        });
    }

    /**
     * Signs into an account and enables the sync if given a {@link SyncService} object.
     *
     * @param syncService Enable the sync with it if it is not null.
     */
    @WorkerThread
    public static void signinAndEnableSync(
            CoreAccountInfo coreAccountInfo, @Nullable SyncService syncService) {
        CallbackHelper callbackHelper = new CallbackHelper();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            SigninManager signinManager = IdentityServicesProvider.get().getSigninManager(
                    Profile.getLastUsedRegularProfile());
            signinManager.onFirstRunCheckDone(); // Allow sign-in
            signinManager.signinAndEnableSync(SigninAccessPoint.UNKNOWN,
                    AccountUtils.createAccountFromName(coreAccountInfo.getEmail()),
                    new SigninManager.SignInCallback() {
                        @Override
                        public void onSignInComplete() {
                            if (syncService != null) {
                                syncService.setFirstSetupComplete(
                                        SyncFirstSetupCompleteSource.BASIC_FLOW);
                            }
                            callbackHelper.notifyCalled();
                        }

                        @Override
                        public void onSignInAborted() {
                            Assert.fail("Sign-in was aborted");
                        }
                    });
        });
        try {
            callbackHelper.waitForFirst();
        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting for callback", e);
        }
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            Assert.assertEquals(coreAccountInfo,
                    IdentityServicesProvider.get()
                            .getIdentityManager(Profile.getLastUsedRegularProfile())
                            .getPrimaryAccountInfo(ConsentLevel.SYNC));
        });
    }

    /**
     * Waits for the AccountTrackerService to seed system accounts.
     */
    static void seedAccounts() {
        ThreadUtils.assertOnBackgroundThread();
        CallbackHelper ch = new CallbackHelper();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            IdentityServicesProvider.get()
                    .getAccountTrackerService(Profile.getLastUsedRegularProfile())
                    .seedAccountsIfNeeded(ch::notifyCalled);
        });
        try {
            ch.waitForFirst("Timed out while waiting for system accounts to seed.");
        } catch (TimeoutException ex) {
            throw new RuntimeException("Timed out while waiting for system accounts to seed.");
        }
    }

    static void signOut() {
        ThreadUtils.assertOnBackgroundThread();
        CallbackHelper callbackHelper = new CallbackHelper();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            IdentityServicesProvider.get()
                    .getSigninManager(Profile.getLastUsedRegularProfile())
                    .signOut(SignoutReason.SIGNOUT_TEST, callbackHelper::notifyCalled, false);
        });
        try {
            callbackHelper.waitForFirst();
        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting for callback", e);
        }
    }

    private SigninTestUtil() {}
}
