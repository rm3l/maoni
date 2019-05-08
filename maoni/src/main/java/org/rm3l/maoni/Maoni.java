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
package org.rm3l.maoni;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.rm3l.maoni.common.contract.Handler;
import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.contract.UiListener;
import org.rm3l.maoni.common.contract.Validator;
import org.rm3l.maoni.email.MaoniEmailListener;
import org.rm3l.maoni.ui.MaoniActivity;
import org.rm3l.maoni.utils.ContextUtils;
import org.rm3l.maoni.utils.ViewUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.rm3l.maoni.Maoni.CallbacksConfiguration.getInstance;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_DEBUG;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_BUILD_CONFIG_FLAVOR;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_PACKAGE_NAME;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_VERSION_CODE;
import static org.rm3l.maoni.ui.MaoniActivity.APPLICATION_INFO_VERSION_NAME;
import static org.rm3l.maoni.ui.MaoniActivity.CALLER_ACTIVITY;
import static org.rm3l.maoni.ui.MaoniActivity.CONTENT_ERROR_TEXT;
import static org.rm3l.maoni.ui.MaoniActivity.CONTENT_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.EXTRA_LAYOUT;
import static org.rm3l.maoni.ui.MaoniActivity.FILE_PROVIDER_AUTHORITY;
import static org.rm3l.maoni.ui.MaoniActivity.HEADER;
import static org.rm3l.maoni.ui.MaoniActivity.INCLUDE_LOGS_TEXT;
import static org.rm3l.maoni.ui.MaoniActivity.INCLUDE_SCREENSHOT_TEXT;
import static org.rm3l.maoni.ui.MaoniActivity.LOGS_CAPTURING_FEATURE_ENABLED;
import static org.rm3l.maoni.ui.MaoniActivity.MESSAGE;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_FILE;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_TOUCH_TO_PREVIEW_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.SCREEN_CAPTURING_FEATURE_ENABLED;
import static org.rm3l.maoni.ui.MaoniActivity.SHARED_PREFERENCES;
import static org.rm3l.maoni.ui.MaoniActivity.SHOW_KEYBOARD_ON_START;
import static org.rm3l.maoni.ui.MaoniActivity.THEME;
import static org.rm3l.maoni.ui.MaoniActivity.TOOLBAR_SUBTITLE_TEXT_COLOR;
import static org.rm3l.maoni.ui.MaoniActivity.TOOLBAR_TITLE_TEXT_COLOR;
import static org.rm3l.maoni.ui.MaoniActivity.WINDOW_SUBTITLE;
import static org.rm3l.maoni.ui.MaoniActivity.WINDOW_TITLE;
import static org.rm3l.maoni.ui.MaoniActivity.WORKING_DIR;

/**
 * Maoni configuration
 */
@SuppressWarnings("WeakerAccess")
public class Maoni {

    private static final String LOG_TAG = Maoni.class.getSimpleName();

    private static final String MAONI_FEEDBACK_SCREENSHOT_FILENAME = "maoni_feedback_screenshot.png";

    private static final String DEBUG = "DEBUG";
    private static final String FLAVOR = "FLAVOR";
    private static final String BUILD_TYPE = "BUILD_TYPE";
    public static final String DEFAULT_LISTENER_EMAIL_TO = "DEFAULT_LISTENER_EMAIL_TO";

