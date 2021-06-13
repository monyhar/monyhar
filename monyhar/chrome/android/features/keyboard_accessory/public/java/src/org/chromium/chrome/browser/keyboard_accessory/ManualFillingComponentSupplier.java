// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.keyboard_accessory;

import org.monyhar.base.UnownedUserDataKey;
import org.monyhar.base.supplier.ObservableSupplier;
import org.monyhar.base.supplier.UnownedUserDataSupplier;
import org.monyhar.ui.base.WindowAndroid;

/**
 * A {@link UnownedUserDataSupplier} which manages the supplier and UnownedUserData for a
 * {@link ManualFillingComponent}.
 */
public class ManualFillingComponentSupplier
        extends UnownedUserDataSupplier<ManualFillingComponent> {
    private static final UnownedUserDataKey<ManualFillingComponentSupplier> KEY =
            new UnownedUserDataKey<>(ManualFillingComponentSupplier.class);

    /**
     * Return {@link ManualFillingComponent} supplier associated with the given {@link
     * WindowAndroid}.
     */
    public static ObservableSupplier<ManualFillingComponent> from(WindowAndroid windowAndroid) {
        return KEY.retrieveDataFromHost(windowAndroid.getUnownedUserDataHost());
    }

    /**
     * Constructs a ManualFillingComponentSupplier and attaches it to the {@link WindowAndroid}
     */
    public ManualFillingComponentSupplier() {
        super(KEY);
    }
}