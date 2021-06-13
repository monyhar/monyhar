// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.page_info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.monyhar.base.test.BaseRobolectricTestRunner;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.test.util.browser.Features;
import org.monyhar.chrome.test.util.browser.LocationSettingsTestUtil;
import org.monyhar.components.content_settings.ContentSettingValues;
import org.monyhar.components.content_settings.ContentSettingsType;
import org.monyhar.components.page_info.PageInfoPermissionsController.PermissionObject;
import org.monyhar.components.page_info.PermissionParamsListBuilder;
import org.monyhar.ui.base.AndroidPermissionDelegate;
import org.monyhar.ui.base.PermissionCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for PermissionParamsListBuilder.
 */
@RunWith(BaseRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PermissionParamsListBuilderUnitTest {
    private PermissionParamsListBuilder mPermissionParamsListBuilder;

    @Rule
    public TestRule mProcessor = new Features.JUnitProcessor();

    @Mock
    Profile mProfileMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FakePermissionDelegate.clearBlockedPermissions();
        AndroidPermissionDelegate permissionDelegate = new FakePermissionDelegate();
        mPermissionParamsListBuilder =
                new PermissionParamsListBuilder(RuntimeEnvironment.application, permissionDelegate);
    }

    @Test
    public void emptyList() {
        List<PermissionObject> permissions = mPermissionParamsListBuilder.build();
        assertEquals(0, permissions.size());
    }

    @Test
    public void addSingleEntryAndBuild() {
        mPermissionParamsListBuilder.addPermissionEntry(
                "Foo", ContentSettingsType.COOKIES, ContentSettingValues.ALLOW);

        List<PermissionObject> permissions = mPermissionParamsListBuilder.build();
        assertEquals(1, permissions.size());
        PermissionObject perm = permissions.get(0);
        assertTrue(perm.allowed);
    }

    @Test
    public void addLocationEntryAndBuildWhenSystemLocationDisabled() {
        LocationSettingsTestUtil.setSystemLocationSettingEnabled(false);
        mPermissionParamsListBuilder.addPermissionEntry(
                "Test", ContentSettingsType.GEOLOCATION, ContentSettingValues.ALLOW);

        List<PermissionObject> permissions = mPermissionParamsListBuilder.build();
        assertEquals(1, permissions.size());

        PermissionObject perm = permissions.get(0);
        assertEquals(R.string.page_info_android_location_blocked, perm.warningTextResource);
    }

    @Test
    public void arNotificationWhenCameraBlocked() {
        FakePermissionDelegate.blockPermission(android.Manifest.permission.CAMERA);
        mPermissionParamsListBuilder.addPermissionEntry(
                "Test", ContentSettingsType.AR, ContentSettingValues.ALLOW);

        List<PermissionObject> permissions = mPermissionParamsListBuilder.build();
        assertEquals(1, permissions.size());

        PermissionObject perm = permissions.get(0);
        assertEquals(R.string.page_info_android_ar_camera_blocked, perm.warningTextResource);
    }

    private static class FakePermissionDelegate implements AndroidPermissionDelegate {
        private static List<String> sBlockedPermissions = new ArrayList<String>();

        private static void blockPermission(String permission) {
            sBlockedPermissions.add(permission);
        }

        private static void clearBlockedPermissions() {
            sBlockedPermissions.clear();
        }

        @Override
        public boolean hasPermission(String permission) {
            return !sBlockedPermissions.contains(permission);
        }

        @Override
        public boolean canRequestPermission(String permission) {
            return true;
        }

        @Override
        public boolean isPermissionRevokedByPolicy(String permission) {
            return false;
        }

        @Override
        public void requestPermissions(String[] permissions, PermissionCallback callback) {}

        @Override
        public boolean handlePermissionResult(
                int requestCode, String[] permissions, int[] grantResults) {
            return false;
        }
    }
}
