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
package org.rm3l.maoni.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;
import org.rm3l.maoni.Maoni.CallbacksConfiguration;
import org.rm3l.maoni.R;
import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.contract.UiListener;
import org.rm3l.maoni.common.contract.Validator;
import org.rm3l.maoni.common.model.Feedback;
import org.rm3l.maoni.utils.LogcatUtils;
import org.rm3l.maoni.utils.ViewUtils;

/**
 * Maoni Activity
 */
public class MaoniActivity extends AppCompatActivity {

    private static final String TAG = MaoniActivity.class.getSimpleName();

    public static final String APPLICATION_INFO_VERSION_CODE = "APPLICATION_INFO_VERSION_CODE";

    public static final String APPLICATION_INFO_VERSION_NAME = "APPLICATION_INFO_VERSION_NAME";

    public static final String APPLICATION_INFO_PACKAGE_NAME = "APPLICATION_INFO_PACKAGE_NAME";

    public static final String APPLICATION_INFO_BUILD_CONFIG_DEBUG =
            "APPLICATION_INFO_BUILD_CONFIG_DEBUG";

    public static final String APPLICATION_INFO_BUILD_CONFIG_FLAVOR =
            "APPLICATION_INFO_BUILD_CONFIG_FLAVOR";

    public static final String APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE =
            "APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE";

    public static final String WORKING_DIR = "WORKING_DIR";

    public static final String FILE_PROVIDER_AUTHORITY = "FILE_PROVIDER_AUTHORITY";

    public static final String THEME = "THEME";

    public static final String TOOLBAR_TITLE_TEXT_COLOR = "TOOLBAR_TITLE_TEXT_COLOR";

    public static final String TOOLBAR_SUBTITLE_TEXT_COLOR = "TOOLBAR_SUBTITLE_TEXT_COLOR";

    public static final String SCREENSHOT_FILE = "SCREENSHOT_FILE";

    public static final String CALLER_ACTIVITY = "CALLER_ACTIVITY";

    public static final String WINDOW_TITLE = "WINDOW_TITLE";

    public static final String WINDOW_SUBTITLE = "WINDOW_SUBTITLE";

    public static final String MESSAGE = "MESSAGE";

    public static final String HEADER = "HEADER";

    public static final String SCREENSHOT_HINT = "SCREENSHOT_HINT";

    public static final String CONTENT_HINT = "CONTENT_HINT";

    public static final String CONTENT_ERROR_TEXT = "CONTENT_ERROR_TEXT";

    public static final String SCREENSHOT_TOUCH_TO_PREVIEW_HINT = "SCREENSHOT_PREVIEW_HINT";

    public static final String INCLUDE_LOGS_TEXT = "INCLUDE_LOGS_TEXT";

    public static final String INCLUDE_SCREENSHOT_TEXT = "INCLUDE_SCREENSHOT_TEXT";

    public static final String EXTRA_LAYOUT = "EXTRA_LAYOUT";

    public static final String SHOW_KEYBOARD_ON_START = "SHOW_KEYBOARD_ON_START";

    public static final String SCREEN_CAPTURING_FEATURE_ENABLED = "SCREEN_CAPTURING_FEATURE_ENABLED";

    public static final String LOGS_CAPTURING_FEATURE_ENABLED = "LOGS_CAPTURING_FEATURE_ENABLED";

    public static final String SHARED_PREFERENCES = "SHARED_PREFS";

    private static final String MAONI_LOGS_FILENAME = "maoni_logs.txt";

    protected View mRootView;

    private Feedback.App mAppInfo;

    private int mBlackoutColor;

    @Nullable
    private EditText mContent;

    @Nullable
    private CharSequence mContentErrorText;

    @Nullable
    private TextInputLayout mContentInputLayout;

    private String mFeedbackUniqueId;

    private int mHighlightColor;

    @Nullable
    private CheckBox mIncludeLogs;

    @Nullable
    private CheckBox mIncludeScreenshot;

    private Listener mListener;

    private boolean mLogsCapturingFeatureEnabled;

    private Menu mMenu;

    private boolean mScreenCapturingFeatureEnabled;

