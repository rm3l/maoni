package org.rm3l.maoni.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rm3l.maoni.BuildConfig;
import org.rm3l.maoni.MaoniConfiguration;
import org.rm3l.maoni.R;
import org.rm3l.maoni.model.Feedback;
import org.rm3l.maoni.utils.ViewUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.UUID;

public class MaoniActivity extends AppCompatActivity {

    public static final String SCREENSHOT_FILE = "SCREENSHOT_FILE";
    public static final String CALLER_ACTIVITY = "CALLER_ACTIVITY";
    public static final String WINDOW_TITLE = "WINDOW_TITLE";
    public static final String MESSAGE = "MESSAGE";
    public static final String SCREENSHOT_HINT = "SCREENSHOT_HINT";
    public static final String EXTRA_LAYOUT_RES = "EXTRA_LAYOUT_RES";
    private Bitmap mBitmap;

    @Nullable
    private TextInputLayout mContentInputLayout;
    @Nullable
    private EditText mContent;

    @Nullable
    private CheckBox mIncludeScreenshot;

    @Nullable
    private ImageButton mScreenshotThumb;
    @Nullable
    private ImageView mScreenshotExpanded;

    @Nullable
    private String mScreenshotFilePath;

    protected View mRootView;
    private Menu mMenu;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @Nullable
    private AppBarLayout mAppBarLayout;

    private String mFeedbackUniqueId;
    private Feedback.App mAppInfo;
    private Feedback.Phone mPhoneInfo;

    @LayoutRes
    @Nullable
    private Integer mExtraLayout;

