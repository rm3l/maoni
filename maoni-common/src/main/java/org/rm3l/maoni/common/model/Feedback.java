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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
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
     * Information about the application
     */
    public static class App {

        /**
         * The application ID
         */

        public final CharSequence applicationId;

        /**
         * The build type
         */

        public final CharSequence buildType;

        /**
         * The caller activity class name (the activity that called Maoni)
         */

        public final CharSequence caller;

        /**
         * Is debug-mode enabled or not?
         */
        public final Boolean debug;

        /**
         * The build flavor
         */

        public final CharSequence flavor;

        /**
         * The version code
         */
        public final Integer versionCode;

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
                final Boolean debug,
                final CharSequence applicationId,
                final Integer versionCode,
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

    /**
     * Information about your application
     */

    public final App appInfo;

    /**
     * Information about the user device
     */

    public final DeviceInfo deviceInfo;

    /**
     * An internal identifier for the feedback collected (auto-generated)
     */

    public final CharSequence id;

    /**
     * User choice: whether to take the app logs into account or not.
     * <p>
     * Note that if this is set to {@code false},
     * the {@link #logsFileUri} (and {@code #logsFile}, subsequently)
     * returned will be {@code null}
     */
    public final boolean includeLogs;

    /**
     * User choice: whether to take the screenshot into account or not.
     * <p>
     * Note that if this is set to {@code false},
     * the {@link #screenshotFileUri} (and {@code #screenshotFile}, subsequently)
     * returned will be {@code null}
     */
    public final boolean includeScreenshot;

    /**
     * The logs file object.
     * <p>
     * It is set to {@code null} if {@link #includeLogs} is {@code null}.
     */

    public final File logsFile;

    /**
     * The logs file URI.
     * <p>
     * It is set to {@code null} if {@link #includeLogs} is {@code null}.
     */

    public final Uri logsFileUri;

    /**
     * The screenshot file object.
     * <p>
     * It is set to {@code null} if {@link #includeScreenshot} is {@code null}.
     */

    public final File screenshotFile;

    /**
     * The screenshot file URI.
     * <p>
     * It is set to {@code null} if {@link #includeScreenshot} is {@code null}.
     */

    public final Uri screenshotFileUri;

    /**
     * The actual user comment
     */

    public final CharSequence userComment;

    /**
     * Generic metadata to attach to the feedback.
     * <p>
     * This may be useful for storing information coming from your extra layout for example.
     */

    private final Map<CharSequence, Object> additionalData = new HashMap<>();

    /**
     * Immutable map of the app shared preferences
     */
    public final Map<String, Object> sharedPreferences;

    /**
     * Construct an immutable feedback
     *
     * @param id                the internal identifier
     * @param activity          the origin activity
     * @param appInfo           the application information
     * @param userComment       the user comment
     * @param includeScreenshot whether to include the screenshot into the feedback or not
     * @param screenshotFileUri the screenshot file URI
     * @param screenshotFile    the screenshot File object
     * @param includeLogs       whether to include the logs into the feedback or not
     * @param logsFileUri       the logs file URI
     * @param logsFile          the logs File object
     */
    public Feedback(CharSequence id,
            Activity activity,
            App appInfo,
            CharSequence userComment,
            boolean includeScreenshot,
            Uri screenshotFileUri,
            File screenshotFile,
            boolean includeLogs,
            Uri logsFileUri,
            File logsFile,
            Map<String, Object> sharedPreferences) {

        this.id = id;
        this.deviceInfo = new DeviceInfo(activity);
        this.appInfo = appInfo;
        this.userComment = userComment;
        this.includeLogs = includeLogs;
        if (this.includeLogs) {
            this.logsFile = logsFile;
            this.logsFileUri = logsFileUri;
        } else {
            this.logsFile = null;
            this.logsFileUri = null;
        }
        this.includeScreenshot = includeScreenshot;
        if (this.includeScreenshot) {
            this.screenshotFile = screenshotFile;
            this.screenshotFileUri = screenshotFileUri;
        } else {
            this.screenshotFileUri = null;
            this.screenshotFile = null;
        }
        if (sharedPreferences != null) {
            this.sharedPreferences = Collections.unmodifiableMap(sharedPreferences);
        } else {
            this.sharedPreferences = null;
        }
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
     * @return an unmodifiable view of the additional map
     */
    public Map<CharSequence, Object> getAdditionalData() {
        return Collections.unmodifiableMap(this.additionalData);
    }

    @SuppressWarnings("unused")
    @SuppressLint("DefaultLocale")
    public Map<String, Object> getDeviceAndAppInfoAsHumanReadableMap() {
        final Map<String, Object> output = new HashMap<>();
        if (this.appInfo != null) {
            output.put("Application ID", this.appInfo.applicationId);
            output.put("Version code", this.appInfo.versionCode);
            output.put("Version name", this.appInfo.versionName);
        }

        output.put("Android version",
                String.format("Android %s (SDK %d)",
                        this.deviceInfo.androidReleaseVersion,
                        this.deviceInfo.sdkVersion));
        output.put("Device", this.deviceInfo.model);
        output.put("Manufacturer", this.deviceInfo.manufacturer);

        output.put("Device Type", this.deviceInfo.isTablet ? "Tablet" : "Phone");

        output.put("Screen density", this.deviceInfo.densityDpi + " dpi");
        output.put("Screen size", this.deviceInfo.resolution);
        output.put("Native platform", Arrays.toString(this.deviceInfo.supportedAbis));
        if (!(this.deviceInfo.openGlVersion == null ||
                "".equals(this.deviceInfo.openGlVersion.trim()))) {
            output.put("OpenGL ES version", this.deviceInfo.openGlVersion);
        }
        output.put("Device language", this.deviceInfo.language);

        return Collections.unmodifiableMap(output);
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
}
