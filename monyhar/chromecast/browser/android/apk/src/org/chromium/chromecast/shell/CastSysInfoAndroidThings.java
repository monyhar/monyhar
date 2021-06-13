// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.shell;

import android.os.Build;

import com.google.android.things.AndroidThings;
import com.google.android.things.factory.FactoryDataManager;
import com.google.android.things.update.UpdateManager;

import org.monyhar.base.Log;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Java implementation of CastSysInfoAndroidThings methods.
 */
@JNINamespace("chromecast")
public final class CastSysInfoAndroidThings {
    private static final String TAG = CastSysInfoAndroidThings.class.getSimpleName();
    private static final String FACTORY_LOCALE_LIST_FILE = "locale_list.txt";

    @CalledByNative
    private static String getProductName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return AndroidThings.Product.NAME;
        } else {
            return Build.PRODUCT;
        }
    }

    @CalledByNative
    private static String getDeviceModel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return AndroidThings.Product.MODEL;
        } else {
            return Build.MODEL;
        }
    }

    @CalledByNative
    private static String getManufacturer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return AndroidThings.Product.MANUFACTURER;
        } else {
            return Build.MANUFACTURER;
        }
    }

    @CalledByNative
    private static String getReleaseChannel() {
        return UpdateManager.getInstance().getChannel();
    }

    @CalledByNative
    private static String[] getFactoryLocaleList() {
        ArrayList<String> locale_list = new ArrayList<String>();
        try {
            FactoryDataManager factoryManager = FactoryDataManager.getInstance();
            try (InputStream input = factoryManager.openFile(FACTORY_LOCALE_LIST_FILE);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    locale_list.add(line);
                }
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to connect to FactoryDataService", e);
        } catch (IllegalArgumentException | IOException e) {
            Log.w(TAG, "Factory file %s doesn't exist or can't be opened.",
                    FACTORY_LOCALE_LIST_FILE, e);
        } catch (java.lang.SecurityException e) {
            // Thrown when com.google.android.things.permission.READ_FACTORY_DATA is missing.
            Log.e(TAG, "Failed to read factory data.", e);
        }
        return locale_list.toArray(new String[locale_list.size()]);
    }
}
