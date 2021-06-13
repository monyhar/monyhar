// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.net.impl;

import android.content.Context;

import org.monyhar.net.CronetEngine;
import org.monyhar.net.CronetProvider;
import org.monyhar.net.ExperimentalCronetEngine;
import org.monyhar.net.ICronetEngineBuilder;

import java.util.Arrays;

/**
 * Implementation of {@link CronetProvider} that creates {@link CronetEngine.Builder}
 * for building the Java-based implementation of {@link CronetEngine}.
 */
public class JavaCronetProvider extends CronetProvider {
    /**
     * Constructor.
     *
     * @param context Android context to use.
     */
    public JavaCronetProvider(Context context) {
        super(context);
    }

    @Override
    public CronetEngine.Builder createBuilder() {
        ICronetEngineBuilder impl = new JavaCronetEngineBuilderImpl(mContext);
        return new ExperimentalCronetEngine.Builder(impl);
    }

    @Override
    public String getName() {
        return CronetProvider.PROVIDER_NAME_FALLBACK;
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
        return Arrays.hashCode(new Object[] {JavaCronetProvider.class, mContext});
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof JavaCronetProvider
                           && this.mContext.equals(((JavaCronetProvider) other).mContext));
    }
}
