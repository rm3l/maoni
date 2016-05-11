package org.rm3l.maoni;

import android.app.Activity;
import android.content.Intent;
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
    private String windowTitle;
    @Nullable
    private String message;
    private boolean withEmailField;
    private boolean closeOnCallbackError;
    private String screenshotHint;

    @Nullable
    public String getWindowTitle() {
        return windowTitle;
    }

    public MaoniBuilder windowTitle(@Nullable String windowTitle) {
        this.windowTitle = windowTitle;
        return this;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public MaoniBuilder message(@Nullable String message) {
        this.message = message;
        return this;
    }

    @Nullable
    public String getScreenshotHint() {
        return screenshotHint;
    }

    public MaoniBuilder screenshotHint(@Nullable String screenshotHint) {
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
        MaoniConfiguration.getInstance().setHandler(handler);
        return this;
    }

    public void start(@Nullable final Activity callerActivity) {
        if (callerActivity == null) {
            Log.d(LOG_TAG, "Target activity is undefined");
            return;
        }

        final Intent maoniIntent = new Intent(callerActivity, MaoniActivity.class);
        final File screenshotFile = new File(callerActivity.getCacheDir(), "feedback_screenshot.png");
        ViewUtils.exportViewToFile(callerActivity, callerActivity.getWindow().getDecorView(), screenshotFile);
        maoniIntent.putExtra(MaoniActivity.SCREENSHOT_FILE, screenshotFile.getAbsolutePath());
        maoniIntent.putExtra(MaoniActivity.CALLER_ACTIVITY, callerActivity.getClass().getCanonicalName());
        maoniIntent.putExtra(MaoniActivity.WINDOW_TITLE, getWindowTitle());
        maoniIntent.putExtra(MaoniActivity.MESSAGE, getMessage());
        callerActivity.startActivity(maoniIntent);
    }

}
