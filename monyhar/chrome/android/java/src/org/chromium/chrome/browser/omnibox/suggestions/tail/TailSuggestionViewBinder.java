// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.omnibox.suggestions.tail;

import androidx.annotation.ColorRes;

import org.monyhar.base.ApiCompatibilityUtils;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.omnibox.styles.OmniboxResourceProvider;
import org.monyhar.chrome.browser.omnibox.suggestions.SuggestionCommonProperties;
import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel;

/** Properties associated with the tail suggestion view. */
public class TailSuggestionViewBinder {
    /** @see PropertyModelChangeProcessor.ViewBinder#bind(Object, Object, Object) */
    public static void bind(PropertyModel model, TailSuggestionView view, PropertyKey propertyKey) {
        if (TailSuggestionViewProperties.ALIGNMENT_MANAGER == propertyKey) {
            view.setAlignmentManager(model.get(TailSuggestionViewProperties.ALIGNMENT_MANAGER));
        } else if (propertyKey == TailSuggestionViewProperties.TEXT) {
            view.setTailText(model.get(TailSuggestionViewProperties.TEXT));
        } else if (propertyKey == TailSuggestionViewProperties.FILL_INTO_EDIT) {
            view.setFullText(model.get(TailSuggestionViewProperties.FILL_INTO_EDIT));
        } else if (propertyKey == SuggestionCommonProperties.OMNIBOX_THEME) {
            final boolean useDarkMode = !OmniboxResourceProvider.isDarkMode(
                    model.get(SuggestionCommonProperties.OMNIBOX_THEME));
            @ColorRes
            final int color = useDarkMode ? R.color.default_text_color_dark
                                          : R.color.default_text_color_light;
            view.setTextColor(ApiCompatibilityUtils.getColor(view.getResources(), color));
        }
    }
}
