// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview.test;

import static org.monyhar.android_webview.test.OnlyRunIn.ProcessMode.MULTI_PROCESS;

import android.util.Pair;

import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.android_webview.AwContents;
import org.monyhar.android_webview.AwRenderProcess;
import org.monyhar.android_webview.AwRenderProcessGoneDetail;
import org.monyhar.android_webview.renderer_priority.RendererPriority;
import org.monyhar.base.task.PostTask;
import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.DisabledTest;
import org.monyhar.base.test.util.Feature;
import org.monyhar.content_public.browser.UiThreadTaskTraits;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.content_public.common.ContentUrlConstants;
import org.monyhar.net.test.util.TestWebServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tests for AwContentsClient.onRenderProcessGone callback.
 */
@RunWith(AwJUnit4ClassRunner.class)
public class AwContentsClientOnRenderProcessGoneTest {
    private static final String TAG = "AwRendererGone";
    @Rule
    public AwActivityTestRule mActivityTestRule = new AwActivityTestRule();

    private TestWebServer mWebServer;
    private RenderProcessGoneTestAwContentsClient mContentsClient;
    private AwTestContainerView mTestView;
    private AwContents mAwContents;

    @Before
    public void setUp() throws Exception {
        mWebServer = TestWebServer.start();
        mContentsClient = new RenderProcessGoneTestAwContentsClient();
        mTestView = mActivityTestRule.createAwTestContainerViewOnMainSync(mContentsClient);
        mAwContents = mTestView.getAwContents();
    }

    @After
    public void tearDown() {
        mWebServer.shutdown();
    }

    private String addPageToTestServer(String httpPath, String html) {
        List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        headers.add(Pair.create("Content-Type", "text/html"));
        headers.add(Pair.create("Cache-Control", "no-store"));
        return mWebServer.setResponse(httpPath, html, headers);
    }

    private static class GetRenderProcessGoneHelper extends CallbackHelper {
        private AwRenderProcessGoneDetail mDetail;

        public AwRenderProcessGoneDetail getAwRenderProcessGoneDetail() {
            assert getCallCount() > 0;
            return mDetail;
        }

        public void notifyCalled(AwRenderProcessGoneDetail detail) {
            mDetail = detail;
            notifyCalled();
        }
    }

    private static class RenderProcessGoneTestAwContentsClient extends TestAwContentsClient {

        private GetRenderProcessGoneHelper mGetRenderProcessGoneHelper;

        public RenderProcessGoneTestAwContentsClient() {
            mGetRenderProcessGoneHelper = new GetRenderProcessGoneHelper();
        }

        public GetRenderProcessGoneHelper getGetRenderProcessGoneHelper() {
            return mGetRenderProcessGoneHelper;
        }

        @Override
        public boolean onRenderProcessGone(AwRenderProcessGoneDetail detail) {
            mGetRenderProcessGoneHelper.notifyCalled(detail);
            return true;
        }
    }

    interface Terminator {
        void terminate();
    }

    private AwRenderProcess createAndTerminateRenderProcess(
            Terminator terminator, boolean expectCrash) throws Throwable {
        GetRenderProcessGoneHelper helper = mContentsClient.getGetRenderProcessGoneHelper();

        final AwRenderProcess renderProcess =
                TestThreadUtils.runOnUiThreadBlocking(() -> mAwContents.getRenderProcess());

        // Ensure that the renderer has started.
        mActivityTestRule.loadUrlSync(mAwContents, mContentsClient.getOnPageFinishedHelper(),
                ContentUrlConstants.ABOUT_BLANK_DISPLAY_URL);

        // Terminate the renderer.
        PostTask.runOrPostTask(UiThreadTaskTraits.DEFAULT, () -> terminator.terminate());

        // Assert that onRenderProcessGone is called once.
        int callCount = helper.getCallCount();
        helper.waitForCallback(callCount, 1, CallbackHelper.WAIT_TIMEOUT_SECONDS * 5,
                TimeUnit.SECONDS);
        Assert.assertEquals(callCount + 1, helper.getCallCount());
        Assert.assertEquals(helper.getAwRenderProcessGoneDetail().didCrash(), expectCrash);
        Assert.assertEquals(
                RendererPriority.HIGH, helper.getAwRenderProcessGoneDetail().rendererPriority());

        return renderProcess;
    }