    private MaoniConfiguration.Validator mValidator;
    private MaoniConfiguration.Listener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_NoActionBar);

        setContentView(R.layout.maoni_activity_feedback);

        mRootView = findViewById(R.id.maoni_container);
        if (mRootView == null) {
            throw new IllegalStateException(
                    "Layout must contain a root view with the following id: maoni_container");
        }

        final Intent intent = getIntent();

        final MaoniConfiguration maoniConfiguration = MaoniConfiguration.getInstance();

        final Integer extraLayout = maoniConfiguration.getExtraLayout();
        if (extraLayout != null) {
            final View extraContentView = findViewById(R.id.maoni_content_extra);
            if (extraContentView instanceof LinearLayout) {
                final LinearLayout extraContent = (LinearLayout) extraContentView;
                extraContent.setVisibility(View.VISIBLE);
                extraContent
                        .addView(getLayoutInflater().inflate(extraLayout, extraContent, false));
            }
        }

        mListener = maoniConfiguration.getListener();
        mValidator = maoniConfiguration.getValidator();

        final Context applicationContext = getApplicationContext();

        mAppBarLayout = (AppBarLayout) findViewById(R.id.maoni_app_bar);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.maoni_toolbar);
        if (toolbar != null) {
            final String windowTitle = intent.getStringExtra(WINDOW_TITLE);
            toolbar.setTitle(windowTitle == null ?
                    getString(R.string.send_feedback) : windowTitle);
            toolbar.setTitleTextAppearance(applicationContext,
                    R.style.MaoniTheme_ToolbarTitle);
            toolbar.setSubtitleTextAppearance(applicationContext,
                    R.style.MaoniTheme_ToolbarSubtitle);
            toolbar.setTitleTextColor(ContextCompat.getColor(this,
                    R.color.white));
            toolbar.setSubtitleTextColor(ContextCompat.getColor(this,
                    R.color.white));
            setSupportActionBar(toolbar);
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        final String message = intent.getStringExtra(MESSAGE);
        final TextView activityMessageTv = (TextView) findViewById(R.id.maoni_feedback_message);
        if (activityMessageTv != null) {
            activityMessageTv.setText(message != null ? message :
                    getString(R.string.maoni_feedback_message));
        }

        final String screenshotInformationalHint = intent.getStringExtra(SCREENSHOT_HINT);
        final TextView screenshotInformationalHintTv =
                (TextView) findViewById(R.id.maoni_screenshot_informational_text);
        if (screenshotInformationalHintTv != null) {
            screenshotInformationalHintTv.setText(screenshotInformationalHint != null ?
                    screenshotInformationalHint :
                    getString(R.string.maoni_screenshot_informational_text));
        }

        mContentInputLayout = (TextInputLayout) findViewById(R.id.maoni_content_input_layout);
        mContent = (EditText) findViewById(R.id.maoni_content);

        mIncludeScreenshot = (CheckBox) findViewById(R.id.maoni_include_screenshot);

        mScreenshotThumb = (ImageButton)
                findViewById(R.id.maoni_screenshot);
        mScreenshotExpanded = (ImageView)
                findViewById(R.id.maoni_screenshot_expanded);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        final View screenshotAndLogsContentView =
                findViewById(R.id.maoni_include_screenshot_content);
        mScreenshotFilePath = intent.getStringExtra(SCREENSHOT_FILE);
        if (!TextUtils.isEmpty(mScreenshotFilePath)) {
            final File file = new File(mScreenshotFilePath);
            if (file.exists()) {
                if (mIncludeScreenshot != null) {
                    mIncludeScreenshot.setVisibility(View.VISIBLE);
                }
                if (screenshotAndLogsContentView != null) {
                    screenshotAndLogsContentView.setVisibility(View.VISIBLE);
                }
                mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (mScreenshotThumb != null) {
                    mScreenshotThumb.setImageBitmap(mBitmap);
                }
                if (mScreenshotExpanded != null) {
                    mScreenshotExpanded.setImageBitmap(mBitmap);
                }

                // Hook up clicks on the thumbnail views.

                mScreenshotThumb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomImageFromThumb(mScreenshotThumb, mBitmap);
                        ViewUtils.hideAppBarLayout(mAppBarLayout);
                    }
                });
            } else {
                if (mIncludeScreenshot != null) {
                    mIncludeScreenshot.setVisibility(View.GONE);
                }
                if (screenshotAndLogsContentView != null) {
                    screenshotAndLogsContentView.setVisibility(View.GONE);
                }
            }
        } else {
            if (mIncludeScreenshot != null) {
                mIncludeScreenshot.setVisibility(View.GONE);
            }
            if (screenshotAndLogsContentView != null) {
                screenshotAndLogsContentView.setVisibility(View.GONE);
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

        final MaoniConfiguration.UiListener uiListener = maoniConfiguration.getUiListener();
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

        // Set app related properties
        final PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //No worries
        }

        final String callerActivity = getIntent().getStringExtra(CALLER_ACTIVITY);
        mAppInfo = new Feedback.App(
                callerActivity != null ? callerActivity : getClass().getSimpleName(),
                BuildConfig.DEBUG,
                BuildConfig.APPLICATION_ID,
                info != null ? info.versionCode : BuildConfig.VERSION_CODE,
                BuildConfig.FLAVOR,
                BuildConfig.BUILD_TYPE,
                info != null ? info.versionName : BuildConfig.VERSION_NAME);
    }

    private void setPhoneRelatedInfo() {

        // Set phone related properties
        SupplicantState supplicantState = null;
        try {
            final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            @SuppressWarnings("MissingPermission")
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            supplicantState = wifiInfo.getSupplicantState();
        } catch (Exception e) {
            //No worries
        }

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            @SuppressWarnings("unchecked")
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
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
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            resolution = Integer.toString(metrics.widthPixels) + "x" + Integer.toString(metrics.heightPixels);
        } catch (Exception e) {
            //No worries
        }

        mPhoneInfo = new Feedback.Phone(Build.MODEL,
                Build.VERSION.RELEASE,
                supplicantState,
                mobileDataEnabled,
                gpsEnabled,
                resolution);
    }

    @Override
    public void onBackPressed() {
        if (mScreenshotExpanded != null) {
            if (mScreenshotExpanded.getVisibility() == View.VISIBLE) {
                //Unzoom
                mScreenshotExpanded.performClick();
            } else {
                if (mListener != null) {
                    mListener.onDismiss();
                }
                super.onBackPressed();
            }
        }
    }

    private boolean validateForm(@NonNull View rootView) {
        if (mContent != null) {
            if (TextUtils.isEmpty(mContent.getText())) {
                if (mContentInputLayout != null) {
                    mContentInputLayout.setErrorEnabled(true);
                    mContentInputLayout.setError(getString(R.string.validate_must_not_be_blank));
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

            //Call actual implementation
            final Feedback feedback =
                    new Feedback(mFeedbackUniqueId, mPhoneInfo, mAppInfo,
                            contentText, includeScreenshot, mScreenshotFilePath);
            if (mListener != null) {
                mListener.onSendButtonClicked(feedback);
            }
            finish();
        } //else do nothing - this is up to the implementation
    }

    private void zoomImageFromThumb(final View thumbView, Bitmap bitmap) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.maoni_screenshot_expanded);
        if (expandedImageView != null) {
            expandedImageView.setImageBitmap(bitmap);
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        mRootView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        if (expandedImageView != null) {
            expandedImageView.setVisibility(View.VISIBLE);
            // Set the pivot point for SCALE_X and SCALE_Y transformations
            // to the top-left corner of the zoomed-in view (the default
            // is the center of the view).
            expandedImageView.setPivotX(0f);
            expandedImageView.setPivotY(0f);

        }

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        if (expandedImageView != null) {
            expandedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCurrentAnimator != null) {
                        mCurrentAnimator.cancel();
                    }

                    // Animate the four positioning/sizing properties in parallel,
                    // back to their original values.
                    AnimatorSet set = new AnimatorSet();
                    set.play(ObjectAnimator
                            .ofFloat(expandedImageView, View.X, startBounds.left))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView,
                                            View.Y, startBounds.top))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView,
                                            View.SCALE_X, startScaleFinal))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView,
                                            View.SCALE_Y, startScaleFinal));
                    set.setDuration(mShortAnimationDuration);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            thumbView.setAlpha(1f);
                            expandedImageView.setVisibility(View.GONE);
                            mCurrentAnimator = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            thumbView.setAlpha(1f);
                            expandedImageView.setVisibility(View.GONE);
                            mCurrentAnimator = null;
                        }
                    });
                    set.start();
                    mCurrentAnimator = set;

                    ViewUtils.showAppBarLayout(mAppBarLayout);
                }
            });
        }
    }

}
