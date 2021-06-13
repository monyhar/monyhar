// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.shape_detection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.test.util.UrlUtils;
import org.monyhar.gms.ChromiumPlayServicesAvailability;
import org.monyhar.skia.mojom.BitmapN32ImageInfo;

import java.nio.ByteBuffer;

/**
 * Utility class for ShapeDetection instrumentation tests,
 * provides support for e.g. reading files and converting
 * Bitmaps to mojom.Bitmaps.
 */
public class TestUtils {
    public static final boolean IS_GMS_CORE_SUPPORTED = isGmsCoreSupported();

    private static boolean isGmsCoreSupported() {
        return ChromiumPlayServicesAvailability.isGooglePlayServicesAvailable(
                ContextUtils.getApplicationContext());
    }

    public static org.monyhar.skia.mojom.BitmapN32 mojoBitmapFromBitmap(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);

        org.monyhar.skia.mojom.BitmapN32 mojoBitmap = new org.monyhar.skia.mojom.BitmapN32();
        mojoBitmap.imageInfo = new BitmapN32ImageInfo();
        mojoBitmap.imageInfo.width = bitmap.getWidth();
        mojoBitmap.imageInfo.height = bitmap.getHeight();
        mojoBitmap.pixelData = new org.monyhar.mojo_base.mojom.BigBuffer();
        mojoBitmap.pixelData.setBytes(buffer.array());
        return mojoBitmap;
    }

    public static org.monyhar.skia.mojom.BitmapN32 mojoBitmapFromFile(String relPath) {
        String path = UrlUtils.getIsolatedTestFilePath("services/test/data/" + relPath);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return mojoBitmapFromBitmap(bitmap);
    }

    public static org.monyhar.skia.mojom.BitmapN32 mojoBitmapFromText(String[] texts) {
        final int x = 10;
        final int baseline = 100;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(36.0f);
        paint.setTextAlign(Paint.Align.LEFT);

        Bitmap bitmap = Bitmap.createBitmap(1080, 480, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        for (int i = 0; i < texts.length; i++) {
            canvas.drawText(texts[i], x, baseline * (i + 1), paint);
        }

        return mojoBitmapFromBitmap(bitmap);
    }
}