    /**
     * The feedback window title
     */
    @Nullable
    public final CharSequence windowTitle;
    /**
     * The feedback window sub-title
     */
    @Nullable
    public final CharSequence windowSubTitle;
    /**
     * The feedback window title color
     */
    @ColorRes
    @Nullable
    public final Integer windowTitleTextColor;
    /**
     * The feedback window sub-title color
     */
    @ColorRes
    @Nullable
    public final Integer windowSubTitleTextColor;
    /**
     * The message to display to the user
     */
    @Nullable
    public final CharSequence message;
    /**
     * The feedback form field error message to display to the user
     */
    @Nullable
    public final CharSequence contentErrorMessage;
    /**
     * The feedback form field hint message
     */
    @Nullable
    public final CharSequence feedbackContentHint;
    /**
     * Some text to display to the user, such as how the screenshot will be used by you,
     * and any links to your privacy policy
     */
    @Nullable
    public final CharSequence screenshotHint;
    /**
     * Header image
     */
    @DrawableRes
    @Nullable
    public final Integer header;
    /**
     * Text do display next to the "Include logs" checkbox
     */
    @Nullable
    public final CharSequence includeLogsText;
    /**
     * Text do display next to the "Include screenshot" checkbox
     */
    @Nullable
    public final CharSequence includeScreenshotText;
    /**
     * The "Touch to preview" text (displayed below the screenshot thumbnail).
     * Keep it short and to the point
     */
    @Nullable
    public final CharSequence touchToPreviewScreenshotText;
    /**
     * Extra layout resource.
     * Will be displayed between the feedback content field and the "Include screenshot" checkbox.
     */
    @LayoutRes
    @Nullable
    public final Integer extraLayout;
    @StyleRes
    @Nullable
    public final Integer theme;
    private final String fileProviderAuthority;
    @Nullable
    private final Context context;
    private File maoniWorkingDir;

    private final HashMap<String, Object> sharedPreferencesContentMap;

    private boolean showKeyboardOnStart;
    private boolean screenCapturingFeatureEnabled = true;
    private boolean logsCapturingFeatureEnabled = true;

    private final AtomicBoolean mUsed = new AtomicBoolean(false);

    /**
     * Default constructor: non-instantiable
     */
    @SuppressWarnings("unused")
    private Maoni() {
        throw new UnsupportedOperationException("Non instantiable this way. Use Maoni.Builder builder class instead.");
    }

    /**
     * Constructor
     * @param builder the builder instance to use for instantiated a Maoni instance
     */
    private Maoni(final Builder builder) {
        this.context = builder.context;
        this.sharedPreferencesContentMap = builder.sharedPreferences;
        this.fileProviderAuthority = builder.fileProviderAuthority;
        this.windowSubTitle = builder.windowSubTitle;
        this.windowTitleTextColor = builder.windowTitleTextColor;
        this.windowSubTitleTextColor = builder.windowSubTitleTextColor;
        this.theme = builder.theme;
        this.windowTitle = builder.windowTitle;
        this.message = builder.message;
        this.contentErrorMessage = builder.contentErrorMessage;
        this.feedbackContentHint = builder.feedbackContentHint;
        this.screenshotHint = builder.screenshotHint;
        this.header = builder.header;
        this.includeLogsText = builder.includeLogsText;
        this.includeScreenshotText = builder.includeScreenshotText;
        this.touchToPreviewScreenshotText = builder.touchToPreviewScreenshotText;
        this.extraLayout = builder.extraLayout;
        this.maoniWorkingDir = builder.maoniWorkingDir;
        this.showKeyboardOnStart = builder.showKeyboardOnStart;
        this.screenCapturingFeatureEnabled = builder.screenCapturingFeatureEnabled;
        this.logsCapturingFeatureEnabled = builder.logsCapturingFeatureEnabled;
    }