    @Test
    @DisabledTest // http://crbug.com/689292
    @Feature({"AndroidWebView"})
    @SmallTest
    @OnlyRunIn(MULTI_PROCESS)
    public void testOnRenderProcessCrash() throws Throwable {
        createAndTerminateRenderProcess(() -> { mAwContents.loadUrl("chrome://crash"); }, true);
    }

    @Test
    @Feature({"AndroidWebView"})
    @SmallTest
    @OnlyRunIn(MULTI_PROCESS)
    public void testOnRenderProcessKill() throws Throwable {
        createAndTerminateRenderProcess(() -> { mAwContents.loadUrl("chrome://kill"); }, false);
    }

    @Test
    @Feature({"AndroidWebView"})
    @SmallTest
    @OnlyRunIn(MULTI_PROCESS)
    public void testRenderProcessTermination() throws Throwable {
        createAndTerminateRenderProcess(
                () -> { mAwContents.getRenderProcess().terminate(); }, false);
    }

    @Test
    @Feature({"AndroidWebView"})
    @SmallTest
    @OnlyRunIn(MULTI_PROCESS)
    public void testRenderProcessDifferentAfterRestart() throws Throwable {
        AwRenderProcess renderProcess1 = createAndTerminateRenderProcess(
                () -> { mAwContents.getRenderProcess().terminate(); }, false);
        AwRenderProcess renderProcess2 = createAndTerminateRenderProcess(
                () -> { mAwContents.getRenderProcess().terminate(); }, false);
        Assert.assertNotEquals(renderProcess1, renderProcess2);
    }

    @Test
    @Feature({"AndroidWebView"})
    @SmallTest
    @OnlyRunIn(MULTI_PROCESS)
    public void testRenderProcessCanNotTerminateBeforeStart() throws Throwable {
        Assert.assertFalse(TestThreadUtils.runOnUiThreadBlocking(
                () -> mAwContents.getRenderProcess().terminate()));
    }

    @Test
    @Feature({"AndroidWebView"})
    @SmallTest
    @OnlyRunIn(MULTI_PROCESS)
    public void testRenderProcessSameBeforeAndAfterStart() throws Throwable {
        AwRenderProcess renderProcessBeforeStart =
                TestThreadUtils.runOnUiThreadBlocking(() -> mAwContents.getRenderProcess());

        // Ensure that the renderer has started.
        mActivityTestRule.loadUrlSync(mAwContents, mContentsClient.getOnPageFinishedHelper(),
                ContentUrlConstants.ABOUT_BLANK_DISPLAY_URL);

        AwRenderProcess renderProcessAfterStart =
                TestThreadUtils.runOnUiThreadBlocking(() -> mAwContents.getRenderProcess());

        Assert.assertEquals(renderProcessBeforeStart, renderProcessAfterStart);
    }

    @Test
    @Feature({"AndroidWebView"})
    @SmallTest
    @OnlyRunIn(MULTI_PROCESS)
    // The RenderDocument feature has a "level" parameter. This enables the feature and sets the
    // default level to "crashed-frame" to enable replacing the render frame host of crashed frames
    // with a new render frame host (instead of resuing the old one). See
    // https://go/force-field-trials-docs for the syntax of these flags.
    @CommandLineFlags.Add({
            "enable-features=RenderDocument<RenderDocument",
            "force-fieldtrials=RenderDocument/Group1",
            "force-fieldtrial-params=RenderDocument.Group1:level/crashed-frame",
    })
    public void
    testNavigationAfterCrashAndJavaScript() throws Throwable {
        // In https://crbug.com/1006814, a crashed frame, reinitialized by running JS fails to
        // navigate.
        mActivityTestRule.getAwSettingsOnUiThread(mAwContents).setJavaScriptEnabled(true);
        // Crash the frame.
        AwRenderProcess renderProcess1 = createAndTerminateRenderProcess(
                () -> { mAwContents.getRenderProcess().terminate(); }, false);
        // Run JS in the frame.
        Assert.assertEquals("3",
                mActivityTestRule.executeJavaScriptAndWaitForResult(
                        mAwContents, mContentsClient, "1+2"));
        // Navigate to somewhere else. This should not crash the frame.
        final String content = "some content";
        final String httpPathOnServer = addPageToTestServer("/page.html", content);
        mActivityTestRule.loadUrlSync(
                mAwContents, mContentsClient.getOnPageFinishedHelper(), httpPathOnServer);
        // Result is a string, so needs "".
        Assert.assertEquals("\"" + content + "\"",
                mActivityTestRule.executeJavaScriptAndWaitForResult(
                        mAwContents, mContentsClient, "document.body.textContent"));
    }
}
