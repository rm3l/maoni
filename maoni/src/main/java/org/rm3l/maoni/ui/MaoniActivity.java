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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rm3l.maoni.Maoni.CallbacksConfiguration;
import org.rm3l.maoni.R;
import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.contract.UiListener;
import org.rm3l.maoni.common.contract.Validator;
import org.rm3l.maoni.common.model.Feedback;

import java.io.File;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Maoni Activity
 */
public class MaoniActivity extends AppCompatActivity {

    public static final String APPLICATION_INFO_VERSION_CODE = "APPLICATION_INFO_VERSION_CODE";
    public static final String APPLICATION_INFO_VERSION_NAME = "APPLICATION_INFO_VERSION_NAME";
    public static final String APPLICATION_INFO_PACKAGE_NAME = "APPLICATION_INFO_PACKAGE_NAME";
    public static final String APPLICATION_INFO_BUILD_CONFIG_DEBUG =
            "APPLICATION_INFO_BUILD_CONFIG_DEBUG";
    public static final String APPLICATION_INFO_BUILD_CONFIG_FLAVOR =
            "APPLICATION_INFO_BUILD_CONFIG_FLAVOR";
    public static final String APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE =
            "APPLICATION_INFO_BUILD_CONFIG_BUILD_TYPE";
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
    public static final String INCLUDE_SCREENSHOT_TEXT = "INCLUDE_SCREENSHOT_TEXT";
    public static final String EXTRA_LAYOUT = "EXTRA_LAYOUT";
    public static final String GET_MOBILE_DATA_ENABLED = "getMobileDataEnabled";

    protected View mRootView;

    @Nullable
    private TextInputLayout mContentInputLayout;

    @Nullable
    private EditText mContent;

    @Nullable
    private CheckBox mIncludeScreenshot;

    @Nullable
    private CharSequence mScreenshotFilePath;

    @Nullable
    private CharSequence mContentErrorText;

    private Menu mMenu;

    private String mFeedbackUniqueId;
    private Feedback.App mAppInfo;
    private Feedback.Device mDeviceInfo;

    private Validator mValidator;
    private Listener mListener;

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

        final ImageView headerImageView = (ImageView) findViewById(R.id.maoni_toolbar_header_image);
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

        final CallbacksConfiguration maoniConfiguration = CallbacksConfiguration.getInstance();