    @Nullable
    private CharSequence mScreenshotFilePath;

    private HashMap<String, Object> mSharedPreferences;

    private boolean mShowKeyboardOnStart;

    private Validator mValidator;

    private File mWorkingDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        setTheme(intent.getIntExtra(THEME, R.style.Maoni_AppTheme_Light));

        setContentView(R.layout.maoni_activity_feedback);

        mRootView = findViewById(R.id.maoni_container);
        if (mRootView == null) {
            throw new IllegalStateException(
                    "Layout must contain a root view with the following id: maoni_container");
        }

        mHighlightColor = ContextCompat.getColor(this, R.color.maoni_highlight_transparent_semi);
        mBlackoutColor = ContextCompat.getColor(this, R.color.maoni_black);

        if (intent.hasExtra(WORKING_DIR)) {
            mWorkingDir = new File(intent.getStringExtra(WORKING_DIR));
        } else {
            mWorkingDir = getCacheDir();
        }

        if (intent.hasExtra(SHARED_PREFERENCES)) {
            //noinspection unchecked
            mSharedPreferences = (HashMap<String, Object>) intent.getSerializableExtra(SHARED_PREFERENCES);
        }

        final ImageView headerImageView = findViewById(R.id.maoni_toolbar_header_image);
        if (headerImageView != null && intent.hasExtra(HEADER)) {
            final int headerLayoutId = intent.getIntExtra(HEADER, -1);
            if (headerLayoutId != -1) {
                headerImageView.setImageResource(headerLayoutId);
            }
        }

        if (intent.hasExtra(EXTRA_LAYOUT)) {
            final View extraContentView = findViewById(R.id.maoni_content_extra);
            if (extraContentView instanceof LinearLayout) {
                final int extraLayout = intent.getIntExtra(EXTRA_LAYOUT, -1);
                if (extraLayout != -1) {
                    final LinearLayout extraContent = (LinearLayout) extraContentView;
                    extraContent.setVisibility(View.VISIBLE);
                    extraContent
                            .addView(getLayoutInflater().inflate(extraLayout, extraContent, false));
                }
            }
        }

        final CallbacksConfiguration maoniConfiguration = CallbacksConfiguration.getInstance(this);
        mListener = maoniConfiguration.getListener();
        mValidator = maoniConfiguration.getValidator();

