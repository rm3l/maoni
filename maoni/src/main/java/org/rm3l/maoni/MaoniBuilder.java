package org.rm3l.maoni;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;

import org.rm3l.maoni.ui.MaoniActivity;
import org.rm3l.maoni.utils.ViewUtils;

import java.io.File;

/**
 * TODO Created by rm3l on 05/05/16.
 */
public class MaoniBuilder {

    private static final String LOG_TAG = MaoniBuilder.class.getSimpleName();

    @Nullable
    private CharSequence windowTitle;

    @Nullable
    private CharSequence message;

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

    @Nullable
    public CharSequence getWindowTitle() {
        return windowTitle;
    }

    public MaoniBuilder windowTitle(@Nullable CharSequence windowTitle) {
        this.windowTitle = windowTitle;
        return this;
    }

    @Nullable
    public CharSequence getFeedbackContentHint() {
        return feedbackContentHint;
    }

    public MaoniBuilder feedbackContentHint(@Nullable CharSequence feedbackContentHint) {
        this.feedbackContentHint = feedbackContentHint;
        return this;
    }
    
    @Nullable
    public CharSequence getIncludeScreenshotText() {
        return includeScreenshotText;
    }

    public MaoniBuilder includeScreenshotText(@Nullable CharSequence includeScreenshotText) {
        this.includeScreenshotText = includeScreenshotText;
        return this;
    }

    @Nullable
    public CharSequence getTouchToPreviewScreenshotText() {
        return touchToPreviewScreenshotText;
    }

    public MaoniBuilder touchToPreviewScreenshotText(@Nullable CharSequence touchToPreviewScreenshotText) {
        this.touchToPreviewScreenshotText = touchToPreviewScreenshotText;
        return this;
    }

    @Nullable
    public CharSequence getMessage() {
        return message;
    }

    public MaoniBuilder message(@Nullable CharSequence message) {
        this.message = message;
        return this;
    }

    @DrawableRes
    @Nullable
    public Integer getHeader() {
        return header;
    }

    public MaoniBuilder header(@Nullable Integer header) {
        this.header = header;
        return this;
    }

    @Nullable
    public CharSequence getScreenshotHint() {
        return screenshotHint;
    }

    public MaoniBuilder screenshotHint(@Nullable CharSequence screenshotHint) {
        this.screenshotHint = screenshotHint;
        return this;
    }

    public MaoniBuilder extraLayout(@LayoutRes @Nullable Integer extraLayout) {
        MaoniConfiguration.getInstance().setExtraLayout(extraLayout);
        return this;
    }

    public MaoniBuilder validator(@Nullable final MaoniConfiguration.Validator validator) {
        MaoniConfiguration.getInstance().setValidator(validator);
        return this;
    }

    public MaoniBuilder listener(@Nullable final MaoniConfiguration.Listener listener) {
        MaoniConfiguration.getInstance().setListener(listener);
        return this;
    }

    public MaoniBuilder uiListener(@Nullable final MaoniConfiguration.UiListener uiListener) {
        MaoniConfiguration.getInstance().setUiListener(uiListener);
        return this;
    }

    public MaoniBuilder handler(@Nullable final MaoniConfiguration.Handler handler) {
        return this
                .listener(handler)
                .validator(handler)
                .uiListener(handler);
    }

    public void start(@Nullable final Activity callerActivity) {
        if (callerActivity == null) {
            Log.d(LOG_TAG, "Target activity is undefined");
            return;
        }

        final Intent maoniIntent = new Intent(callerActivity, MaoniActivity.class);

        //Create screenshot file
        final File screenshotFile = new File(callerActivity.getCacheDir(),
                "maoni_feedback_screenshot.png");
        ViewUtils.exportViewToFile(callerActivity, callerActivity.getWindow().getDecorView(), screenshotFile);
        maoniIntent.putExtra(MaoniActivity.SCREENSHOT_FILE, screenshotFile.getAbsolutePath());

        maoniIntent.putExtra(MaoniActivity.CALLER_ACTIVITY, callerActivity.getClass().getCanonicalName());

        if (message != null) {
            maoniIntent.putExtra(MaoniActivity.MESSAGE, message);
        }

        if (header != null) {
            maoniIntent.putExtra(MaoniActivity.HEADER, header);
        }

        if (feedbackContentHint != null) {
            maoniIntent.putExtra(MaoniActivity.CONTENT_HINT, feedbackContentHint);
        }

        if (screenshotHint != null) {
            maoniIntent.putExtra(MaoniActivity.SCREENSHOT_HINT, screenshotHint);
        }

        if (includeScreenshotText != null) {
            maoniIntent.putExtra(MaoniActivity.INCLUDE_SCREENSHOT_TEXT, includeScreenshotText);
        }

        if (touchToPreviewScreenshotText != null) {
            maoniIntent.putExtra(MaoniActivity.SCREENSHOT_TOUCH_TO_PREVIEW_HINT,
                    touchToPreviewScreenshotText);
        }

        callerActivity.startActivity(maoniIntent);
    }

}
