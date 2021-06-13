// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.layouts;

import org.monyhar.chrome.browser.layouts.scene_layer.SceneLayer;
import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.modelutil.PropertyModelChangeProcessor;

/** An interface for classes that manage the display of layouts and their components. */
public interface LayoutManager extends LayoutStateProvider {
    /**
     * Add a {@link SceneOverlay} to be drawn on the composited layer of the active layout.
     * @param overlay The overlay to add.
     */
    void addSceneOverlay(SceneOverlay overlay);

    /**
     * Creates a CompositorModelChangeProcessor observing the given {@code model} that will operate
     * on this {@code LayoutManager}'s frame cycle. The model will be bound to the view initially
     * and request a new frame.
     * @param model The model containing the data to be bound to the view.
     * @param view The view which the model will be bound to.
     * @param viewBinder This is used to bind the model to the view.
     */
    <V extends SceneLayer> CompositorModelChangeProcessor<V> createCompositorMCP(
            PropertyModel model, V view,
            PropertyModelChangeProcessor.ViewBinder<PropertyModel, V, PropertyKey> viewBinder);
}