    /**
     * Start the Maoni Activity
     *
     * @param callerActivity the caller activity
     */
    public void start(@Nullable final Activity callerActivity) {

        if (mUsed.getAndSet(true)) {
            this.clear();
            throw new UnsupportedOperationException(
                    "Maoni instance cannot be reused to start a new activity. " +
                            "Please build a new Maoni instance.");
        }

        if (callerActivity == null) {
            Log.d(LOG_TAG, "Target activity is undefined");
            return;
        }

        final Intent maoniIntent = new Intent(callerActivity, MaoniActivity.class);

        //Set app-related info
        final PackageManager packageManager = callerActivity.getPackageManager();
        try {
            if (packageManager != null) {
                final PackageInfo packageInfo = packageManager
                        .getPackageInfo(callerActivity.getPackageName(), 0);
                if (packageInfo != null) {
                    maoniIntent.putExtra(APPLICATION_INFO_VERSION_CODE, packageInfo.versionCode);
                    maoniIntent.putExtra(APPLICATION_INFO_VERSION_NAME, packageInfo.versionName);
                    maoniIntent.putExtra(APPLICATION_INFO_PACKAGE_NAME, packageInfo.packageName);
                }
            }
        } catch (final PackageManager.NameNotFoundException nnfe) {
            //No worries
            nnfe.printStackTrace();
        }
        final Object buildConfigDebugValue = ContextUtils.getBuildConfigValue(callerActivity,
                DEBUG);
        if (buildConfigDebugValue instanceof Boolean) {
            maoniIntent.putExtra(APPLICATION_INFO_BUILD_CONFIG_DEBUG,
                    (Boolean) buildConfigDebugValue);
        }
        final Object buildConfigFlavorValue = ContextUtils.getBuildConfigValue(callerActivity,
                FLAVOR);
        if (buildConfigFlavorValue != null) {
            maoniIntent.putExtra(APPLICATION_INFO_BUILD_CONFIG_FLAVOR,
                    buildConfigFlavorValue.toString());
        }
        final Object buildConfigBuildTypeValue = ContextUtils.getBuildConfigValue(callerActivity,
                BUILD_TYPE);
        if (buildConfigBuildTypeValue != null) {
            maoniIntent.putExtra(APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE,
                    buildConfigBuildTypeValue.toString());
        }

        maoniIntent.putExtra(FILE_PROVIDER_AUTHORITY, fileProviderAuthority);

        maoniIntent.putExtra(SHOW_KEYBOARD_ON_START, showKeyboardOnStart);

        maoniIntent.putExtra(WORKING_DIR,
                maoniWorkingDir != null ?
                        maoniWorkingDir.getAbsolutePath() : callerActivity.getCacheDir().getAbsolutePath());

        maoniIntent.putExtra(SCREEN_CAPTURING_FEATURE_ENABLED, screenCapturingFeatureEnabled);
        if (this.screenCapturingFeatureEnabled) {
            //Create screenshot file
            final File screenshotFile = new File(maoniWorkingDir != null ? maoniWorkingDir : callerActivity.getCacheDir(),
                MAONI_FEEDBACK_SCREENSHOT_FILENAME);
            ViewUtils.exportViewToFile(callerActivity, callerActivity.getWindow().getDecorView(),
                screenshotFile);
            maoniIntent.putExtra(SCREENSHOT_FILE, screenshotFile.getAbsolutePath());

            if (screenshotHint != null) {
                maoniIntent.putExtra(SCREENSHOT_HINT, screenshotHint);
            }

            if (includeScreenshotText != null) {
                maoniIntent.putExtra(INCLUDE_SCREENSHOT_TEXT, includeScreenshotText);
            }

            if (touchToPreviewScreenshotText != null) {
                maoniIntent.putExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT, touchToPreviewScreenshotText);
            }
        }

        maoniIntent.putExtra(CALLER_ACTIVITY, callerActivity.getClass().getCanonicalName());

        if (theme != null) {
            maoniIntent.putExtra(THEME, theme);
        }

        if (windowTitle != null) {
            maoniIntent.putExtra(WINDOW_TITLE, windowTitle);
        }

        if (windowSubTitle != null) {
            maoniIntent.putExtra(WINDOW_SUBTITLE, windowSubTitle);
        }

        if (windowTitleTextColor != null) {
            maoniIntent.putExtra(TOOLBAR_TITLE_TEXT_COLOR, windowTitleTextColor);
        }

        if (windowSubTitleTextColor != null) {
            maoniIntent.putExtra(TOOLBAR_SUBTITLE_TEXT_COLOR, windowSubTitleTextColor);
        }

        if (message != null) {
            maoniIntent.putExtra(MESSAGE, message);
        }

        if (header != null) {
            maoniIntent.putExtra(HEADER, header);
        }

        if (extraLayout != null) {
            maoniIntent.putExtra(EXTRA_LAYOUT, extraLayout);
        }

        if (feedbackContentHint != null) {
            maoniIntent.putExtra(CONTENT_HINT, feedbackContentHint);
        }

        if (contentErrorMessage != null) {
            maoniIntent.putExtra(CONTENT_ERROR_TEXT, contentErrorMessage);
        }

        maoniIntent.putExtra(LOGS_CAPTURING_FEATURE_ENABLED, logsCapturingFeatureEnabled);
        if (logsCapturingFeatureEnabled) {
            if (includeLogsText != null) {
                maoniIntent.putExtra(INCLUDE_LOGS_TEXT, includeLogsText);
            }
        }

        maoniIntent.putExtra(SHARED_PREFERENCES, sharedPreferencesContentMap);

