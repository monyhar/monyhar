// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.mojo.bindings;

import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.BaseJUnit4ClassRunner;
import org.monyhar.mojo.MojoTestRule;
import org.monyhar.mojo.bindings.BindingsTestUtils.RecordingMessageReceiver;
import org.monyhar.mojo.system.Core;
import org.monyhar.mojo.system.DataPipe;
import org.monyhar.mojo.system.Handle;
import org.monyhar.mojo.system.MessagePipeHandle;
import org.monyhar.mojo.system.MojoException;
import org.monyhar.mojo.system.MojoResult;
import org.monyhar.mojo.system.Pair;
import org.monyhar.mojo.system.impl.CoreImpl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Testing {@link Connector#readAndDispatchMessage}.
 */
@RunWith(BaseJUnit4ClassRunner.class)
public class ReadAndDispatchMessageTest {
    @Rule
    public MojoTestRule mTestRule = new MojoTestRule();

    private static final int DATA_SIZE = 1024;

    private ByteBuffer mData;
    private Pair<MessagePipeHandle, MessagePipeHandle> mHandles;
    private List<Handle> mHandlesToSend = new ArrayList<Handle>();
    private List<Handle> mHandlesToClose = new ArrayList<Handle>();
    private RecordingMessageReceiver mMessageReceiver;

    /**
     * @see org.monyhar.mojo.MojoTestCase#setUp()
     */
    @Before
    public void setUp() {
        Core core = CoreImpl.getInstance();
        mData = BindingsTestUtils.newRandomMessage(DATA_SIZE).getData();
        mMessageReceiver = new RecordingMessageReceiver();
        mHandles = core.createMessagePipe(new MessagePipeHandle.CreateOptions());
        Pair<DataPipe.ProducerHandle, DataPipe.ConsumerHandle> datapipe = core.createDataPipe(null);
        mHandlesToSend.addAll(Arrays.asList(datapipe.first, datapipe.second));
        mHandlesToClose.addAll(Arrays.asList(mHandles.first, mHandles.second));
        mHandlesToClose.addAll(mHandlesToSend);
    }

    /**
     * @see org.monyhar.mojo.MojoTestCase#tearDown()
     */
    @After
    public void tearDown() {
        for (Handle handle : mHandlesToClose) {
            handle.close();
        }
    }

    /**
     * Testing {@link Connector#readAndDispatchMessage(MessagePipeHandle, MessageReceiver)}
     */
    @Test
    @SmallTest
    public void testReadAndDispatchMessage() {
        mHandles.first.writeMessage(mData, mHandlesToSend, MessagePipeHandle.WriteFlags.NONE);
        Assert.assertEquals(MojoResult.OK,
                Connector.readAndDispatchMessage(mHandles.second, mMessageReceiver)
                        .getMojoResult());
        Assert.assertEquals(1, mMessageReceiver.messages.size());
        Message message = mMessageReceiver.messages.get(0);
        mHandlesToClose.addAll(message.getHandles());
        Assert.assertEquals(mData, message.getData());
        Assert.assertEquals(2, message.getHandles().size());
        for (Handle handle : message.getHandles()) {
            Assert.assertTrue(handle.isValid());
        }
    }

    /**
     * Testing {@link Connector#readAndDispatchMessage(MessagePipeHandle, MessageReceiver)}
     * with no message available.
     */
    @Test
    @SmallTest
    public void testReadAndDispatchMessageOnEmptyHandle() {
        Assert.assertEquals(MojoResult.SHOULD_WAIT,
                Connector.readAndDispatchMessage(mHandles.second, mMessageReceiver)
                        .getMojoResult());
        Assert.assertEquals(0, mMessageReceiver.messages.size());
    }

    /**
     * Testing {@link Connector#readAndDispatchMessage(MessagePipeHandle, MessageReceiver)}
     * on closed handle.
     */
    @Test
    @SmallTest
    public void testReadAndDispatchMessageOnClosedHandle() {
        mHandles.first.close();
        try {
            Connector.readAndDispatchMessage(mHandles.second, mMessageReceiver);
            Assert.fail("MojoException should have been thrown");
        } catch (MojoException expected) {
            Assert.assertEquals(MojoResult.FAILED_PRECONDITION, expected.getMojoResult());
        }
        Assert.assertEquals(0, mMessageReceiver.messages.size());
    }
}
