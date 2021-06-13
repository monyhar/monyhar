// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content_public.browser.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** An annotation used in test to hard-code some of the ChildProcessAllocator settings */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChildProcessAllocatorSettings {
    int sandboxedServiceCount() default - 1;
    String sandboxedServiceName() default "";
}
