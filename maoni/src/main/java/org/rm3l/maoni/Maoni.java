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
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
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
    private boolean showKeyboardOnStart;
    private boolean screenCapturingFeatureEnabled = true;
    private boolean logsCapturingFeatureEnabled = true;

    private final AtomicBoolean mUsed = new AtomicBoolean(false);

    /**
     * Constructor
     * @param fileProviderAuthority        the file provider authority.
     *                                     If {@literal null}, file sharing will not be available
     * @param maoniWorkingDir                the working directory for Maoni.
     *                                       Will default to the caller activity cache directory if none was specified.
     *                                       This is where screenshots are typically stored.
     * @param windowTitle                  the feedback window title
     * @param windowSubTitle                the feedback window sub-title
     * @param windowTitleTextColor          the feedback window title text color
     *                                      (use {@literal null} for the default)
     * @param windowSubTitleTextColor       the feedback window sub-title text color
     *                                      (use {@literal null} for the default)
     * @param theme                        the theme to apply
     * @param header                       the header image
     * @param message                      the feedback form field error message to display to the user
     * @param feedbackContentHint          the feedback form field hint message
     * @param contentErrorMessage          the feedback form field error message to display to the user
     * @param extraLayout                  the extra layout resource.
     * @param includeLogsText              the text do display next to the "Include logs" checkbox
     * @param includeScreenshotText        the text do display next to the "Include screenshot" checkbox
     * @param touchToPreviewScreenshotText the "Touch to preview" text
     * @param screenshotHint               the text to display to the user
     */
    public Maoni(
        @Nullable final Context context,
        @Nullable String fileProviderAuthority,
        @Nullable final File maoniWorkingDir,
        @Nullable final CharSequence windowTitle,
        @Nullable final CharSequence windowSubTitle,
        @ColorRes @Nullable final Integer windowTitleTextColor,
        @ColorRes @Nullable final Integer windowSubTitleTextColor,
        @StyleRes @Nullable final Integer theme,
        @DrawableRes @Nullable final Integer header,
        @Nullable final CharSequence message,
        @Nullable final CharSequence feedbackContentHint,
        @Nullable final CharSequence contentErrorMessage,
        @LayoutRes @Nullable final Integer extraLayout,
        @Nullable final CharSequence includeLogsText,
        @Nullable final CharSequence includeScreenshotText,
        @Nullable final CharSequence touchToPreviewScreenshotText,
        @Nullable final CharSequence screenshotHint) {
        this(context, fileProviderAuthority, maoniWorkingDir, windowTitle,
            windowSubTitle, windowTitleTextColor, windowSubTitleTextColor,
            theme, header, message, feedbackContentHint, contentErrorMessage,
            extraLayout, includeLogsText, includeScreenshotText, touchToPreviewScreenshotText,
            screenshotHint, false);
    }

    /**
     * Constructor
     * @param fileProviderAuthority        the file provider authority.
     *                                     If {@literal null}, file sharing will not be available
     * @param maoniWorkingDir                the working directory for Maoni.
     *                                       Will default to the caller activity cache directory if none was specified.
     *                                       This is where screenshots are typically stored.
     * @param windowTitle                  the feedback window title
     * @param windowSubTitle                the feedback window sub-title
     * @param windowTitleTextColor          the feedback window title text color
     *                                      (use {@literal null} for the default)
     * @param windowSubTitleTextColor       the feedback window sub-title text color
     *                                      (use {@literal null} for the default)
     * @param theme                        the theme to apply
     * @param header                       the header image
     * @param message                      the feedback form field error message to display to the user
     * @param feedbackContentHint          the feedback form field hint message
     * @param contentErrorMessage          the feedback form field error message to display to the user
     * @param extraLayout                  the extra layout resource.
     * @param includeLogsText              the text do display next to the "Include logs" checkbox
     * @param includeScreenshotText        the text do display next to the "Include screenshot" checkbox
     * @param touchToPreviewScreenshotText the "Touch to preview" text
     * @param screenshotHint               the text to display to the user
     * @param showKeyboardOnStart          whether to show the keyboard on start or not. Default is {@code false}
     */
    public Maoni(
            @Nullable final Context context,
            @Nullable String fileProviderAuthority,
            @Nullable final File maoniWorkingDir,
            @Nullable final CharSequence windowTitle,
            @Nullable final CharSequence windowSubTitle,
            @ColorRes @Nullable final Integer windowTitleTextColor,
            @ColorRes @Nullable final Integer windowSubTitleTextColor,
            @StyleRes @Nullable final Integer theme,
            @DrawableRes @Nullable final Integer header,
            @Nullable final CharSequence message,
            @Nullable final CharSequence feedbackContentHint,
            @Nullable final CharSequence contentErrorMessage,
            @LayoutRes @Nullable final Integer extraLayout,
            @Nullable final CharSequence includeLogsText,
            @Nullable final CharSequence includeScreenshotText,
            @Nullable final CharSequence touchToPreviewScreenshotText,
            @Nullable final CharSequence screenshotHint,
            final boolean showKeyboardOnStart) {
        this(context, fileProviderAuthority, maoniWorkingDir, windowTitle,
            windowSubTitle, windowTitleTextColor, windowSubTitleTextColor,
            theme, header, message, feedbackContentHint, contentErrorMessage,
            extraLayout, includeLogsText, includeScreenshotText, touchToPreviewScreenshotText,
            screenshotHint, showKeyboardOnStart, true, true);
    }

    /**
     * Constructor
     * @param fileProviderAuthority        the file provider authority.
     *                                     If {@literal null}, file sharing will not be available
     * @param maoniWorkingDir                the working directory for Maoni.
     *                                       Will default to the caller activity cache directory if none was specified.
     *                                       This is where screenshots are typically stored.
     * @param windowTitle                  the feedback window title
     * @param windowSubTitle                the feedback window sub-title
     * @param windowTitleTextColor          the feedback window title text color
     *                                      (use {@literal null} for the default)
     * @param windowSubTitleTextColor       the feedback window sub-title text color
     *                                      (use {@literal null} for the default)
     * @param theme                        the theme to apply
     * @param header                       the header image
     * @param message                      the feedback form field error message to display to the user
     * @param feedbackContentHint          the feedback form field hint message
     * @param contentErrorMessage          the feedback form field error message to display to the user
     * @param extraLayout                  the extra layout resource.
     * @param includeLogsText              the text do display next to the "Include logs" checkbox
     * @param includeScreenshotText        the text do display next to the "Include screenshot" checkbox
     * @param touchToPreviewScreenshotText the "Touch to preview" text
     * @param screenshotHint               the text to display to the user
     * @param showKeyboardOnStart          whether to show the keyboard on start or not. Default is {@code false}
     * @param screenCapturingFeatureEnabled whether to enable screen capturing or not. Default is {@code true}
     * @param logsCapturingFeatureEnabled whether to enable logs capturing or not. Default is {@code true}
     */
    public Maoni(
        @Nullable final Context context,
        @Nullable String fileProviderAuthority,
        @Nullable final File maoniWorkingDir,
        @Nullable final CharSequence windowTitle,
        @Nullable final CharSequence windowSubTitle,
        @ColorRes @Nullable final Integer windowTitleTextColor,
        @ColorRes @Nullable final Integer windowSubTitleTextColor,
        @StyleRes @Nullable final Integer theme,
        @DrawableRes @Nullable final Integer header,
        @Nullable final CharSequence message,
        @Nullable final CharSequence feedbackContentHint,
        @Nullable final CharSequence contentErrorMessage,
        @LayoutRes @Nullable final Integer extraLayout,
        @Nullable final CharSequence includeLogsText,
        @Nullable final CharSequence includeScreenshotText,
        @Nullable final CharSequence touchToPreviewScreenshotText,
        @Nullable final CharSequence screenshotHint,
        final boolean showKeyboardOnStart,
        final boolean screenCapturingFeatureEnabled,
        final boolean logsCapturingFeatureEnabled) {

        this.context = context;
        this.fileProviderAuthority = fileProviderAuthority;
        this.windowSubTitle = windowSubTitle;
        this.windowTitleTextColor = windowTitleTextColor;
        this.windowSubTitleTextColor = windowSubTitleTextColor;
        this.theme = theme;
        this.windowTitle = windowTitle;
        this.message = message;
        this.contentErrorMessage = contentErrorMessage;
        this.feedbackContentHint = feedbackContentHint;
        this.screenshotHint = screenshotHint;
        this.header = header;
        this.includeLogsText = includeLogsText;
        this.includeScreenshotText = includeScreenshotText;
        this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
        this.extraLayout = extraLayout;
        this.maoniWorkingDir = maoniWorkingDir;
        this.showKeyboardOnStart = showKeyboardOnStart;
        this.screenCapturingFeatureEnabled = screenCapturingFeatureEnabled;
        this.logsCapturingFeatureEnabled = logsCapturingFeatureEnabled;
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
        if (buildConfigDebugValue != null && buildConfigDebugValue instanceof Boolean) {
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
                        maoniWorkingDir : callerActivity.getCacheDir().getAbsolutePath());

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

        @Nullable
        public File getMaoniWorkingDir() {
            return maoniWorkingDir;
        }

        public Builder withMaoniWorkingDir(@Nullable File maoniWorkingDir) {
            this.maoniWorkingDir = maoniWorkingDir;
            return this;
        }

        @Nullable
        public Integer getTheme() {
            return theme;
        }

        public Builder withTheme(@StyleRes @Nullable Integer theme) {
            this.theme = theme;
            return this;
        }

        @Nullable
        public CharSequence getWindowTitle() {
            return windowTitle;
        }

        public Builder withWindowTitle(@Nullable CharSequence windowTitle) {
            this.windowTitle = windowTitle;
            return this;
        }

        @Nullable
        public CharSequence getWindowSubTitle() {
            return windowSubTitle;
        }

        public Builder withWindowSubTitle(@Nullable CharSequence windowSubTitle) {
            this.windowSubTitle = windowSubTitle;
            return this;
        }

        @Nullable
        public Integer getWindowTitleTextColor() {
            return windowTitleTextColor;
        }

        public Builder withWindowTitleTextColor(@ColorRes @Nullable Integer windowTitleTextColor) {
            this.windowTitleTextColor = windowTitleTextColor;
            return this;
        }

        @Nullable
        public Integer getWindowSubTitleTextColor() {
            return windowSubTitleTextColor;
        }

        public Builder withWindowSubTitleTextColor(@ColorRes @Nullable Integer windowSubTitleTextColor) {
            this.windowSubTitleTextColor = windowSubTitleTextColor;
            return this;
        }

        @Nullable
        public Integer getExtraLayout() {
            return extraLayout;
        }

        public Builder withExtraLayout(@LayoutRes @Nullable Integer extraLayout) {
            this.extraLayout = extraLayout;
            return this;
        }

        @Nullable
        public CharSequence getFeedbackContentHint() {
            return feedbackContentHint;
        }

        public Builder withFeedbackContentHint(@Nullable CharSequence feedbackContentHint) {
            this.feedbackContentHint = feedbackContentHint;
            return this;
        }

        @Nullable
        public CharSequence getIncludeLogText() {
            return includeLogsText;
        }

        public Builder withIncludeLogsText(@Nullable CharSequence includeLogsText) {
            this.includeLogsText = includeLogsText;
            return this;
        }

        @Nullable
        public CharSequence getIncludeScreenshotText() {
            return includeScreenshotText;
        }

        public Builder withIncludeScreenshotText(@Nullable CharSequence includeScreenshotText) {
            this.includeScreenshotText = includeScreenshotText;
            return this;
        }

        @Nullable
        public CharSequence getTouchToPreviewScreenshotText() {
            return touchToPreviewScreenshotText;
        }

        public Builder withTouchToPreviewScreenshotText(@Nullable CharSequence touchToPreviewScreenshotText) {
            this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
            return this;
        }

        @Nullable
        public CharSequence getMessage() {
            return message;
        }

        public Builder withMessage(@Nullable CharSequence message) {
            this.message = message;
            return this;
        }

        @Nullable
        public CharSequence getContentErrorMessage() {
            return contentErrorMessage;
        }

        public Builder withContentErrorMessage(@Nullable CharSequence contentErrorMessage) {
            this.contentErrorMessage = contentErrorMessage;
            return this;
        }

        @DrawableRes
        @Nullable
        public Integer getHeader() {
            return header;
        }

        public Builder withHeader(@Nullable Integer header) {
            this.header = header;
            return this;
        }

        public boolean isShowKeyboardOnStart() {
            return showKeyboardOnStart;
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

        @Nullable
        public CharSequence getScreenshotHint() {
            return screenshotHint;
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



        public Maoni build() {
            return new Maoni(
                    context,
                    fileProviderAuthority,
                    maoniWorkingDir,
                    windowTitle,
                    windowSubTitle,
                    windowTitleTextColor,
                    windowSubTitleTextColor,
                    theme,
                    header,
                    message,
                    feedbackContentHint,
                    contentErrorMessage,
                    extraLayout,
                    includeLogsText,
                    includeScreenshotText,
                    touchToPreviewScreenshotText,
                    screenshotHint,
                    showKeyboardOnStart,
                    screenCapturingFeatureEnabled,
                    logsCapturingFeatureEnabled);
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

        public CallbacksConfiguration reset() {
            return this.setUiListener(null)
                    .setListener(null)
                    .setValidator(null);
        }
    }

}
