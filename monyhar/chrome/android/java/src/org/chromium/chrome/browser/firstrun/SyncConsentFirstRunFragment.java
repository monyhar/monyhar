// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.firstrun;

import android.accounts.Account;
import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import org.monyhar.base.metrics.RecordHistogram;
import org.monyhar.base.metrics.RecordUserAction;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.ntp.cards.SignInPromo;
import org.monyhar.chrome.browser.signin.SyncConsentFragmentBase;
import org.monyhar.components.signin.AccountManagerFacadeProvider;
import org.monyhar.components.signin.ChildAccountStatus;
import org.monyhar.components.signin.metrics.SigninAccessPoint;

import java.util.List;

/**
 * Implementation of {@link SyncConsentFragmentBase} for the first run experience.
 */
public class SyncConsentFirstRunFragment
        extends SyncConsentFragmentBase implements FirstRunFragment {
    // Per-page parameters:
    // TODO(crbug/1168516): Remove CHILD_ACCOUNT_STATUS
    public static final String CHILD_ACCOUNT_STATUS = "ChildAccountStatus";

    // Every fragment must have a public default constructor.
    public SyncConsentFirstRunFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final List<Account> accounts =
                AccountManagerFacadeProvider.getInstance().tryGetGoogleAccounts();
        final @ChildAccountStatus.Status int childAccountStatus =
                getPageDelegate().getProperties().getInt(CHILD_ACCOUNT_STATUS);
        setArguments(createArguments(SigninAccessPoint.START_PAGE,
                accounts.isEmpty() ? null : accounts.get(0).name, childAccountStatus));
        // Records if there are {0, 1, 2+} accounts on device for default/non-default flows.
        RecordHistogram.recordCountHistogram(
                "Signin.AndroidDeviceAccountsNumberWhenEnteringFRE", Math.min(accounts.size(), 2));
        RecordUserAction.record("MobileFre.SignInShown");
    }

    @Override
    protected void onSigninRefused() {
        if (ChildAccountStatus.isChild(mChildAccountStatus)) {
            // Somehow the child account disappeared while we were in the FRE.
            // The user would have to go through the FRE again.
            getPageDelegate().abortFirstRunExperience();
        } else {
            SignInPromo.temporarilySuppressPromos();
            getPageDelegate().refuseSignIn();
            getPageDelegate().advanceToNextPage();
        }
    }

    @Override
    protected void onSigninAccepted(String accountName, boolean isDefaultAccount,
            boolean settingsClicked, Runnable callback) {
        getPageDelegate().acceptSignIn(accountName, isDefaultAccount, settingsClicked);
        getPageDelegate().advanceToNextPage();
        callback.run();
    }

    @Override
    public void setInitialA11yFocus() {
        // Ignore calls before view is created.
        if (getView() == null) return;

        final View title = getView().findViewById(R.id.signin_title);
        title.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
    }
}
