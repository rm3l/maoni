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

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import org.rm3l.maoni.common.contract.Handler;
import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.contract.UiListener;
import org.rm3l.maoni.common.contract.Validator;
import org.rm3l.maoni.ui.MaoniActivity;
import org.rm3l.maoni.utils.ViewUtils;

import java.io.File;

import static org.rm3l.maoni.Maoni.CallbacksConfiguration.getInstance;
import static org.rm3l.maoni.ui.MaoniActivity.CALLER_ACTIVITY;
import static org.rm3l.maoni.ui.MaoniActivity.CONTENT_ERROR_TEXT;
import static org.rm3l.maoni.ui.MaoniActivity.CONTENT_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.EXTRA_LAYOUT;
import static org.rm3l.maoni.ui.MaoniActivity.FILE_PROVIDER_AUTHORITY;
import static org.rm3l.maoni.ui.MaoniActivity.HEADER;
import static org.rm3l.maoni.ui.MaoniActivity.INCLUDE_SCREENSHOT_TEXT;
import static org.rm3l.maoni.ui.MaoniActivity.MESSAGE;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_FILE;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.SCREENSHOT_TOUCH_TO_PREVIEW_HINT;
import static org.rm3l.maoni.ui.MaoniActivity.THEME;
import static org.rm3l.maoni.ui.MaoniActivity.TOOLBAR_SUBTITLE_TEXT_COLOR;
import static org.rm3l.maoni.ui.MaoniActivity.TOOLBAR_TITLE_TEXT_COLOR;
import static org.rm3l.maoni.ui.MaoniActivity.WINDOW_SUBTITLE;
import static org.rm3l.maoni.ui.MaoniActivity.WINDOW_TITLE;

/**
 * Maoni configuration
 */
public class Maoni {

    public static final String MAONI_FEEDBACK_SCREENSHOT_FILENAME = "maoni_feedback_screenshot.png";
    private static final String LOG_TAG = Maoni.class.getSimpleName();
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
    private File maoniWorkingDir;

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
     * @param includeScreenshotText        the text do display next to the "Include screenshot" checkbox
     * @param touchToPreviewScreenshotText the "Touch to preview" text
     * @param screenshotHint               the text to display to the user
     */
    public Maoni(
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
            @Nullable final CharSequence includeScreenshotText,
            @Nullable final CharSequence touchToPreviewScreenshotText,
            @Nullable final CharSequence screenshotHint) {

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
        this.includeScreenshotText = includeScreenshotText;
        this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
        this.extraLayout = extraLayout;
        this.maoniWorkingDir = maoniWorkingDir;
    }

    /**
     * Start the Maoni Activity
     *
     * @param callerActivity the caller activity
     */
    public void start(@Nullable final Activity callerActivity) {
        if (callerActivity == null) {
            Log.d(LOG_TAG, "Target activity is undefined");
            return;
        }

        final Intent maoniIntent = new Intent(callerActivity, MaoniActivity.class);

        maoniIntent.putExtra(FILE_PROVIDER_AUTHORITY, fileProviderAuthority);

        //Create screenshot file
        final File screenshotFile = new File(
                maoniWorkingDir != null ? maoniWorkingDir : callerActivity.getCacheDir(),
                MAONI_FEEDBACK_SCREENSHOT_FILENAME);
        ViewUtils.exportViewToFile(callerActivity,
                callerActivity.getWindow().getDecorView(), screenshotFile);
        maoniIntent.putExtra(SCREENSHOT_FILE, screenshotFile.getAbsolutePath());

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

        if (screenshotHint != null) {
            maoniIntent.putExtra(SCREENSHOT_HINT, screenshotHint);
        }

        if (includeScreenshotText != null) {
            maoniIntent.putExtra(INCLUDE_SCREENSHOT_TEXT, includeScreenshotText);
        }

        if (touchToPreviewScreenshotText != null) {
            maoniIntent.putExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT, touchToPreviewScreenshotText);
        }

        callerActivity.startActivity(maoniIntent);
    }

    /**
     * Maoni Builder
     */
    public static class Builder {

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
        private CharSequence touchToPreviewScreenshotText;
        @LayoutRes
        @Nullable
        private Integer extraLayout;

        /**
         * Constructor
         *
         * @param fileProviderAuthority the file provider authority.
         *                              If {@literal null}, screenshot file sharing will not be available
         */
        public Builder(@Nullable final String fileProviderAuthority) {
            this.fileProviderAuthority = fileProviderAuthority;
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

        @Nullable
        public CharSequence getScreenshotHint() {
            return screenshotHint;
        }

        public Builder withScreenshotHint(@Nullable CharSequence screenshotHint) {
            this.screenshotHint = screenshotHint;
            return this;
        }

        public Builder withValidator(@Nullable final Validator validator) {
            getInstance().setValidator(validator);
            return this;
        }

        public Builder withListener(@Nullable final Listener listener) {
            getInstance().setListener(listener);
            return this;
        }

        public Builder withUiListener(@Nullable final UiListener uiListener) {
            getInstance().setUiListener(uiListener);
            return this;
        }

        public Builder withHandler(@Nullable final Handler handler) {
            return this
                    .withListener(handler)
                    .withValidator(handler)
                    .withUiListener(handler);
        }

        public Maoni build() {
            return new Maoni(
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
                    includeScreenshotText,
                    touchToPreviewScreenshotText,
                    screenshotHint);
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

        private CallbacksConfiguration() {
        }

        @NonNull
        public static CallbacksConfiguration getInstance() {
            if (SINGLETON == null) {
                SINGLETON = new CallbacksConfiguration();
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
    }

}