        mListener = maoniConfiguration.getListener();
        mValidator = maoniConfiguration.getValidator();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.maoni_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(intent.hasExtra(WINDOW_TITLE) ?
                    intent.getCharSequenceExtra(WINDOW_TITLE) :
                    getString(R.string.send_feedback));
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
            final TextView activityMessageTv = (TextView) findViewById(R.id.maoni_feedback_message);
            if (activityMessageTv != null) {
                activityMessageTv.setText(message);
            }
        }

        if (intent.hasExtra(SCREENSHOT_HINT)) {
            final CharSequence screenshotInformationalHint = intent.getCharSequenceExtra(SCREENSHOT_HINT);
            final TextView screenshotInformationalHintTv =
                    (TextView) findViewById(R.id.maoni_screenshot_informational_text);
            if (screenshotInformationalHintTv != null) {
                screenshotInformationalHintTv.setText(screenshotInformationalHint);
            }
        }

        mContentInputLayout = (TextInputLayout) findViewById(R.id.maoni_content_input_layout);
        mContent = (EditText) findViewById(R.id.maoni_content);

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

        mIncludeScreenshot = (CheckBox) findViewById(R.id.maoni_include_screenshot);
        if (mIncludeScreenshot != null && intent.hasExtra(INCLUDE_SCREENSHOT_TEXT)) {
            mIncludeScreenshot.setText(intent.getCharSequenceExtra(INCLUDE_SCREENSHOT_TEXT));
        }

        final ImageButton screenshotThumb = (ImageButton)
                findViewById(R.id.maoni_screenshot);

        final TextView touchToPreviewTextView =
                (TextView) findViewById(R.id.maoni_screenshot_touch_to_preview);
        if (touchToPreviewTextView != null && intent.hasExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT)) {
            touchToPreviewTextView.setText(
                    intent.getCharSequenceExtra(SCREENSHOT_TOUCH_TO_PREVIEW_HINT));
        }

        final View screenshotContentView = findViewById(R.id.maoni_include_screenshot_content);
        mScreenshotFilePath = intent.getCharSequenceExtra(SCREENSHOT_FILE);
        if (!TextUtils.isEmpty(mScreenshotFilePath)) {
            final File file = new File(mScreenshotFilePath.toString());
            if (file.exists()) {
                if (mIncludeScreenshot != null) {
                    mIncludeScreenshot.setVisibility(View.VISIBLE);
                }
                if (screenshotContentView != null) {
                    screenshotContentView.setVisibility(View.VISIBLE);
                }
                final Bitmap mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (screenshotThumb != null) {
                    screenshotThumb.setImageBitmap(mBitmap);
                }

                // Hook up clicks on the thumbnail views.
                if (screenshotThumb != null) {
                    screenshotThumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final Dialog imagePreviewDialog = new Dialog(MaoniActivity.this);

                            imagePreviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imagePreviewDialog.getWindow().setBackgroundDrawable(
                                    new ColorDrawable(android.graphics.Color.TRANSPARENT));

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

                            final ImageView imageView = (ImageView)
                                    imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_image);
                            imageView.setImageBitmap(mBitmap);
                            imageView.setOnClickListener(clickListener);
                            imagePreviewDialog.findViewById(R.id.maoni_screenshot_preview_close)
                                    .setOnClickListener(clickListener);

                            imagePreviewDialog.setCancelable(true);
                            imagePreviewDialog.setCanceledOnTouchOutside(true);

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

        mFeedbackUniqueId = UUID.randomUUID().toString();

        final View fab = findViewById(R.id.maoni_fab);
        if (fab != null) {
            final ViewTreeObserver viewTreeObserver = fab.getViewTreeObserver();
            if (viewTreeObserver == null) {
                if (this.mMenu != null) {
                    final MenuItem item = this.mMenu.findItem(R.id.maoni_feedback_send);
                    if (item != null) {
                        item.setVisible(false);
                    }
                }
            } else {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mMenu != null) {
                            final MenuItem item = mMenu.findItem(R.id.maoni_feedback_send);
                            if (item != null) {
                                item.setVisible(fab.getVisibility() != View.VISIBLE);
                            }
                        }
                    }
                });
            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validateAndSubmitForm();
                }
            });
        }

        setAppRelatedInfo();
        setPhoneRelatedInfo();

        final UiListener uiListener = maoniConfiguration.getUiListener();
        if (uiListener != null) {
            uiListener.onCreate(mRootView, savedInstanceState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maoni_activity_menu, menu);
        this.mMenu = menu;
        return super.onCreateOptionsMenu(menu);
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

    private void setPhoneRelatedInfo() {

        SupplicantState supplicantState = null;
        try {
            final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            @SuppressWarnings("MissingPermission")
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            supplicantState = wifiInfo.getSupplicantState();
        } catch (Exception e) {
            //No worries
        }

        boolean mobileDataEnabled = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class cmClass = Class.forName(cm.getClass().getName());
            @SuppressWarnings("unchecked")
            final Method method = cmClass.getDeclaredMethod(GET_MOBILE_DATA_ENABLED);
            method.setAccessible(true);
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Private API access - no worries
        }
        boolean gpsEnabled = false;
        try {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            //No worries
        }

        String resolution = null;
        try {
            final DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            resolution = (Integer.toString(metrics.widthPixels) + "x" +
                    Integer.toString(metrics.heightPixels));
        } catch (Exception e) {
            //No worries
        }

        mDeviceInfo = new Feedback.Device(
                Build.MODEL,
                Build.VERSION.RELEASE,
                supplicantState,
                mobileDataEnabled,
                gpsEnabled,
                resolution);
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

    @Override
    public void onBackPressed() {
        if (mListener != null) {
            mListener.onDismiss();
        }
        super.onBackPressed();
    }

    private void validateAndSubmitForm() {
        //Validate form
        if (this.validateForm(mRootView)) {
            //Check that device is actually connected to the internet prior to going any further
            boolean includeScreenshot = false;
            if (mIncludeScreenshot != null) {
                includeScreenshot = mIncludeScreenshot.isChecked();
            }
            String contentText = "";
            if (mContent != null) {
                contentText = mContent.getText().toString();
            }

            final Intent intent = getIntent();

            Uri screenshotUri = null;
            File screenshotFile = null;
            if (intent.hasExtra(FILE_PROVIDER_AUTHORITY)) {
                final String fileProviderAuthority = intent.getStringExtra(FILE_PROVIDER_AUTHORITY);
                if (mScreenshotFilePath != null) {
                    screenshotFile = new File(mScreenshotFilePath.toString());
                    screenshotUri = FileProvider
                            .getUriForFile(this, fileProviderAuthority, screenshotFile);
                    grantUriPermission(intent.getComponent().getPackageName(),
                            screenshotUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            //Construct the feedback object and call the actual implementation
            final Feedback feedback =
                    new Feedback(mFeedbackUniqueId, mDeviceInfo, mAppInfo,
                            contentText, includeScreenshot, screenshotUri, screenshotFile);
            if (mListener != null) {
                mListener.onSendButtonClicked(feedback);
            }
            finish();
        } //else do nothing - this is up to the callback implementation
    }

}
