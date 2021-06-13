// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.permissions;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.components.content_settings.ContentSettingsType;

import java.util.Arrays;

/**
 * A utility class for permissions.
 */
public class PermissionUtil {
    /** The android permissions associated with requesting location. */
    private static final String[] LOCATION_PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION};
    /** The android permissions associated with requesting access to the camera. */
    private static final String[] CAMERA_PERMISSIONS = {android.Manifest.permission.CAMERA};
    /** The android permissions associated with requesting access to the microphone. */
    private static final String[] MICROPHONE_PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO};
    /** Signifies there are no permissions associated. */
    private static final String[] EMPTY_PERMISSIONS = {};

    private PermissionUtil() {}

    /**
     * Return the list of android permission strings for a given {@link ContentSettingsType}.  If
     * there is no permissions associated with the content setting, then an empty array is returned.
     *
     * @param contentSettingType The content setting to get the android permission for.
     * @return The android permissions for the given content setting.
     */
    @CalledByNative
    public static String[] getAndroidPermissionsForContentSetting(int contentSettingType) {
        switch (contentSettingType) {
            case ContentSettingsType.GEOLOCATION:
                return Arrays.copyOf(LOCATION_PERMISSIONS, LOCATION_PERMISSIONS.length);
            case ContentSettingsType.MEDIASTREAM_MIC:
                return Arrays.copyOf(MICROPHONE_PERMISSIONS, MICROPHONE_PERMISSIONS.length);
            case ContentSettingsType.MEDIASTREAM_CAMERA:
            case ContentSettingsType.AR:
                return Arrays.copyOf(CAMERA_PERMISSIONS, CAMERA_PERMISSIONS.length);
            default:
                return EMPTY_PERMISSIONS;
        }
    }
}
