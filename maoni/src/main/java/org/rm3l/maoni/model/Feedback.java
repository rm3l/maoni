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
package org.rm3l.maoni.model;

import android.net.wifi.SupplicantState;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Feedback {

    @NonNull
    public final CharSequence id;

    @NonNull
    public final Phone phoneInfo;

    @NonNull
    public final App appInfo;

    @NonNull
    public final CharSequence userComment;

    public final boolean includeScreenshot;

    @Nullable
    public final File screenshotFilePath;

    @NonNull
    private final Map<CharSequence, Object> additionalData = new HashMap<>();

    public Feedback(@NonNull CharSequence id,
                    @NonNull Phone phoneInfo,
                    @NonNull App appInfo,
                    @NonNull CharSequence userComment,
                    boolean includeScreenshot,
                    @Nullable CharSequence screenshotFilePath) {

        this.id = id;
        this.phoneInfo = phoneInfo;
        this.appInfo = appInfo;
        this.userComment = userComment;
        this.includeScreenshot = includeScreenshot;
        this.screenshotFilePath =
                (screenshotFilePath != null ? new File(screenshotFilePath.toString()) : null);
    }

    public void put(@NonNull final CharSequence key, @Nullable final Object value) {
        this.additionalData.put(key, value);
    }

    @Nullable
    public Object get(@NonNull final CharSequence key) {
        return this.additionalData.get(key);
    }

    @Nullable
    public Object get(@NonNull final CharSequence key, @Nullable final Object defaultValue) {
        final Object existingValue = this.additionalData.get(key);
        return existingValue != null ? existingValue : defaultValue;
    }

    @NonNull
    public CharSequence getId() {
        return id;
    }

    public static class Phone {

        public final CharSequence model;

        public final CharSequence androidVersion;

        public final SupplicantState wifiState;

        public final boolean mobileDataEnabled;

        public final boolean gpsEnabled;

        public final CharSequence screenResolution;

        public Phone(CharSequence model,
                     CharSequence androidVersion,
                     SupplicantState wifiState,
                     boolean mobileDataEnabled,
                     boolean gpsEnabled,
                     CharSequence screenResolution) {

            this.model = model;
            this.androidVersion = androidVersion;
            this.wifiState = wifiState;
            this.mobileDataEnabled = mobileDataEnabled;
            this.gpsEnabled = gpsEnabled;
            this.screenResolution = screenResolution;
        }

    }

    public static class App {

        public final CharSequence caller;

        public final boolean debug;

        public final CharSequence applicationId;

        public final int versionCode;

        public final CharSequence flavor;

        public final CharSequence buildType;

        public final CharSequence versionName;

        public App(CharSequence caller, boolean debug,
                   CharSequence applicationId,
                   int versionCode,
                   CharSequence flavor,
                   CharSequence buildType,
                   CharSequence versionName) {

            this.caller = caller;
            this.debug = debug;
            this.applicationId = applicationId;
            this.versionCode = versionCode;
            this.flavor = flavor;
            this.buildType = buildType;
            this.versionName = versionName;
        }

    }
}
