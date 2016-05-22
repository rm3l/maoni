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
package org.rm3l.maoni.common.model;

import android.net.Uri;
import android.net.wifi.SupplicantState;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A Feedback object.
 * <p/>
 * This is what is returned in the Maoni Activity callbacks,
 * which you can manipulate later on (e.g, by forwarding to a remote service).
 */
public class Feedback {

    /**
     * An internal identifier for the feedback collected (auto-generated)
     */

    public final CharSequence id;

    /**
     * Information about the user device
     */

    public final Device deviceInfo;

    /**
     * Information about your application
     */

    public final App appInfo;

    /**
     * The actual user comment
     */

    public final CharSequence userComment;

    /**
     * User choice: whether to take the screenshot into account or not.
     * <p/>
     * Note that if this is set to {@code false},
     * the {@link #screenshotFileUri} (and {@code #screenshotFile}, subsequently)
     * returned will be {@code null}
     */
    public final boolean includeScreenshot;

    /**
     * The screenshot file URI.
     * <p/>
     * It is set to {@code null} if {@link #includeScreenshot} is {@code null}.
     */

    public final Uri screenshotFileUri;

    /**
     * The screenshot file object.
     * <p/>
     * It is set to {@code null} if {@link #includeScreenshot} is {@code null}.
     */

    public final File screenshotFile;

    /**
     * Generic metadata to attach to the feedback.
     * <p/>
     * This may be useful for storing information coming from your extra layout for example.
     */

    private final Map<CharSequence, Object> additionalData = new HashMap<>();

    /**
     * Construct an immutable feedback
     *
     * @param id                the internal identifier
     * @param deviceInfo        the device information
     * @param appInfo           the application information
     * @param userComment       the user comment
     * @param includeScreenshot whether to include the screenshot into the feedback or not
     * @param screenshotFileUri the screenshot file URI
     */
    public Feedback(CharSequence id,
                    Device deviceInfo,
                    App appInfo,
                    CharSequence userComment,
                    boolean includeScreenshot,
                    Uri screenshotFileUri,
                    File screenshotFile) {

        this.id = id;
        this.deviceInfo = deviceInfo;
        this.appInfo = appInfo;
        this.userComment = userComment;
        this.includeScreenshot = includeScreenshot;
        if (this.includeScreenshot) {
            this.screenshotFile = screenshotFile;
            this.screenshotFileUri = screenshotFileUri;
        } else {
            this.screenshotFileUri = null;
            this.screenshotFile = null;
        }
    }

    /**
     * @return the feedback internal identifier
     */

    public CharSequence getId() {
        return id;
    }

    /**
     * Attach a new metadata to the feedback
     *
     * @param key   the metadata key
     * @param value the metadata value
     */
    public void put(final CharSequence key, final Object value) {
        this.additionalData.put(key, value);
    }

    /**
     * Get the metadata value
     *
     * @param key the metadata key
     * @return the metadata value
     */

    public Object get(final CharSequence key) {
        return this.additionalData.get(key);
    }

    /**
     * @return an unmodifiable view of the additional map
     */
    public Map<CharSequence, Object> getAdditionalData() {
        return Collections.unmodifiableMap(this.additionalData);
    }

    /**
     * Get the metadata value, or return the value specified if not found
     *
     * @param key          the metadata key
     * @param defaultValue the default value to return if the metadata was not found
     * @return the metadata value, or return the value specified if not found
     */

    public Object get(final CharSequence key, final Object defaultValue) {
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

        public final CharSequence model;

        /**
         * The Android version
         */

        public final CharSequence androidVersion;

        /**
         * The Wifi state
         */

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
        public Device(final CharSequence model,
                      final CharSequence androidVersion,
                      final SupplicantState wifiState,
                      final boolean mobileDataEnabled,
                      final boolean gpsEnabled,
                      final CharSequence screenResolution) {
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

        public final CharSequence caller;

        /**
         * Is debug-mode enabled or not?
         */
        public final boolean debug;

        /**
         * The application ID
         */

        public final CharSequence applicationId;

        /**
         * The version code
         */
        public final int versionCode;

        /**
         * The build flavor
         */

        public final CharSequence flavor;

        /**
         * The build type
         */

        public final CharSequence buildType;

        /**
         * The version name
         */

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
        public App(final CharSequence caller,
                   final boolean debug,
                   final CharSequence applicationId,
                   final int versionCode,
                   final CharSequence flavor,
                   final CharSequence buildType,
                   final CharSequence versionName) {
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
