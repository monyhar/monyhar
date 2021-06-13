// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.net.impl;

import android.content.Context;

import org.monyhar.base.annotations.UsedByReflection;
import org.monyhar.net.CronetEngine;
import org.monyhar.net.CronetProvider;
import org.monyhar.net.ExperimentalCronetEngine;
import org.monyhar.net.ICronetEngineBuilder;

import java.util.Arrays;

/**
 * Implementation of {@link CronetProvider} that creates {@link CronetEngine.Builder}
 * for building the native implementation of {@link CronetEngine}.
 */
public class NativeCronetProvider extends CronetProvider {
    /**
     * Constructor.
     *
     * @param context Android context to use.
     */
    @UsedByReflection("CronetProvider.java")
    public NativeCronetProvider(Context context) {
        super(context);
    }

    @Override
    public CronetEngine.Builder createBuilder() {
        ICronetEngineBuilder impl = new NativeCronetEngineBuilderWithLibraryLoaderImpl(mContext);
        return new ExperimentalCronetEngine.Builder(impl);
    }

    @Override
    public String getName() {
        return CronetProvider.PROVIDER_NAME_APP_PACKAGED;
    }

    @Override
    public String getVersion() {
        return ImplVersion.getCronetVersion();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {NativeCronetProvider.class, mContext});
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof NativeCronetProvider
                           && this.mContext.equals(((NativeCronetProvider) other).mContext));
    }
}
