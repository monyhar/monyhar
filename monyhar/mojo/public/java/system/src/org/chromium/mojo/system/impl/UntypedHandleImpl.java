// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.mojo.system.impl;

import org.monyhar.mojo.system.DataPipe.ConsumerHandle;
import org.monyhar.mojo.system.DataPipe.ProducerHandle;
import org.monyhar.mojo.system.MessagePipeHandle;
import org.monyhar.mojo.system.SharedBufferHandle;
import org.monyhar.mojo.system.UntypedHandle;

/**
 * Implementation of {@link UntypedHandle}.
 */
class UntypedHandleImpl extends HandleBase implements UntypedHandle {
    /**
     * @see HandleBase#HandleBase(CoreImpl, int)
     */
    UntypedHandleImpl(CoreImpl core, int mojoHandle) {
        super(core, mojoHandle);
    }

    /**
     * @see HandleBase#HandleBase(HandleBase)
     */
    UntypedHandleImpl(HandleBase handle) {
        super(handle);
    }

    /**
     * @see org.monyhar.mojo.system.UntypedHandle#pass()
     */
    @Override
    public UntypedHandle pass() {
        return new UntypedHandleImpl(this);
    }

    /**
     * @see org.monyhar.mojo.system.UntypedHandle#toMessagePipeHandle()
     */
    @Override
    public MessagePipeHandle toMessagePipeHandle() {
        return new MessagePipeHandleImpl(this);
    }

    /**
     * @see org.monyhar.mojo.system.UntypedHandle#toDataPipeConsumerHandle()
     */
    @Override
    public ConsumerHandle toDataPipeConsumerHandle() {
        return new DataPipeConsumerHandleImpl(this);
    }

    /**
     * @see org.monyhar.mojo.system.UntypedHandle#toDataPipeProducerHandle()
     */
    @Override
    public ProducerHandle toDataPipeProducerHandle() {
        return new DataPipeProducerHandleImpl(this);
    }

    /**
     * @see org.monyhar.mojo.system.UntypedHandle#toSharedBufferHandle()
     */
    @Override
    public SharedBufferHandle toSharedBufferHandle() {
        return new SharedBufferHandleImpl(this);
    }
}