        final Toolbar toolbar = findViewById(R.id.maoni_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(intent.hasExtra(WINDOW_TITLE) ?
                    intent.getCharSequenceExtra(WINDOW_TITLE) :
                    getString(R.string.maoni_send_feedback));
            if (intent.hasExtra(WINDOW_SUBTITLE)) {
                toolbar.setSubtitle(intent.getCharSequenceExtra(WINDOW_SUBTITLE));
            }
            if (intent.hasExtra(TOOLBAR_TITLE_TEXT_COLOR)) {
                toolbar.setTitleTextColor(
                        intent.getIntExtra(TOOLBAR_TITLE_TEXT_COLOR, R.color.maoni_white));
            }
            if (intent.hasExtra(TOOLBAR_SUBTITLE_TEXT_COLOR)) {
                toolbar.setSubtitleTextColor(
                        intent.getIntExtra(TOOLBAR_SUBTITLE_TEXT_COLOR, R.color.maoni_white));
            }
            setSupportActionBar(toolbar);
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if (intent.hasExtra(MESSAGE)) {
            final CharSequence message = intent.getCharSequenceExtra(MESSAGE);
            final TextView activityMessageTv = findViewById(R.id.maoni_feedback_message);
            if (activityMessageTv != null) {
                activityMessageTv.setText(message);
            }
        }

        if (intent.hasExtra(SCREENSHOT_HINT)) {
            final CharSequence screenshotInformationalHint = intent.getCharSequenceExtra(SCREENSHOT_HINT);
            final TextView screenshotInformationalHintTv = findViewById(R.id.maoni_screenshot_informational_text);
            if (screenshotInformationalHintTv != null) {
                screenshotInformationalHintTv.setText(screenshotInformationalHint);
            }
        }

        mContentInputLayout = findViewById(R.id.maoni_content_input_layout);
        mContent = findViewById(R.id.maoni_content);

        if (intent.hasExtra(CONTENT_HINT)) {
            final CharSequence contentHint = intent.getCharSequenceExtra(CONTENT_HINT);
            if (mContentInputLayout != null) {
                mContentInputLayout.setHint(contentHint);
            }
        }

        if (intent.hasExtra(CONTENT_ERROR_TEXT)) {
            mContentErrorText = intent.getCharSequenceExtra(CONTENT_ERROR_TEXT);
        } else {
            mContentErrorText = getString(R.string.maoni_validate_must_not_be_blank);
        }

        mIncludeLogs = findViewById(R.id.maoni_include_logs);
        if (mIncludeLogs != null && intent.hasExtra(INCLUDE_LOGS_TEXT)) {
            mIncludeLogs.setText(intent.getCharSequenceExtra(INCLUDE_LOGS_TEXT));
        }

        mIncludeScreenshot = findViewById(R.id.maoni_include_screenshot);
        if (mIncludeScreenshot != null && intent.hasExtra(INCLUDE_SCREENSHOT_TEXT)) {
            mIncludeScreenshot.setText(intent.getCharSequenceExtra(INCLUDE_SCREENSHOT_TEXT));
        }

        mScreenshotFilePath = intent.getCharSequenceExtra(SCREENSHOT_FILE);

        mShowKeyboardOnStart = intent.getBooleanExtra(SHOW_KEYBOARD_ON_START, false);

        mScreenCapturingFeatureEnabled = intent.getBooleanExtra(SCREEN_CAPTURING_FEATURE_ENABLED, true);
        final Integer[] screenCapturingRelatedFields = new Integer[]{
                R.id.maoni_include_screenshot,
                R.id.maoni_include_screenshot_content
        };
        if (mScreenCapturingFeatureEnabled) {
            initScreenCaptureView(intent);
        }
        final int visibilityForScreenCapturingRelatedFields = (mScreenCapturingFeatureEnabled ?
                View.VISIBLE : View.GONE);
        for (final Integer screenCapturingRelatedField : screenCapturingRelatedFields) {
            final View view = findViewById(screenCapturingRelatedField);
            if (view == null) {
                continue;
            }
            view.setVisibility(visibilityForScreenCapturingRelatedFields);
        }

        mLogsCapturingFeatureEnabled = intent.getBooleanExtra(LOGS_CAPTURING_FEATURE_ENABLED, true);
        final Integer[] logsCapturingRelatedFields = new Integer[]{
                R.id.maoni_include_logs
        };
        final int visibilityForLogsCapturingRelatedFields = (mLogsCapturingFeatureEnabled ?
                View.VISIBLE : View.GONE);
        for (final Integer logsCapturingRelatedField : logsCapturingRelatedFields) {
            final View view = findViewById(logsCapturingRelatedField);
            if (view == null) {
                continue;
            }
            view.setVisibility(visibilityForLogsCapturingRelatedFields);
        }

        mFeedbackUniqueId = UUID.randomUUID().toString();

        final View fab = findViewById(R.id.maoni_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validateAndSubmitForm();
                }
            });
        }

        setAppRelatedInfo();

        final UiListener uiListener = maoniConfiguration.getUiListener();
        if (uiListener != null) {
            uiListener.onCreate(mRootView, savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mShowKeyboardOnStart) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    @Override
    protected void onDestroy() {
        CallbacksConfiguration.getInstance(this).reset();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mListener != null) {
            mListener.onDismiss();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maoni_activity_menu, menu);
        this.mMenu = menu;
        final AppBarLayout appBarLayout = findViewById(R.id.maoni_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                final MenuItem item = mMenu.findItem(R.id.maoni_feedback_send);
                if (item != null) {
                    item.setVisible(state != State.EXPANDED);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public final boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        } else if (itemId == R.id.maoni_feedback_send) {
            validateAndSubmitForm();
        }
        return true;
    }

    private void initScreenCaptureView(@NonNull final Intent intent) {
        final ImageButton screenshotThumb = findViewById(R.id.maoni_screenshot);

        final TextView touchToPreviewTextView = findViewById(R.id.maoni_screenshot_touch_to_preview);
        if (touchToPreviewTextView != null && intent.hasExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT)) {
            touchToPreviewTextView.setText(
                    intent.getCharSequenceExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT));
        }

        final View screenshotContentView = findViewById(R.id.maoni_include_screenshot_content);
        if (!TextUtils.isEmpty(mScreenshotFilePath)) {
            final File file = new File(mScreenshotFilePath.toString());
            if (file.exists()) {
                if (mIncludeScreenshot != null) {
                    mIncludeScreenshot.setVisibility(View.VISIBLE);
                }
                if (screenshotContentView != null) {
                    screenshotContentView.setVisibility(View.VISIBLE);
                }
                if (screenshotThumb != null) {
                    //Thumbnail - load with smaller resolution so as to reduce memory footprint
                    screenshotThumb.setImageBitmap(
                            ViewUtils.decodeSampledBitmapFromFilePath(
                                    file.getAbsolutePath(), 100, 100));
                }

                // Hook up clicks on the thumbnail views.
                if (screenshotThumb != null) {
                    screenshotThumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final Dialog imagePreviewDialog = new Dialog(MaoniActivity.this);

                            imagePreviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imagePreviewDialog.getWindow().setBackgroundDrawable(
                                    new ColorDrawable(Color.TRANSPARENT));

                            imagePreviewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    //nothing;
                                }
                            });

                            imagePreviewDialog.setContentView(R.layout.maoni_screenshot_preview);

                            final View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imagePreviewDialog.dismiss();
                                }
                            };

                            final ImageView imageView =
                                    imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_image);
                            imageView.setImageURI(Uri.fromFile(file));

                            final DrawableView drawableView = imagePreviewDialog
                                    .findViewById(R.id.maoni_screenshot_preview_image_drawable_view);
                            final DrawableViewConfig config = new DrawableViewConfig();
                            // If the view is bigger than canvas, with this the user will see the bounds
                            config.setShowCanvasBounds(true);
                            config.setStrokeWidth(57.0f);
                            config.setMinZoom(1.0f);
                            config.setMaxZoom(1.0f);
                            config.setStrokeColor(mHighlightColor);
                            final View decorView = getWindow().getDecorView();
                            config.setCanvasWidth(decorView.getWidth());
                            config.setCanvasHeight(decorView.getHeight());
                            drawableView.setConfig(config);
                            drawableView.bringToFront();

                            imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_pick_highlight_color)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            config.setStrokeColor(mHighlightColor);
                                        }
                                    });
                            imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_pick_blackout_color)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            config.setStrokeColor(mBlackoutColor);
                                        }
                                    });
                            imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_close)
                                    .setOnClickListener(clickListener);

                            imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_undo)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            drawableView.undo();
                                        }
                                    });

                            imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_save)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ViewUtils.exportViewToFile(MaoniActivity.this,
                                                    imagePreviewDialog.findViewById(
                                                            R.id.maoni_screenshot_preview_image_view_updated),
                                                    new File(mScreenshotFilePath.toString()));
                                            initScreenCaptureView(intent);
                                            imagePreviewDialog.dismiss();
                                        }
                                    });

                            imagePreviewDialog.setCancelable(true);
                            imagePreviewDialog.setCanceledOnTouchOutside(false);

                            imagePreviewDialog.show();
                        }
                    });
                }
            } else {
                if (mIncludeScreenshot != null) {
                    mIncludeScreenshot.setVisibility(View.GONE);
                }
                if (screenshotContentView != null) {
                    screenshotContentView.setVisibility(View.GONE);
                }
            }
        } else {
            if (mIncludeScreenshot != null) {
                mIncludeScreenshot.setVisibility(View.GONE);
            }
            if (screenshotContentView != null) {
                screenshotContentView.setVisibility(View.GONE);
            }
        }
    }

    private void setAppRelatedInfo() {

        final Intent intent = getIntent();
        final CharSequence callerActivity = intent.getCharSequenceExtra(CALLER_ACTIVITY);
        mAppInfo = new Feedback.App(
                callerActivity != null ? callerActivity : getClass().getSimpleName(),
                intent.hasExtra(APPLICATION_INFO_BUILD_CONFIG_DEBUG) ?
                        intent.getBooleanExtra(APPLICATION_INFO_BUILD_CONFIG_DEBUG, false) : null,
                intent.getStringExtra(APPLICATION_INFO_PACKAGE_NAME),
                intent.getIntExtra(APPLICATION_INFO_VERSION_CODE, -1),
                intent.getStringExtra(APPLICATION_INFO_BUILD_CONFIG_FLAVOR),
                intent.getStringExtra(APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE),
                intent.hasExtra(APPLICATION_INFO_VERSION_NAME) ?
                        intent.getStringExtra(APPLICATION_INFO_VERSION_NAME) : null);
    }

    private void validateAndSubmitForm() {
        //Validate form
        if (this.validateForm(mRootView)) {
            //TODO Check that device is actually connected to the internet prior to going any further
            boolean includeScreenshot = false;
            if (mScreenCapturingFeatureEnabled && mIncludeScreenshot != null) {
                includeScreenshot = mIncludeScreenshot.isChecked();
            }
            String contentText = "";
            if (mContent != null) {
                contentText = mContent.getText().toString();
            }

            final Intent intent = getIntent();

            Uri screenshotUri = null;
            File screenshotFile = null;
            Uri logsUri = null;
            File logsFile = null;

            final boolean includeLogs = mLogsCapturingFeatureEnabled &&
                    mIncludeLogs != null && mIncludeLogs.isChecked();
            if (includeLogs) {
                logsFile = new File(
                        mWorkingDir,
                        MAONI_LOGS_FILENAME);
                LogcatUtils.getLogsToFile(logsFile);
            }

            if (intent.hasExtra(FILE_PROVIDER_AUTHORITY)) {
                final String fileProviderAuthority = intent.getStringExtra(FILE_PROVIDER_AUTHORITY);
                //#11 : Potential NPE in Android's FileProvider if Provider Authority couldn't be resolved
                final ProviderInfo providerAuthorityInfo = this.getPackageManager()
                        .resolveContentProvider(fileProviderAuthority, PackageManager.GET_META_DATA);
                if (providerAuthorityInfo == null) {
                    Log.w(TAG, "Could not resolve file provider authority : "
                            + fileProviderAuthority + ". Sharing of files captured not supported then. "
                            + "See http://maoni.rm3l.org/ for setup instructions.");
                } else {
                    if (mScreenshotFilePath != null) {
                        screenshotFile = new File(mScreenshotFilePath.toString());
                        screenshotUri =
                                FileProvider.getUriForFile(this, fileProviderAuthority, screenshotFile);
                        grantUriPermission(intent.getComponent().getPackageName(), screenshotUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    if (logsFile != null) {
                        logsUri = FileProvider.getUriForFile(this, fileProviderAuthority, logsFile);
                        grantUriPermission(intent.getComponent().getPackageName(), logsUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
            }

            //Construct the feedback object and call the actual implementation
            final Feedback feedback =
                    new Feedback(mFeedbackUniqueId,
                            this,
                            mAppInfo,
                            contentText,
                            includeScreenshot,
                            screenshotUri,
                            screenshotFile,
                            includeLogs,
                            logsUri,
                            logsFile,
                            mSharedPreferences);
            if (mListener != null) {
                if (mListener.onSendButtonClicked(feedback)) {
                    finish();
                } // else do *not* finish the activity
            } else {
                finish();
            }
        } //else do nothing - this is up to the callback implementation
    }

    private boolean validateForm(@NonNull View rootView) {
        if (mContent != null) {
            if (TextUtils.isEmpty(mContent.getText())) {
                if (mContentInputLayout != null) {
                    mContentInputLayout.setErrorEnabled(true);
                    mContentInputLayout.setError(mContentErrorText);
                }
                return false;
            } else {
                if (mContentInputLayout != null) {
                    mContentInputLayout.setErrorEnabled(false);
                }
            }
        }
        //Call the validator implementation instead
        return mValidator == null || mValidator.validateForm(rootView);
    }

}
