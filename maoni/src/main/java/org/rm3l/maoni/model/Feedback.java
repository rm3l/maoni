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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.SupplicantState;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A Feedback object.
 * <p>
 * This is what is returned in the Maoni Activity callbacks,
 * which you can manipulate later on (e.g, by forwarding to a remote service).
 */
public class Feedback {

    /**
     * An internal identifier for the feedback collected (auto-generated)
     */
    @NonNull
    public final CharSequence id;

    /**
     * Information about the user device
     */
    @NonNull
    public final Device deviceInfo;

    /**
     * Information about your application
     */
    @NonNull
    public final App appInfo;

    /**
     * The actual user comment
     */
    @NonNull
    public final CharSequence userComment;

    /**
     * User choice: whether to take the screenshot into account or not.
     * <p>
     * Note that if this is set to {@code false},
     * the {@link #screenshotFilePath} (and {@code #screenshot}, subsequently)
     * returned will be {@code null}
     */
    public final boolean includeScreenshot;

    /**
     * The absolute path to the screenshot file.
     * <p>
     * It is set to {@code null} if {@link #includeScreenshot} is {@code null}.
     */
    @Nullable
    public final File screenshotFilePath;

    /**
     * The actual screenshot content
     */
    @Nullable
    public final Bitmap screenshot;

    /**
     * Generic metadata to attach to the feedback.
     * <p>
     * This may be useful for storing information coming from your extra layout for example.
     */
    @NonNull
    private final Map<CharSequence, Object> additionalData = new HashMap<>();

    /**
     * Construct an immutable feedback
     * @param id the internal identifier
     * @param deviceInfo the device information
     * @param appInfo the application information
     * @param userComment the user comment
     * @param includeScreenshot whether to include the screenshot into the feedback or not
     * @param screenshotFilePath the absolute path to the screenshot file
     */
    public Feedback(@NonNull CharSequence id,
                    @NonNull Device deviceInfo,
                    @NonNull App appInfo,
                    @NonNull CharSequence userComment,
                    boolean includeScreenshot,
                    @Nullable CharSequence screenshotFilePath) {

        this.id = id;
        this.deviceInfo = deviceInfo;
        this.appInfo = appInfo;
        this.userComment = userComment;
        this.includeScreenshot = includeScreenshot;
        if (this.includeScreenshot) {
            this.screenshotFilePath =
                    (screenshotFilePath != null ? new File(screenshotFilePath.toString()) : null);
        } else {
            this.screenshotFilePath = null;
        }
        if (this.screenshotFilePath != null) {
            this.screenshot = BitmapFactory.decodeFile(this.screenshotFilePath.getAbsolutePath());
        } else {
            this.screenshot = null;
        }
    }

    /**
     * @return the feedback internal identifier
     */
    @NonNull
    public CharSequence getId() {
        return id;
    }

    /**
     * Attach a new metadata to the feedback
     * @param key the metadata key
     * @param value the metadata value
     */
    public void put(@NonNull final CharSequence key, @Nullable final Object value) {
        this.additionalData.put(key, value);
    }

    /**
     * Get the metadata value
     * @param key the metadata key
     * @return the metadata value
     */
    @Nullable
    public Object get(@NonNull final CharSequence key) {
        return this.additionalData.get(key);
    }

    /**
     * Get the metadata value, or return the value specified if not found
     * @param key the metadata key
     * @param defaultValue the default value to return if the metadata was not found
     * @return the metadata value, or return the value specified if not found
     */
    @Nullable
    public Object get(@NonNull final CharSequence key, @Nullable final Object defaultValue) {
        if (!this.additionalData.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key);
    }

    /**
     * Information about the current device (at the moment the object was constructed)
     */
    public static class Device {

        /**
         * The device model
         */
        @Nullable
        public final CharSequence model;

        /**
         * The Android version
         */
        @Nullable
        public final CharSequence androidVersion;

        /**
         * The Wifi state
         */
        @Nullable
        public final SupplicantState wifiState;

        /**
         * The mobile data state
         */
        public final boolean mobileDataEnabled;

        /**
         * The GPS state
         */
        public final boolean gpsEnabled;

        /**
         * The screen resolution
         */
        @Nullable
        public final CharSequence screenResolution;

        /**
         * Construct an immutable Device Info object
         *
         * @param model             the device model
         * @param androidVersion    the Android version
         * @param wifiState         the WiFi state
         * @param mobileDataEnabled the mobile data state
         * @param gpsEnabled        the GPS state
         * @param screenResolution  the device screen resolution
         */
        public Device(@Nullable final CharSequence model,
                      @Nullable final CharSequence androidVersion,
                      @Nullable final SupplicantState wifiState,
                      final boolean mobileDataEnabled,
                      final boolean gpsEnabled,
                      @Nullable final CharSequence screenResolution) {
            this.model = model;
            this.androidVersion = androidVersion;
            this.wifiState = wifiState;
            this.mobileDataEnabled = mobileDataEnabled;
            this.gpsEnabled = gpsEnabled;
            this.screenResolution = screenResolution;
        }

    }

    /**
     * Information about the application
     */
    public static class App {

        /**
         * The caller activity class name (the activity that called Maoni)
         */
        @Nullable
        public final CharSequence caller;

        /**
         * Is debug-mode enabled or not?
         */
        public final boolean debug;

        /**
         * The application ID
         */
        @Nullable
        public final CharSequence applicationId;

        /**
         * The version code
         */
        public final int versionCode;

        /**
         * The build flavor
         */
        @Nullable
        public final CharSequence flavor;

        /**
         * The build type
         */
        @Nullable
        public final CharSequence buildType;

        /**
         * The version name
         */
        @Nullable
        public final CharSequence versionName;

        /**
         * Build an immutable App Info object
         *
         * @param caller        the caller activity class name (the activity that called Maoni)
         * @param debug         is debug-mode enabled or not?
         * @param applicationId the application ID
         * @param versionCode   the version code
         * @param flavor        the build flavor
         * @param buildType     the build type
         * @param versionName   the version name
         */
        public App(@Nullable final CharSequence caller,
                   final boolean debug,
                   @Nullable final CharSequence applicationId,
                   final int versionCode,
                   @Nullable final CharSequence flavor,
                   @Nullable final CharSequence buildType,
                   @Nullable final CharSequence versionName) {
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
