// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.shell;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.monyhar.base.CommandLine;
import org.monyhar.base.test.BaseRobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

/** Unit tests for {@link CastCommandLineHelper}. */
@RunWith(BaseRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CastCommandLineHelperTest {
    private static final String NON_WHITELISTED_ARG = "non-whitelisted-arg";
    private static final String WHITELISTED_ARG = "audio-input-disable-eraser";
    private static final String WHITELISTED_ARG_WITH_VALUE = "setup-ssid-suffix";
    private static final String WHITELISTED_ARG_WITH_VALUE2 = "default-eureka-name-prefix";
    private static final List<String> COMMAND_LINE_ARG_WHITELIST =
            Arrays.asList(WHITELISTED_ARG, WHITELISTED_ARG_WITH_VALUE, WHITELISTED_ARG_WITH_VALUE2);

    private static Context getApplicationContext() {
        return RuntimeEnvironment.application;
    }

    private static Intent getTestIntentWithArgs() {
        Intent intent = new Intent();
        intent.putExtra(NON_WHITELISTED_ARG, "value");
        intent.putExtra(WHITELISTED_ARG, (String) null);
        intent.putExtra(WHITELISTED_ARG_WITH_VALUE, "a");
        return intent;
    }

    // Verifies that the provided CommandLine has the expected switches to match the Intent returned
    // from getTestIntentWithArgs().
    private void verifyTestIntent(CommandLine cmdLine) {
        collector.checkThat(cmdLine.hasSwitch(NON_WHITELISTED_ARG), is(false));
        collector.checkThat(cmdLine.hasSwitch(WHITELISTED_ARG), is(true));
        collector.checkThat(cmdLine.getSwitchValue(WHITELISTED_ARG), is(nullValue()));
        collector.checkThat(cmdLine.hasSwitch(WHITELISTED_ARG_WITH_VALUE), is(true));
        collector.checkThat(cmdLine.getSwitchValue(WHITELISTED_ARG_WITH_VALUE), is("a"));
    }

    private void initCommandLineWithSavedArgs(String[] initialArgs) {
        CastCommandLineHelper.initCommandLineWithSavedArgs(() -> {
            CommandLine.init(initialArgs);
            return CommandLine.getInstance();
        });
    }

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Before
    public void setUp() {
        // Reset the CommandLine to uninitialized & delete any saved args before starting each test.
        CastCommandLineHelper.resetCommandLineForTest();
        CastCommandLineHelper.resetSavedArgsForTest();
    }

    @Test
    public void testNullIntent() {
        CastCommandLineHelper.saveCommandLineArgsFromIntent(null, COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
    }

    @Test
    public void testEmptyIntent() {
        CastCommandLineHelper.saveCommandLineArgsFromIntent(
                new Intent(), COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
    }

    @Test
    public void testIntentWIthOnlyNonWhitelistedExtra() {
        Intent intent = new Intent();
        intent.putExtra(NON_WHITELISTED_ARG, "value");

        CastCommandLineHelper.saveCommandLineArgsFromIntent(intent, COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
    }

    @Test
    public void testIntentWithArgs() {
        CastCommandLineHelper.saveCommandLineArgsFromIntent(
                getTestIntentWithArgs(), COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
        verifyTestIntent(CommandLine.getInstance());
    }

    @Test
    public void testArgsArePersisted() {
        CastCommandLineHelper.saveCommandLineArgsFromIntent(
                getTestIntentWithArgs(), COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
        verifyTestIntent(CommandLine.getInstance());

        // Mimic the process being killed, such that the CommandLine singleton is reset.
        CastCommandLineHelper.resetCommandLineForTest();
        assertFalse(CommandLine.isInitialized());

        // CommandLine should be initialized with the same args as above, even if a null Intent is
        // passed in (which mimics the fact that onStartCommand receives a null Intent when a
        // START_STICKY Service is restarted).
        CastCommandLineHelper.saveCommandLineArgsFromIntent(null, COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
        verifyTestIntent(CommandLine.getInstance());
    }

    @Test
    public void testAlreadyInitialized() {
        CastCommandLineHelper.saveCommandLineArgsFromIntent(
                getTestIntentWithArgs(), COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
        verifyTestIntent(CommandLine.getInstance());

        // If Service is killed w/o process being killed, then the CommandLine singleton will still
        // be initialized. Make sure it's safe for the Service to use CastCommandLineHelper again
        // when it starts.
        CastCommandLineHelper.saveCommandLineArgsFromIntent(
                getTestIntentWithArgs(), COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(null);
        assertTrue(CommandLine.isInitialized());
        verifyTestIntent(CommandLine.getInstance());
    }

    // Test that arguments passed in through the intent don't override values already in the command
    // line, i.e. loaded from the castshell-command-line file, but that new arguments do get added.
    @Test
    public void testDontOverrideExistingValues() {
        final String[] initialArgs = new String[] {"dummyapp", "--" + WHITELISTED_ARG,
                "--" + WHITELISTED_ARG_WITH_VALUE + "=initValue"};

        Intent intent = new Intent();
        intent.putExtra(WHITELISTED_ARG, "intentValue");
        intent.putExtra(WHITELISTED_ARG_WITH_VALUE, "intentValue");
        intent.putExtra(WHITELISTED_ARG_WITH_VALUE2, "intentValue");

        CastCommandLineHelper.saveCommandLineArgsFromIntent(intent, COMMAND_LINE_ARG_WHITELIST);
        initCommandLineWithSavedArgs(initialArgs);

        assertTrue(CommandLine.isInitialized());
        final CommandLine cmdLine = CommandLine.getInstance();
        collector.checkThat(cmdLine.hasSwitch(WHITELISTED_ARG), is(true));
        collector.checkThat(cmdLine.getSwitchValue(WHITELISTED_ARG), is(nullValue()));
        collector.checkThat(cmdLine.hasSwitch(WHITELISTED_ARG_WITH_VALUE), is(true));
        collector.checkThat(cmdLine.getSwitchValue(WHITELISTED_ARG_WITH_VALUE), is("initValue"));
        collector.checkThat(cmdLine.hasSwitch(WHITELISTED_ARG_WITH_VALUE2), is(true));
        collector.checkThat(cmdLine.getSwitchValue(WHITELISTED_ARG_WITH_VALUE2), is("intentValue"));
    }
}
