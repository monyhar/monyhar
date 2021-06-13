// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.share.qrcode.share_tab;

import android.content.Context;
import android.view.View;

import org.monyhar.base.metrics.RecordUserAction;
import org.monyhar.chrome.browser.share.qrcode.QrCodeDialogTab;
import org.monyhar.ui.base.AndroidPermissionDelegate;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.modelutil.PropertyModelChangeProcessor;

/**
 * Creates and represents the QrCode share panel UI.
 */
public class QrCodeShareCoordinator implements QrCodeDialogTab {
    private final QrCodeShareView mShareView;
    private final QrCodeShareMediator mMediator;

    public QrCodeShareCoordinator(Context context, Runnable closeDialog, String url,
            AndroidPermissionDelegate windowAndroid) {
        PropertyModel shareViewModel = new PropertyModel(QrCodeShareViewProperties.ALL_KEYS);
        mMediator =
                new QrCodeShareMediator(context, shareViewModel, closeDialog, url, windowAndroid);
        mShareView = new QrCodeShareView(context, mMediator::downloadQrCode);
        PropertyModelChangeProcessor.create(
                shareViewModel, mShareView, new QrCodeShareViewBinder());
    }

    /** QrCodeDialogTab implementation. */
    @Override
    public View getView() {
        return mShareView.getView();
    }

    @Override
    public void onResume() {
        mMediator.setIsOnForeground(true);
        RecordUserAction.record("SharingQRCode.TabVisible.Share");
    }

    @Override
    public void onPause() {
        mMediator.setIsOnForeground(false);
    }

    @Override
    public void onDestroy() {}
}
