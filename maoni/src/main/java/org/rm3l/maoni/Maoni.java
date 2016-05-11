package org.rm3l.maoni;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import org.rm3l.maoni.contract.Validator;
import org.rm3l.maoni.ui.MaoniActivity;
import org.rm3l.maoni.utils.ViewUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * TODO Created by rm3l on 05/05/16.
 */
public class Maoni<T extends MaoniActivity> {

    private static final String LOG_TAG = Maoni.class.getSimpleName();
    private final Class<T> mConcreteActivityType;
    @Nullable
    private String windowTitle;
    @Nullable
    private String message;
    private boolean withEmailField;
    private boolean closeOnCallbackError;
    @Nullable
    private Validator validator;
    private String screenshotHint;

    public Maoni(@Nullable final Class<T> concreteActivityType) {
        this.mConcreteActivityType = concreteActivityType;
    }

    @Nullable
    public String getWindowTitle() {
        return windowTitle;
    }

    public Maoni windowTitle(@Nullable String windowTitle) {
        this.windowTitle = windowTitle;
        return this;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public Maoni message(@Nullable String message) {
        this.message = message;
        return this;
    }

    @Nullable
    public String getScreenshotHint() {
        return screenshotHint;
    }

    public Maoni screenshotHint(@Nullable String screenshotHint) {
        this.screenshotHint = screenshotHint;
        return this;
    }

    public void start(@Nullable final Activity callerActivity) {
        if (callerActivity == null) {
            Log.d(LOG_TAG, "Target activity is undefined");
            return;
        }

        final Intent maoniIntent = new Intent(callerActivity, mConcreteActivityType);
        final File screenshotFile = new File(callerActivity.getCacheDir(), "feedback_screenshot.png");
        ViewUtils.exportViewToFile(callerActivity, callerActivity.getWindow().getDecorView(), screenshotFile);
        maoniIntent.putExtra(MaoniActivity.SCREENSHOT_FILE, screenshotFile.getAbsolutePath());
        maoniIntent.putExtra(MaoniActivity.CALLER_ACTIVITY, callerActivity.getClass().getCanonicalName());
        maoniIntent.putExtra(MaoniActivity.WINDOW_TITLE, getWindowTitle());
        maoniIntent.putExtra(MaoniActivity.MESSAGE, getMessage());
        maoniIntent.putExtra(MaoniActivity.SCREENSHOT_HINT, getScreenshotHint());
        callerActivity.startActivity(maoniIntent);
    }

}
