// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.vr;

import static org.monyhar.chrome.browser.vr.WebXrArTestFramework.PAGE_LOAD_TIMEOUT_S;

import android.os.Build;

import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import org.monyhar.base.test.params.ParameterAnnotations.ClassParameter;
import org.monyhar.base.test.params.ParameterAnnotations.UseRunnerDelegate;
import org.monyhar.base.test.params.ParameterSet;
import org.monyhar.base.test.params.ParameterizedRunner;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.MinAndroidSdkLevel;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.vr.rules.ArPlaybackFile;
import org.monyhar.chrome.browser.vr.rules.XrActivityRestriction;
import org.monyhar.chrome.browser.vr.util.ArTestRuleUtils;
import org.monyhar.chrome.test.ChromeActivityTestRule;
import org.monyhar.chrome.test.ChromeJUnit4RunnerDelegate;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * End-to-end tests for testing WebXR for AR's hit testing behavior.
 */
@RunWith(ParameterizedRunner.class)
@UseRunnerDelegate(ChromeJUnit4RunnerDelegate.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE,
        "enable-features=WebXR,WebXRARModule,WebXRHitTest,LogJsConsoleMessages"})
@MinAndroidSdkLevel(Build.VERSION_CODES.N) // WebXR for AR is only supported on N+
public class WebXrArHitTestTest {
    @ClassParameter
    private static List<ParameterSet> sClassParams =
            ArTestRuleUtils.generateDefaultTestRuleParameters();
    @Rule
    public RuleChain mRuleChain;

    private ChromeActivityTestRule mTestRule;
    private WebXrArTestFramework mWebXrArTestFramework;

    public WebXrArHitTestTest(Callable<ChromeActivityTestRule> callable) throws Exception {
        mTestRule = callable.call();
        mRuleChain = ArTestRuleUtils.wrapRuleInActivityRestrictionRule(mTestRule);
    }

    @Before
    public void setUp() {
        mWebXrArTestFramework = new WebXrArTestFramework(mTestRule);
    }

    /**
     * Tests that hit test returns a valid result when there is a plane present.
     */
    @Test
    @MediumTest
    @XrActivityRestriction({XrActivityRestriction.SupportedActivity.ALL})
    @ArPlaybackFile("chrome/test/data/xr/ar_playback_datasets/floor_session_12s_30fps.mp4")
    public void testHitTestSucceedsWithPlane() {
        mWebXrArTestFramework.loadFileAndAwaitInitialization(
                "webxr_test_basic_hittest", PAGE_LOAD_TIMEOUT_S);
        mWebXrArTestFramework.enterSessionWithUserGestureOrFail();
        mWebXrArTestFramework.executeStepAndWait("stepStartHitTesting()");
        mWebXrArTestFramework.endTest();
    }

    /**
     * Tests that hit test results are available in the subsequent frame after hit
     * test source was returned.
     */
    @Test
    @MediumTest
    @XrActivityRestriction({XrActivityRestriction.SupportedActivity.ALL})
    @ArPlaybackFile("chrome/test/data/xr/ar_playback_datasets/floor_session_12s_30fps.mp4")
    public void testHitTestResultsAvailableInSubsequentFrame() {
        mWebXrArTestFramework.loadFileAndAwaitInitialization(
                "webxr_test_basic_hittest_results_availability", PAGE_LOAD_TIMEOUT_S);
        mWebXrArTestFramework.enterSessionWithUserGestureOrFail();
        mWebXrArTestFramework.executeStepAndWait("stepStartHitTesting()");
        mWebXrArTestFramework.endTest();
    }

    /**
     * Tests that hit test cancellation works for hit test sources when the session has ended.
     */
    @Test
    @MediumTest
    @XrActivityRestriction({XrActivityRestriction.SupportedActivity.ALL})
    @ArPlaybackFile("chrome/test/data/xr/ar_playback_datasets/floor_session_12s_30fps.mp4")
    public void testHitTestCancellationWorks() {
        mWebXrArTestFramework.loadFileAndAwaitInitialization(
                "webxr_test_basic_hittest_cancellation", PAGE_LOAD_TIMEOUT_S);
        mWebXrArTestFramework.enterSessionWithUserGestureOrFail();
        mWebXrArTestFramework.executeStepAndWait("stepStartHitTesting(false)");
        mWebXrArTestFramework.endTest();
    }

    /**
     * Tests that hit test cancellation works for transient input hit tests when the session has
     * ended.
     */
    @Test
    @MediumTest
    @XrActivityRestriction({XrActivityRestriction.SupportedActivity.ALL})
    @ArPlaybackFile("chrome/test/data/xr/ar_playback_datasets/floor_session_12s_30fps.mp4")
    public void testHitTestForTransientInputCancellationWorks() {
        mWebXrArTestFramework.loadFileAndAwaitInitialization(
                "webxr_test_basic_hittest_cancellation", PAGE_LOAD_TIMEOUT_S);
        mWebXrArTestFramework.enterSessionWithUserGestureOrFail();
        mWebXrArTestFramework.executeStepAndWait("stepStartHitTesting(true)");
        mWebXrArTestFramework.endTest();
    }
}
