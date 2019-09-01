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
package org.rm3l.maoni.doorbell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.model.DeviceInfo;
import org.rm3l.maoni.common.model.Feedback;
import org.rm3l.maoni.doorbell.api.DoorbellService;
import org.rm3l.maoni.doorbell.api.DoorbellSubmitRequest;
import org.rm3l.maoni.doorbell.api.MaoniDoorbellTransferListener;
import org.rm3l.maoni.doorbell.api.MaoniDoorbellTransferListener.MaoniDoorbellTransferProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import needle.UiRelatedProgressTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.rm3l.maoni.doorbell.api.MaoniDoorbellTransferListener.MaoniDoorbellTransferProgress.COMPLETED;
import static org.rm3l.maoni.doorbell.api.MaoniDoorbellTransferListener.MaoniDoorbellTransferProgress.ERROR;
import static org.rm3l.maoni.doorbell.api.MaoniDoorbellTransferListener.MaoniDoorbellTransferProgress.STARTED;
import static org.rm3l.maoni.doorbell.api.MaoniDoorbellTransferListener.MaoniDoorbellTransferProgress.UPLOADING_FEEDBACK_CONTENT;
import static org.rm3l.maoni.doorbell.api.MaoniDoorbellTransferListener.MaoniDoorbellTransferProgress.UPLOADING_FILES_CAPTURED;

/**
 * Callback for Maoni that takes care of sending the Feedback to Doorbell.io provider
 */
public class MaoniDoorbellListener implements Listener {

    private static final String USER_AGENT = "maoni-doorbell (v8.0.2)";

    private static final String FEEDBACK_API_BASE_URL = "https://doorbell.io/api/";

    private static final String EMPTY_STRING = "";

    private final int mApplicationId;
    private final String mApplicationKey;
    private final boolean mSkipFilesUpload;

    private final Callable<CharSequence> mFeedbackHeaderTextProvider;
    private final Callable<CharSequence> mFeedbackFooterTextProvider;

    private final CharSequence mWaitDialogTitle;
    private final CharSequence mWaitDialogMessage;
    private final CharSequence mWaitDialogCancelButtonText;

    private final Map<String, String> mHttpHeaders;

    private final DoorbellService mDoorbellService;
    private final Activity mActivity;
    private final Callable<Map<String, Object>> mAdditionalPropertiesProvider;
    private final Callable<List<String>> mAdditionalTagsProvider;
    private final MaoniDoorbellTransferListener mTransferListener;

    /**
     * Constructor, with the defaults.
     *
     * @param activity the calling activity
     */
    public MaoniDoorbellListener(final Activity activity) {
        this(new Builder(activity));
    }

