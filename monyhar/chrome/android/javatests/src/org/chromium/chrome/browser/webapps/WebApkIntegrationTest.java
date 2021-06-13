// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.support.test.InstrumentationRegistry;

import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import org.monyhar.base.CommandLine;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.DisableIf;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.FlakyTest;
import org.monyhar.chrome.browser.flags.ActivityType;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.test.MockCertVerifierRuleAndroid;
import org.monyhar.chrome.test.ChromeActivityTestRule;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.util.ChromeTabUtils;
import org.monyhar.components.webapk.lib.client.WebApkValidator;
import org.monyhar.content_public.browser.test.util.JavaScriptUtils;
import org.monyhar.content_public.common.ContentSwitches;

import java.util.concurrent.TimeoutException;

/** Integration tests for WebAPK feature. */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class WebApkIntegrationTest {
    public final WebApkActivityTestRule mActivityTestRule = new WebApkActivityTestRule();

    public MockCertVerifierRuleAndroid mCertVerifierRule =
            new MockCertVerifierRuleAndroid(0 /* net::OK */);

    @Rule
    public RuleChain mRuleChain = RuleChain.emptyRuleChain()
                                          .around(mActivityTestRule)
                                          .around(mCertVerifierRule);

    @Before
    public void setUp() {
        mActivityTestRule.getEmbeddedTestServerRule().setServerUsesHttps(true);
        Uri mapToUri =
                Uri.parse(mActivityTestRule.getEmbeddedTestServerRule().getServer().getURL("/"));
        CommandLine.getInstance().appendSwitchWithValue(
                ContentSwitches.HOST_RESOLVER_RULES, "MAP * " + mapToUri.getAuthority());
        WebApkValidator.setDisableValidationForTesting(true);
    }

    /**
     * Tests that sending deep link intent to WebAPK launches WebAPK Activity.
     */
    @Test
    @LargeTest
    @Feature({"Webapps"})
    @DisableIf.Build(message = "See https://crbug.com/1199869",
            sdk_is_greater_than = VERSION_CODES.O_MR1, sdk_is_less_than = VERSION_CODES.Q)
    public void testDeepLink() {
        String pageUrl = "https://pwa-directory.appspot.com/defaultresponse";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl));
        intent.setPackage("org.monyhar.webapk.test");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        InstrumentationRegistry.getTargetContext().startActivity(intent);

        WebappActivity lastActivity = ChromeActivityTestRule.waitFor(WebappActivity.class);
        Assert.assertEquals(ActivityType.WEB_APK, lastActivity.getActivityType());
        Assert.assertEquals(pageUrl, lastActivity.getIntentDataProvider().getUrlToLoad());
    }

    /**
     * Tests launching WebAPK via POST share intent.
     */
    @Test
    @LargeTest
    @Feature({"Webapps"})
    @FlakyTest(message = "https://crbug.com/1112352")
    public void testShare() throws TimeoutException {
        final String sharedSubject = "Fun tea parties";
        final String sharedText = "Boston";
        final String expectedShareUrl = "https://pwa-directory.appspot.com/echoall";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage("org.monyhar.webapk.test");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, sharedSubject);
        intent.putExtra(Intent.EXTRA_TEXT, sharedText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        InstrumentationRegistry.getTargetContext().startActivity(intent);

        WebappActivity lastActivity = ChromeActivityTestRule.waitFor(WebappActivity.class);
        Assert.assertEquals(ActivityType.WEB_APK, lastActivity.getActivityType());

        Tab tab = lastActivity.getActivityTab();
        ChromeTabUtils.waitForTabPageLoaded(tab, expectedShareUrl);
        String postDataJson = JavaScriptUtils.executeJavaScriptAndWaitForResult(
                tab.getWebContents(), "document.getElementsByTagName('pre')[0].innerText");
        assertEquals("\"title=Fun+tea+parties\\ntext=Boston\\n\"", postDataJson);
    }
}
