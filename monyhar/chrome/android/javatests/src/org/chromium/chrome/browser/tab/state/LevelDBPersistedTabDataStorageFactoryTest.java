// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tab.state;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.monyhar.base.test.UiThreadTest;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.JniMocker;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.profiles.ProfileManager;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.components.embedder_support.browser_context.BrowserContextHandle;

/**
 * Tests relating to {@link LevelDBPersistedTabDataStorageFactory}
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class LevelDBPersistedTabDataStorageFactoryTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Rule
    public JniMocker mMocker = new JniMocker();

    @Mock
    private Profile mProfile1;

    @Mock
    private Profile mProfile2;

    @Mock
    private LevelDBPersistedDataStorage.Natives mLevelDBPersistedTabDataStorage;

    @Before
    public void setUp() throws Exception {
        mActivityTestRule.startMainActivityOnBlankPage();
        MockitoAnnotations.initMocks(this);
        mMocker.mock(LevelDBPersistedDataStorageJni.TEST_HOOKS, mLevelDBPersistedTabDataStorage);
        doNothing()
                .when(mLevelDBPersistedTabDataStorage)
                .init(any(LevelDBPersistedDataStorage.class), any(BrowserContextHandle.class));
        doNothing().when(mLevelDBPersistedTabDataStorage).destroy(anyLong());
        doReturn(false).when(mProfile1).isOffTheRecord();
        doReturn(false).when(mProfile2).isOffTheRecord();
        LevelDBPersistedDataStorage.setSkipNativeAssertionsForTesting(true);
    }

    @UiThreadTest
    @SmallTest
    @Test
    public void testFactoryMethod() {
        Profile realProfile = Profile.getLastUsedRegularProfile();
        LevelDBPersistedTabDataStorageFactory factory = new LevelDBPersistedTabDataStorageFactory();
        Profile.setLastUsedProfileForTesting(mProfile1);
        LevelDBPersistedTabDataStorage profile1Storage = factory.create();
        Profile.setLastUsedProfileForTesting(mProfile2);
        LevelDBPersistedTabDataStorage profile2Storage = factory.create();
        Profile.setLastUsedProfileForTesting(mProfile1);
        LevelDBPersistedTabDataStorage profile1StorageAgain = factory.create();
        Assert.assertEquals(profile1Storage, profile1StorageAgain);
        Assert.assertNotEquals(profile1Storage, profile2Storage);
        // Restore the original profile so the Activity can shut down correctly.
        Profile.setLastUsedProfileForTesting(realProfile);
    }

    @UiThreadTest
    @SmallTest
    @Test
    public void testStorageDestroyedWhenProfileDestroyed() {
        Profile.setLastUsedProfileForTesting(null);
        Profile profile = Profile.getLastUsedRegularProfile();
        LevelDBPersistedTabDataStorageFactory factory = new LevelDBPersistedTabDataStorageFactory();
        LevelDBPersistedTabDataStorage storage = factory.create();
        ProfileManager.onProfileDestroyed(profile);
        Assert.assertTrue(storage.isDestroyed());
    }
}