    /**
     * Constructor, from a Builder instance
     * @param builder the builder instance
     */
    public MaoniDoorbellListener(Builder builder) {

        this.mActivity = builder.activity;
        this.mApplicationId = builder.applicationId;
        this.mApplicationKey = builder.applicationKey;
        this.mFeedbackHeaderTextProvider = builder.feedbackHeaderTextProvider;
        this.mFeedbackFooterTextProvider = builder.feedbackFooterTextProvider;
        this.mHttpHeaders = builder.httpHeaders;
        this.mSkipFilesUpload = builder.skipFilesUpload;
        this.mWaitDialogTitle = builder.waitDialogTitle;
        this.mWaitDialogMessage = builder.waitDialogMessage;
        this.mWaitDialogCancelButtonText = builder.waitDialogCancelButtonText;
        this.mAdditionalPropertiesProvider = builder.additionalPropertiesProvider;
        this.mAdditionalTagsProvider = builder.additionalTagsProvider;
        this.mTransferListener = builder.transferListener;

        final OkHttpClient.Builder okHttpClientBilder = new OkHttpClient().newBuilder();
        okHttpClientBilder.readTimeout(builder.readTimeout, builder.readTimeoutUnit);
        okHttpClientBilder.connectTimeout(builder.connectTimeout, builder.connectTimeoutUnit);

        if (builder.debug) {
            final HttpLoggingInterceptor interceptor =
                    new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            Log.d(USER_AGENT, message);
                        }
                    });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBilder.addInterceptor(interceptor);
        }

        this.mDoorbellService = new Retrofit.Builder()
                .baseUrl(FEEDBACK_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBilder.build())
                .build()
                .create(DoorbellService.class);
    }

    /**
     * Method to override, so as to include the email address of the user sending feedback.
     *
     * @return a valid email address, the address of the user sending feedback
     */
    @SuppressWarnings("WeakerAccess")
    protected String getUserEmail() {
        return null;
    }

    /**
     * Method to override, so as to include the name of the user who is sending feedback.
     *
     * @return the name of the user who is sending feedback
     */
    @SuppressWarnings("WeakerAccess")
    protected String getUserName() {
        return null;
    }

    @Override
    public void onDismiss() {
        //Nothing to do
    }

    @Override
    public boolean onSendButtonClicked(Feedback feedback) {

        //Check that device is actually connected to the internet prior to going any further
        final ConnectivityManager connMgr = (ConnectivityManager)
                mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(mActivity,
                    "An Internet connection is needed to send feedback.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        MultiThreadingManager.getFeedbackExecutor().execute(new FeedbackSenderTask(feedback));

        return true;
    }

    /**
     * Construct a new Builder
     * @param activity the calling activity
     * @return a builder
     */
    public Builder newBuilder(final Activity activity) {
        return new Builder(activity);
    }

    /**
     * Builder for Maoni Doorbell Listener
     */
    public static final class Builder {

        static final long READ_TIMEOUT_DEFAULT = 10;
        static final TimeUnit READ_TIMEOUT_UNIT_DEFAULT = TimeUnit.SECONDS;
        static final long CONNECT_TIMEOUT_DEFAULT = 10;
        static final TimeUnit CONNECT_TIMEOUT_UNIT_DEFAULT = TimeUnit.SECONDS;

        int applicationId;
        String applicationKey;
        boolean skipFilesUpload;
        Map<String, String> httpHeaders;

        Callable<CharSequence> feedbackHeaderTextProvider;
        Callable<CharSequence> feedbackFooterTextProvider;

        final Activity activity;
        CharSequence waitDialogTitle;
        CharSequence waitDialogMessage;
        CharSequence waitDialogCancelButtonText;
        boolean debug;
        long readTimeout;
        TimeUnit readTimeoutUnit;
        long connectTimeout;
        TimeUnit connectTimeoutUnit;
        Callable<Map<String, Object>> additionalPropertiesProvider;
        Callable<List<String>> additionalTagsProvider;
        MaoniDoorbellTransferListener transferListener;

        public Builder(final Activity activity) {
            this.activity = activity;
            this.debug = false;
            this.skipFilesUpload = false;
            this.readTimeout = READ_TIMEOUT_DEFAULT;
            this.readTimeoutUnit = READ_TIMEOUT_UNIT_DEFAULT;
            this.connectTimeout = CONNECT_TIMEOUT_DEFAULT;
            this.connectTimeoutUnit = CONNECT_TIMEOUT_UNIT_DEFAULT;

            this.waitDialogTitle = "Please hold on...";
            this.waitDialogMessage = "Submitting your feedback...";
            this.waitDialogCancelButtonText = "Cancel";

            this.httpHeaders = new HashMap<>();
            this.httpHeaders.put("Content-Type", "application/json");
            this.httpHeaders.put("User-Agent", USER_AGENT);

            this.transferListener = null;
        }

        /**
         * @param applicationId your Doorbell Application ID
         * @return this builder
         */
        public Builder withApplicationId(int applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        /**
         * @param applicationKey your Doorbell Secret Application Key
         * @return this builder
         */
        public Builder withApplicationKey(String applicationKey) {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder withFeedbackHeaderText(final String feedbackHeaderText) {
            return this.withFeedbackHeaderTextProvider(new Callable<CharSequence>() {
                @Override
                public CharSequence call() throws Exception {
                    return feedbackHeaderText;
                }
            });
        }

        public Builder withFeedbackFooterText(final String feedbackFooterText) {
            return this.withFeedbackFooterTextProvider(new Callable<CharSequence>() {
                @Override
                public CharSequence call() throws Exception {
                    return feedbackFooterText;
                }
            });
        }

        public Builder withFeedbackHeaderTextProvider(final Callable<CharSequence> feedbackHeaderTextProvider) {
            this.feedbackHeaderTextProvider = feedbackHeaderTextProvider;
            return this;
        }

        public Builder withFeedbackFooterTextProvider(final Callable<CharSequence> feedbackFooterTextProvider) {
            this.feedbackFooterTextProvider = feedbackFooterTextProvider;
            return this;
        }

        public Builder withWaitDialogCancelButtonText(CharSequence waitDialogCancelButtonText) {
            this.waitDialogCancelButtonText = waitDialogCancelButtonText;
            return this;
        }

        public Builder withWaitDialogMessage(CharSequence waitDialogMessage) {
            this.waitDialogMessage = waitDialogMessage;
            return this;
        }

        public Builder withWaitDialogTitle(CharSequence waitDialogTitle) {
            this.waitDialogTitle = waitDialogTitle;
            return this;
        }

        /**
         * Set a flag indicating whether or not to skip file uploads to Doorbell.
         * <p>
         * This may be useful in cases like:
         * <ul>
         *     <li>you know in advance that your Doorbell account does not uploads capabilities</li>
         *     <li>and/or you wish to explicitly upload files to another remote service</li>
         * </ul>
         *
         * @param skipFilesUpload a flag indicating whether or not to skip file uploads to Doorbell
         * @return this builder
         */
        public Builder withSkipFilesUpload(boolean skipFilesUpload) {
            this.skipFilesUpload = skipFilesUpload;
            return this;
        }

        public Builder withDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder withReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder withReadTimeoutUnit(TimeUnit readTimeoutUnit) {
            this.readTimeoutUnit = readTimeoutUnit;
            return this;
        }

        public Builder withConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder withConnectTimeoutUnit(TimeUnit connectTimeoutUnit) {
            this.connectTimeoutUnit = connectTimeoutUnit;
            return this;
        }

        public Builder addHttpHeader(String headerKey, String headerValue) {
            if (this.httpHeaders == null) {
                this.httpHeaders = new HashMap<>();
            }
            this.httpHeaders.put(headerKey, headerValue);
            return this;
        }

        public Builder withAdditionalPropertiesToSend(final Map<String, Object> additionalProperties) {
            return this.withAdditionalPropertiesProvider(new Callable<Map<String, Object>>() {
                @Override
                public Map<String, Object> call() throws Exception {
                    return additionalProperties;
                }
            });
        }

        public Builder withAdditionalPropertiesProvider(Callable<Map<String, Object>> additionalPropertiesProvider) {
            this.additionalPropertiesProvider = additionalPropertiesProvider;
            return this;
        }

        public Builder withAdditionalTagsToSend(final List<String> additionalTags) {
            return this.withAdditionalTagsProvider(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    return additionalTags;
                }
            });
        }

        public Builder withAdditionalTagsProvider(Callable<List<String>> additionalTagsProvider) {
            this.additionalTagsProvider = additionalTagsProvider;
            return this;
        }

        public Builder withTransferListener(MaoniDoorbellTransferListener transferListener) {
            this.transferListener = transferListener;
            return this;
        }

        public MaoniDoorbellListener build() {
            return new MaoniDoorbellListener(this);
        }
    }

    private class FeedbackSenderTask
            extends UiRelatedProgressTask<Throwable, MaoniDoorbellTransferProgress> {

        private final Feedback feedback;
        private final ProgressDialog alertDialog;
        private final Map<String, Object> properties;
        private final List<String> tags;

        FeedbackSenderTask(Feedback feedback) {
            this.feedback = feedback;
            alertDialog = new ProgressDialog(mActivity);
            alertDialog.setTitle(mWaitDialogTitle);
            alertDialog.setMessage(mWaitDialogMessage);
            alertDialog.setIndeterminate(false);
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setMax(100);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mWaitDialogCancelButtonText,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FeedbackSenderTask.this.cancel();
                }
            });
            alertDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog.show();
                }
            });
            this.properties = new HashMap<>();
            this.tags = new ArrayList<>();
        }

        @Override
        protected void onProgressUpdate(MaoniDoorbellTransferProgress transferProgress) {
            if (MaoniDoorbellListener.this.mTransferListener != null) {
                MaoniDoorbellListener.this.mTransferListener.onProgressUpdate(transferProgress);
            }

            switch (transferProgress) {
                case UPLOADING_FEEDBACK_CONTENT:
                    alertDialog.setProgress(50);
                    break;
                case COMPLETED:
                case ERROR:
                    alertDialog.setProgress(100);
                    sUiHandler.postDelayed(alertDialog::cancel, 1000);
                    break;
            }
        }

        @Override
        protected Throwable doWork() {
            publishProgress(STARTED);
            final boolean includeScreenshot = feedback.includeScreenshot;
            final boolean includeLogs = feedback.includeLogs;

            try {
                final Response<ResponseBody> openResponse = mDoorbellService
                        .openApplication(mHttpHeaders, mApplicationId, mApplicationKey)
                        .execute();
                if (openResponse.code() != 201) {
                    throw new IllegalStateException("Cannot open Doorbell application: " +
                            openResponse.message());
                }

                if (mAdditionalPropertiesProvider != null) {
                    final Map<String, Object> map = mAdditionalPropertiesProvider.call();
                    if (map != null) {
                        properties.putAll(map);
                    }
                }

                if (mAdditionalTagsProvider != null) {
                    List<String> list = mAdditionalTagsProvider.call();
                    if (list != null) {
                        tags.addAll(list);
                    }
                }

                //Add device info retrieved from the Feedback object
                final DeviceInfo deviceInfo = feedback.deviceInfo;
                if (deviceInfo != null) {
                    properties.putAll(feedback.getDeviceAndAppInfoAsHumanReadableMap());
                }

                final List<String> attachmentsIds = new ArrayList<>();
                //1. Upload file captures
                if (!mSkipFilesUpload) {
                    final Map<String, String> multipartUploadHeaders = new HashMap<>(mHttpHeaders);
                    multipartUploadHeaders.remove("Content-Type");

                    boolean screenshotUploadProgressPublished = false;
                    if (includeScreenshot) {
                        publishProgress(UPLOADING_FILES_CAPTURED);
                        screenshotUploadProgressPublished = true;

                        final Response<String[]> uploadScreenshotResponse = mDoorbellService
                                .uploadScreenshot(multipartUploadHeaders, mApplicationId, mApplicationKey,
                                RequestBody.create(
                                        MediaType.parse(DoorbellService.MULTIPART_FORM_DATA),
                                        feedback.screenshotFile)).execute();
                        switch (uploadScreenshotResponse.code()) {
                            case 201:
                                //Great
                                break;
                            case 400:
                                throw new IllegalArgumentException(uploadScreenshotResponse.message());
                            default:
                                throw new IllegalStateException(uploadScreenshotResponse.message());

                        }
                        attachmentsIds.addAll(Arrays.asList(uploadScreenshotResponse.body()));
                    }

                    if (includeLogs) {
                        if (!screenshotUploadProgressPublished) {
                            publishProgress(UPLOADING_FILES_CAPTURED);
                        }
                        final Response<String[]> uploadLogsResponse = mDoorbellService
                                .uploadLogs(multipartUploadHeaders, mApplicationId, mApplicationKey,
                                        RequestBody.create(
                                                MediaType.parse(DoorbellService.MULTIPART_FORM_DATA),
                                                feedback.logsFile))
                                .execute();

                        switch (uploadLogsResponse.code()) {
                            case 201:
                                //Great
                                break;
                            case 400:
                                throw new IllegalArgumentException(uploadLogsResponse.message());
                            default:
                                throw new IllegalStateException(uploadLogsResponse.message());

                        }
                        attachmentsIds.addAll(Arrays.asList(uploadLogsResponse.body()));
                    }
                }

                //Now send feedback object
                publishProgress(UPLOADING_FEEDBACK_CONTENT);

                final String userEmail = getUserEmail();
                final String userName = getUserName();

                final List<Long> attachments = new ArrayList<>();
                for (final String attachmentsId : attachmentsIds) {
                    if (attachmentsId != null) {
                        attachments.add(Long.valueOf(attachmentsId));
                    }
                }

                final DoorbellSubmitRequest doorbellSubmitRequest = new DoorbellSubmitRequest()
                    .setEmail(userEmail)
                    .setName(userName)
                    .setMessage(String.format("%s\n%s\n%s",
                        nullToEmpty(mFeedbackHeaderTextProvider != null ?
                            mFeedbackHeaderTextProvider.call() : null),
                        nullToEmpty(feedback.userComment),
                        nullToEmpty(mFeedbackFooterTextProvider != null ?
                            mFeedbackFooterTextProvider.call() : null)))
                    .setAttachments(attachments)
                    .setProperties(properties)
                    .setTags(tags);

                final Response<ResponseBody> response = mDoorbellService
                        .submitFeedbackForm(
                                mHttpHeaders,
                                mApplicationId,
                                mApplicationKey,
                                doorbellSubmitRequest)
                        .execute();

                if (response.code() != 201) {
                    throw new IllegalStateException(response.message());
                }

                publishProgress(COMPLETED);
                return null;

            } catch (final Exception exception) {
                exception.printStackTrace();
                publishProgress(ERROR);
                if (MaoniDoorbellListener.this.mTransferListener != null) {
                    MaoniDoorbellListener.this.mTransferListener.onError(exception);
                }
                return exception;
            }
        }

        @Override
        protected void thenDoUiRelatedWork(Throwable throwable) {
            if (throwable == null && MaoniDoorbellListener.this.mTransferListener != null) {
                MaoniDoorbellListener.this.mTransferListener
                        .thenDoUiRelatedWorkOnSuccessfulTransfer(this.feedback);
            }
        }
    }

    private static String nullToEmpty(final CharSequence str) {
        return (TextUtils.isEmpty(str) ? EMPTY_STRING : str.toString());
    }
}
