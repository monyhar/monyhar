// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.infobar;

import android.text.TextUtils;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.chrome.R;
import org.monyhar.components.infobars.ConfirmInfoBar;
import org.monyhar.components.infobars.InfoBar;
import org.monyhar.components.infobars.InfoBarControlLayout;
import org.monyhar.components.infobars.InfoBarLayout;
import org.monyhar.components.signin.base.AccountInfo;

/**
 * The Save Password infobar asks the user whether they want to save the password for the site.
 */
public class SavePasswordInfoBar extends ConfirmInfoBar {
    private final String mDetailsMessage;
    private final AccountInfo mAccountInfo;

    @CalledByNative
    private static InfoBar show(int iconId, String message, String detailsMessage,
            String primaryButtonText, String secondaryButtonText, AccountInfo accountInfo) {
        // If accountInfo is empty, no footer will be shown.
        return new SavePasswordInfoBar(iconId, message, detailsMessage, primaryButtonText,
                secondaryButtonText, accountInfo);
    }

    private SavePasswordInfoBar(int iconDrawbleId, String message, String detailsMessage,
            String primaryButtonText, String secondaryButtonText, AccountInfo accountInfo) {
        super(iconDrawbleId, R.color.infobar_icon_drawable_color, null, message, null,
                primaryButtonText, secondaryButtonText);
        mDetailsMessage = detailsMessage;
        mAccountInfo = accountInfo;
    }

    @Override
    public void createContent(InfoBarLayout layout) {
        super.createContent(layout);
        if (!TextUtils.isEmpty(mDetailsMessage)) {
            InfoBarControlLayout detailsMessageLayout = layout.addControlLayout();
            detailsMessageLayout.addDescription(mDetailsMessage);
        }

        if (mAccountInfo != null && !TextUtils.isEmpty(mAccountInfo.getEmail())
                && mAccountInfo.getAccountImage() != null) {
            layout.addFooterView(PasswordInfoBarUtils.createAccountIndicationFooter(
                    layout.getContext(), mAccountInfo.getAccountImage(), mAccountInfo.getEmail()));
        }
    }
}