        callerActivity.startActivity(maoniIntent);
    }


    public Maoni unregisterListener() {
        getInstance(context).setListener(null);
        return this;
    }

    public Maoni unregisterUiListener() {
        getInstance(context).setUiListener(null);
        return this;
    }

    public Maoni unregisterValidator() {
        getInstance(context).setValidator(null);
        return this;
    }

    public Maoni unregisterHandler() {
        return this.unregisterListener()
                .unregisterUiListener()
                .unregisterValidator();
    }

    public Maoni clear() {
        return this.unregisterHandler();
    }

    /**
     * Maoni Builder
     */
    public static class Builder {

        @Nullable
        private final Context context;

        @Nullable
        private final String fileProviderAuthority;
        @StyleRes
        @Nullable
        public Integer theme;
        @Nullable
        private File maoniWorkingDir;
        @Nullable
        private CharSequence windowTitle;
        @Nullable
        private CharSequence windowSubTitle;
        @ColorRes
        @Nullable
        private Integer windowTitleTextColor;
        @ColorRes
        @Nullable
        private Integer windowSubTitleTextColor;
        @Nullable
        private CharSequence message;
        @Nullable
        private CharSequence contentErrorMessage;
        @Nullable
        private CharSequence feedbackContentHint;
        @Nullable
        private CharSequence screenshotHint;
        @DrawableRes
        @Nullable
        private Integer header;
        @Nullable
        private CharSequence includeScreenshotText;
        @Nullable
        private CharSequence includeLogsText;
        @Nullable
        private CharSequence touchToPreviewScreenshotText;
        @LayoutRes
        @Nullable
        private Integer extraLayout;

        private boolean showKeyboardOnStart;
        private boolean screenCapturingFeatureEnabled = true;
        private boolean logsCapturingFeatureEnabled = true;

        @NonNull
        private HashMap<String, Object> sharedPreferences = new HashMap<>();

        /**
         * Constructor
         *
         * @param fileProviderAuthority the file provider authority.
         *                              If {@literal null}, screenshot file sharing will not be available
         */
        public Builder(@Nullable final String fileProviderAuthority) {
            this(null, fileProviderAuthority);
        }

        /**
         * Constructor
         *
         * @param context the context
         * @param fileProviderAuthority the file provider authority.
         *                              If {@literal null}, screenshot file sharing will not be available
         */
        public Builder(@Nullable final Context context, @Nullable final String fileProviderAuthority) {
            this.fileProviderAuthority = fileProviderAuthority;
            this.context = context;
        }

        public Builder withMaoniWorkingDir(@Nullable File maoniWorkingDir) {
            this.maoniWorkingDir = maoniWorkingDir;
            return this;
        }

        public Builder withTheme(@StyleRes @Nullable Integer theme) {
            this.theme = theme;
            return this;
        }

        public Builder withWindowTitle(@Nullable CharSequence windowTitle) {
            this.windowTitle = windowTitle;
            return this;
        }

        public Builder withWindowSubTitle(@Nullable CharSequence windowSubTitle) {
            this.windowSubTitle = windowSubTitle;
            return this;
        }

        public Builder withWindowTitleTextColor(@ColorRes @Nullable Integer windowTitleTextColor) {
            this.windowTitleTextColor = windowTitleTextColor;
            return this;
        }

        public Builder withWindowSubTitleTextColor(@ColorRes @Nullable Integer windowSubTitleTextColor) {
            this.windowSubTitleTextColor = windowSubTitleTextColor;
            return this;
        }

        public Builder withExtraLayout(@LayoutRes @Nullable Integer extraLayout) {
            this.extraLayout = extraLayout;
            return this;
        }

        public Builder withFeedbackContentHint(@Nullable CharSequence feedbackContentHint) {
            this.feedbackContentHint = feedbackContentHint;
            return this;
        }

        public Builder withIncludeLogsText(@Nullable CharSequence includeLogsText) {
            this.includeLogsText = includeLogsText;
            return this;
        }

        public Builder withIncludeScreenshotText(@Nullable CharSequence includeScreenshotText) {
            this.includeScreenshotText = includeScreenshotText;
            return this;
        }

        public CharSequence getTouchToPreviewScreenshotText() {
            return touchToPreviewScreenshotText;
        }

        public Builder withTouchToPreviewScreenshotText(@Nullable CharSequence touchToPreviewScreenshotText) {
            this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
            return this;
        }

        public Builder withMessage(@Nullable CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder withContentErrorMessage(@Nullable CharSequence contentErrorMessage) {
            this.contentErrorMessage = contentErrorMessage;
            return this;
        }

        public Builder withHeader(@Nullable Integer header) {
            this.header = header;
            return this;
        }

        public Builder showKeyboardOnStart(final boolean showKeyboardOnStart) {
            this.showKeyboardOnStart = showKeyboardOnStart;
            return this;
        }

        public Builder showKeyboardOnStart() {
            return this.showKeyboardOnStart(true);
        }

        public Builder hideKeyboardOnStart() {
            return this.showKeyboardOnStart(false);
        }

        public Builder disableScreenCapturingFeature() {
            this.screenCapturingFeatureEnabled = false;
            return this;
        }

        public Builder enableScreenCapturingFeature() {
            this.screenCapturingFeatureEnabled = true;
            return this;
        }

        public Builder withScreenCapturingFeature(final boolean screenCapturingFeature) {
            if (screenCapturingFeature) {
                this.enableScreenCapturingFeature();
            } else {
                this.disableScreenCapturingFeature();
            }
            return this;
        }

        public Builder disableLogsCapturingFeature() {
            this.logsCapturingFeatureEnabled = false;
            return this;
        }

        public Builder enableLogsCapturingFeature() {
            this.logsCapturingFeatureEnabled = true;
            return this;
        }

        public Builder withLogsCapturingFeature(final boolean logsCapturingFeature) {
            if (logsCapturingFeature) {
                this.enableLogsCapturingFeature();
            } else {
                this.disableLogsCapturingFeature();
            }
            return this;
        }

        public Builder disableCapturingFeature() {
            this.disableLogsCapturingFeature();
            this.disableScreenCapturingFeature();
            return this;
        }

        public Builder enableCapturingFeature() {
            this.enableLogsCapturingFeature();
            this.enableScreenCapturingFeature();
            return this;
        }

        public Builder withCapturingFeature(final boolean capturingFeature) {
            if (capturingFeature) {
                this.enableCapturingFeature();
            } else {
                this.disableCapturingFeature();
            }
            return this;
        }

        public Builder withScreenshotHint(@Nullable CharSequence screenshotHint) {
            this.screenshotHint = screenshotHint;
            return this;
        }

        public Builder withValidator(@Nullable final Validator validator) {
            getInstance(context).setValidator(validator);
            return this;
        }

        public Builder withListener(@Nullable final Listener listener) {
            getInstance(context).setListener(listener);
            return this;
        }

        public Builder withUiListener(@Nullable final UiListener uiListener) {
            getInstance(context).setUiListener(uiListener);
            return this;
        }

        public Builder withHandler(@Nullable final Handler handler) {
            return this
                    .withListener(handler)
                    .withValidator(handler)
                    .withUiListener(handler);
        }

        /**
         * If no listener is set (i.e no call to {@link #withListener(Listener)}),
         * {@link MaoniEmailListener} is used by default (provided {@link #context} is non-null).
         * <p>
         * This allows to configure the 'to' email addresses to use for the default listener.
         * If {@link #withListener(Listener)} is called explicitly, then this
         * method will have no effect at all.
         * @param toAddresses the 'to' addresses
         * @return this Builder
         */
        @SuppressLint("ApplySharedPref")
        public Builder withDefaultToEmailAddress(@Nullable final String... toAddresses) {
            if (context != null && toAddresses != null) {
                context.getSharedPreferences(
                    Maoni.class.getPackage().getName(),
                    Context.MODE_PRIVATE)
                    .edit()
                    .putStringSet(DEFAULT_LISTENER_EMAIL_TO,
                        new HashSet<>(Arrays.asList(toAddresses)))
                    .commit();
            }
            return this;
        }

        /**
         * Include SharedPreferences value map.
         * <p>
         * SharedPreferences files are opened with the default operating mode.
         * @param sharedPreferences the names of each {@link android.content.SharedPreferences} file
         * @return this Builder
         */
        public Builder withSharedPreferences(@Nullable final String... sharedPreferences) {
            return this.withSharedPreferences(Context.MODE_PRIVATE, sharedPreferences);
        }

        /**
         * Include SharedPreferences value map.
         * @param mode Operating mode.  Use 0 or {@link Context#MODE_PRIVATE} for the
         * default operation.
         * @param sharedPreferences the names of each {@link android.content.SharedPreferences} file
         * @return this Builder
         */
        public Builder withSharedPreferences(final int mode, @Nullable final String... sharedPreferences) {
            if (sharedPreferences == null) {
                return this;
            }
            final Map<String, Integer> sharedPreferencesModeMap = new HashMap<>();
            for (final String sharedPreference : sharedPreferences) {
                if (sharedPreference == null) {
                    continue;
                }
                sharedPreferencesModeMap.put(sharedPreference, mode);
            }
            return this.withSharedPreferences(sharedPreferencesModeMap);
        }

        /**
         * Include SharedPreferences value map.
         * @param sharedPreferencesModeMap the map of {@link android.content.SharedPreferences} and their operating mode
         * @return this builder
         */
        public Builder withSharedPreferences(@Nullable final Map<String, Integer> sharedPreferencesModeMap) {
            if (sharedPreferencesModeMap == null || sharedPreferencesModeMap.isEmpty()) {
                return this;
            }
            if (this.context == null) {
                throw new IllegalArgumentException("A context is needed to load the shared preferences");
            }
            for (final Entry<String, Integer> entry : sharedPreferencesModeMap.entrySet()) {
                final String sharedPreference = entry.getKey();
                final Integer sharedPreferenceMode = entry.getValue();
                if (sharedPreference == null || sharedPreferenceMode == null) {
                    continue;
                }
                final Map<String, ?> sharedPreferencesContent = 
                    this.context.getSharedPreferences(sharedPreference, sharedPreferenceMode).getAll();
                for (final Entry<String, ?> sharedPreferencesContentEntry : sharedPreferencesContent.entrySet()) {
                    this.sharedPreferences.put(
                        "SharedPreferences/" + sharedPreference + "/" + sharedPreferencesContentEntry.getKey(),
                        sharedPreferencesContentEntry.getValue());
                }
            }
            return this;
        }

        public Maoni build() {
            return new Maoni(this);
        }
    }

    /**
     * Callbacks Configuration for Maoni
     */
    public static class CallbacksConfiguration {

        @Nullable
        private static CallbacksConfiguration SINGLETON = null;

        @Nullable
        private Validator validator;

        @Nullable
        private Listener listener;

        @Nullable
        private UiListener uiListener;

        private CallbacksConfiguration(@Nullable final Context context) {
            //Default listener comes from maoni-email
            if (context != null) {
                final Set<String> defaultToAddresses =
                    context.getSharedPreferences(Maoni.class.getPackage().getName(),
                        Context.MODE_PRIVATE)
                        .getStringSet(DEFAULT_LISTENER_EMAIL_TO, new HashSet<String>());
                this.listener =
                    new MaoniEmailListener(context,
                        defaultToAddresses.toArray(new String[defaultToAddresses.size()]));
            } else {
                Log.d(LOG_TAG, "context is NULL => no default listener configured");
            }
        }

        @NonNull
        public static CallbacksConfiguration getInstance(@Nullable final Context context) {
            if (SINGLETON == null) {
                SINGLETON = new CallbacksConfiguration(context);
            }
            return SINGLETON;
        }

        @Nullable
        public Validator getValidator() {
            return validator;
        }

        @NonNull
        public CallbacksConfiguration setValidator(@Nullable final Validator validator) {
            this.validator = validator;
            return this;
        }

        @Nullable
        public Listener getListener() {
            return listener;
        }

        @NonNull
        public CallbacksConfiguration setListener(@Nullable final Listener listener) {
            this.listener = listener;
            return this;
        }

        @Nullable
        public UiListener getUiListener() {
            return uiListener;
        }

        @NonNull
        public CallbacksConfiguration setUiListener(@Nullable final UiListener uiListener) {
            this.uiListener = uiListener;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public CallbacksConfiguration reset() {
            return this.setUiListener(null)
                    .setListener(null)
                    .setValidator(null);
        }
    }

}
