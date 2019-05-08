/*
 * Copyright (c) 2016 Armel Soro
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.rm3l.maoni.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Utilities for manipulating {@code Context}.
 */
public final class ContextUtils {

    private ContextUtils() {
    }

    /**
     * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
     * are used at the project level to set custom fields.
     * <p/>
     * Workaround inspired from http://goo.gl/gKQqkC
     *
     * @param context   Used to find the correct file
     * @param fieldName The name of the field to access
     * @return The value of the field, or {@literal null} if the field is not found.
     */
    @Nullable
    public static Object getBuildConfigValue(@NonNull final Context context,
                                             @NonNull final String fieldName) {
        try {
            return Class
                    .forName(String.format("%s.BuildConfig", context.getPackageName()))
                    .getField(fieldName)
                    .get(null);
        } catch (final Exception e) {
            //No worries
            e.printStackTrace();
            return null;
        }
    }
}
