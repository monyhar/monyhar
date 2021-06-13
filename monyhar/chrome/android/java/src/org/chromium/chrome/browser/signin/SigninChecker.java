// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.signin;

import android.accounts.Account;

import androidx.annotation.VisibleForTesting;

import org.monyhar.base.ApplicationState;
import org.monyhar.base.ApplicationStatus;
import org.monyhar.base.Log;
import org.monyhar.base.TraceEvent;
import org.monyhar.base.metrics.RecordUserAction;
import org.monyhar.chrome.browser.SyncFirstSetupCompleteSource;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.signin.services.SigninManager;
import org.monyhar.chrome.browser.signin.services.SigninManager.SignInCallback;
import org.monyhar.chrome.browser.sync.SyncService;
import org.monyhar.chrome.browser.sync.SyncUserDataWiper;
import org.monyhar.components.signin.AccountManagerFacade;
import org.monyhar.components.signin.AccountManagerFacadeProvider;
import org.monyhar.components.signin.AccountRenameChecker;
import org.monyhar.components.signin.AccountUtils;
import org.monyhar.components.signin.ChildAccountStatus;
import org.monyhar.components.signin.ChildAccountStatus.Status;
import org.monyhar.components.signin.base.CoreAccountInfo;
import org.monyhar.components.signin.identitymanager.AccountTrackerService;
import org.monyhar.components.signin.identitymanager.ConsentLevel;
import org.monyhar.components.signin.metrics.SigninAccessPoint;
import org.monyhar.components.signin.metrics.SignoutReason;

import java.util.List;

/**
 * This class regroups sign-in checks when chrome starts up and when accounts change on device
 */
public class SigninChecker
        implements ApplicationStatus.ApplicationStateListener, AccountTrackerService.Observer {
    private static final String TAG = "SigninChecker";
    private final SigninManager mSigninManager;
    private final AccountTrackerService mAccountTrackerService;
    private final AccountManagerFacade mAccountManagerFacade;
    // Counter to record the number of child account checks done for tests.
    private int mNumOfChildAccountChecksDone;

    /**
     * Please use SigninHelperProvider to get {@link SigninChecker} instance instead of creating it
     * manually.
     */
    public SigninChecker(SigninManager signinManager, AccountTrackerService accountTrackerService) {
        mSigninManager = signinManager;
        mAccountTrackerService = accountTrackerService;
        mAccountManagerFacade = AccountManagerFacadeProvider.getInstance();
        mNumOfChildAccountChecksDone = 0;

        ApplicationStatus.registerApplicationStateListener(this);
        mAccountTrackerService.addObserver(this);
    }

    private void validateAccountSettings() {
        mAccountManagerFacade.tryGetGoogleAccounts(accounts -> {
            mAccountTrackerService.seedAccountsIfNeeded(() -> {
                mSigninManager.runAfterOperationInProgress(() -> {
                    validatePrimaryAccountExists(accounts);
                    checkChildAccount(accounts);
                });
            });
        });
    }

    /**
     * This method is invoked every time the accounts on device are seeded.
     */
    @Override
    public void onAccountsSeeded(List<CoreAccountInfo> accountInfos) {
        final List<Account> accounts = AccountUtils.toAndroidAccounts(accountInfos);
        mSigninManager.runAfterOperationInProgress(() -> {
            validatePrimaryAccountExists(accounts);
            checkChildAccount(accounts);
        });
    }

    @VisibleForTesting
    public int getNumOfChildAccountChecksDoneForTests() {
        return mNumOfChildAccountChecksDone;
    }

    /**
     * Validates that the primary account exists on device.
     */
    private void validatePrimaryAccountExists(List<Account> accounts) {
        final CoreAccountInfo oldAccount =
                mSigninManager.getIdentityManager().getPrimaryAccountInfo(ConsentLevel.SYNC);
        if (oldAccount == null
                || AccountUtils.findAccountByName(accounts, oldAccount.getEmail()) != null) {
            // Do nothing if user is not signed in or if the primary account is still on device
            return;
        }
        // Check whether the primary account is renamed to another account when it is not on device
        AccountRenameChecker.get()
                .getNewNameOfRenamedAccountAsync(oldAccount.getEmail(), accounts)
                .then(newAccountName -> {
                    if (newAccountName != null) {
                        // Sign in to the new account if the current primary account is renamed
                        // to a new account
                        mSigninManager.signOut(SignoutReason.USER_CLICKED_SIGNOUT_SETTINGS, () -> {
                            mSigninManager.signinAndEnableSync(SigninAccessPoint.ACCOUNT_RENAMED,
                                    AccountUtils.createAccountFromName(newAccountName),
                                    new SignInCallback() {
                                        @Override
                                        public void onSignInComplete() {
                                            SyncService.get().setFirstSetupComplete(
                                                    SyncFirstSetupCompleteSource.BASIC_FLOW);
                                        }

                                        @Override
                                        public void onSignInAborted() {}
                                    });
                        }, false);
                    } else {
                        // Sign out if the current primary account is not renamed
                        mSigninManager.signOut(SignoutReason.ACCOUNT_REMOVED_FROM_DEVICE);
                    }
                });
    }

    private void checkChildAccount(List<Account> accounts) {
        if (accounts.size() == 1) {
            // Child accounts can't share a device.
            final Account account = accounts.get(0);
            mAccountManagerFacade.checkChildAccountStatus(
                    account, status -> { onChildAccountStatusReady(account, status); });
        } else {
            ++mNumOfChildAccountChecksDone;
        }
    }

    private void onChildAccountStatusReady(Account account, @Status int status) {
        if (ChildAccountStatus.isChild(status)) {
            mSigninManager.onFirstRunCheckDone();
            if (mSigninManager.isSignInAllowed()) {
                Log.d(TAG, "The child account sign-in starts.");
                final SignInCallback signInCallback = new SignInCallback() {
                    @Override
                    public void onSignInComplete() {
                        final SyncService syncService = SyncService.get();
                        if (syncService != null) {
                            syncService.setFirstSetupComplete(
                                    SyncFirstSetupCompleteSource.BASIC_FLOW);
                        }
                        ++mNumOfChildAccountChecksDone;
                    }

                    @Override
                    public void onSignInAborted() {}
                };
                boolean shouldWipeData = ChromeFeatureList.isEnabled(
                        ChromeFeatureList.WIPE_DATA_ON_CHILD_ACCOUNT_SIGNIN);
                SyncUserDataWiper.wipeSyncUserDataIfRequired(shouldWipeData).then((Void v) -> {
                    RecordUserAction.record("Signin_Signin_WipeDataOnChildAccountSignin");
                    mSigninManager.signinAndEnableSync(
                            SigninAccessPoint.FORCED_SIGNIN, account, signInCallback);
                });
                return;
            }
        }
        ++mNumOfChildAccountChecksDone;
    }

    /**
     * Called once during initialization and then again for every start (warm-start).
     * Responsible for checking if configuration has changed since Chrome was last launched
     * and updates state accordingly.
     */
    public void onMainActivityStart() {
        try (TraceEvent ignored = TraceEvent.scoped("SigninHelper.onMainActivityStart")) {
            validateAccountSettings();
        }
    }

    @Override
    public void onApplicationStateChange(int newState) {
        if (newState == ApplicationState.HAS_RUNNING_ACTIVITIES) {
            onMainActivityStart();
        }
    }
}
