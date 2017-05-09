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
package org.rm3l.maoni.email;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.model.Feedback;

import java.util.ArrayList;
import java.util.Map;

/**
 * Simple callback for Maoni that takes care of sending the Feedback via an email provider
 */
public class MaoniEmailListener implements Listener {

    public static final String DEFAULT_EMAIL_SUBJECT = "Feedback";

    private final Context mContext;
    private final String mMimeType;
    private final String mSubject;
    private final String[] mToAddresses;
    private final String mBodyHeader;
    private final String mBodyFooter;
    private final String[] mCcAddresses;
    private final String[] mBccAddresses;

    public MaoniEmailListener(final Context context, final String... toAddresses) {
        this(context, DEFAULT_EMAIL_SUBJECT, toAddresses);
    }

    public MaoniEmailListener(final Context context, final String subject,
        final String[] toAddresses) {
        this(context, subject, toAddresses, null, null);
    }

    public MaoniEmailListener(final Context context, final String subject,
        final String[] toAddresses, final String[] ccAddresses, final String[] bccAddresses) {
        this(context, "text/html", subject, null, null, toAddresses, ccAddresses, bccAddresses);
    }

    public MaoniEmailListener(
            final Context context,
            final String mimeType,
            final String subject,
            final String bodyHeader,
            final String bodyFooter,
            final String[] toAddresses,
            final String[] ccAddresses,
            final String[] bccAddresses) {
        this.mContext = context;
        this.mMimeType = mimeType;
        this.mSubject = subject;
        this.mToAddresses = toAddresses;
        this.mBodyHeader = bodyHeader;
        this.mBodyFooter = bodyFooter;
        this.mCcAddresses = ccAddresses;
        this.mBccAddresses = bccAddresses;
    }

    @Override
    public void onDismiss() {
        //Nothing to do
    }

    @Override
    public boolean onSendButtonClicked(final Feedback feedback) {

        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT,
                mSubject != null ? mSubject : DEFAULT_EMAIL_SUBJECT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            if (mToAddresses != null) {
                intent.putExtra(Intent.EXTRA_EMAIL, mToAddresses);
            }
            if (mCcAddresses != null) {
                intent.putExtra(Intent.EXTRA_CC, mCcAddresses);
            }
            if (mBccAddresses != null) {
                intent.putExtra(Intent.EXTRA_BCC, mBccAddresses);
            }
        }
        if (mMimeType != null) {
            intent.setType(mMimeType);
        }

        final StringBuilder body = new StringBuilder();
        if (mBodyHeader != null) {
            body.append(mBodyHeader).append("\n\n");
        }

        body.append(feedback.userComment).append("\n\n");

        body.append("\n------------\n");
        body.append("- Feedback ID: ").append(feedback.id).append("\n");

        final Map<CharSequence, Object> additionalData = feedback.getAdditionalData();
        if (additionalData != null) {
            body.append("\n------ Extra-fields ------\n");
            for (final Map.Entry<CharSequence, Object> entry : additionalData.entrySet()) {
                body.append("- ")
                        .append(entry.getKey()).append(": ").
                        append(entry.getValue()).append("\n");
            }
        }

        body.append("\n------ Application ------\n");
        if (feedback.appInfo != null) {
            if (feedback.appInfo.applicationId != null) {
                body.append("- Application ID: ").append(feedback.appInfo.applicationId).append("\n");
            }
            if (feedback.appInfo.caller != null) {
                body.append("- Activity: ").append(feedback.appInfo.caller).append("\n");
            }
            if (feedback.appInfo.buildType != null) {
                body.append("- Build Type: ").append(feedback.appInfo.buildType).append("\n");
            }
            if (feedback.appInfo.flavor != null) {
                body.append("- Flavor: ").append(feedback.appInfo.flavor).append("\n");
            }
            if (feedback.appInfo.versionCode != null) {
                body.append("- Version Code: ").append(feedback.appInfo.versionCode).append("\n");
            }
            if (feedback.appInfo.versionName != null) {
                body.append("- Version Name: ").append(feedback.appInfo.versionName).append("\n");
            }
        }

        body.append("\n------ Device ------\n");
        if (feedback.deviceInfo != null) {
            body.append(feedback.deviceInfo.toString());
        }
        body.append("\n\n");

        if (mBodyFooter != null) {
            body.append("\n--").append(mBodyFooter);
        }

        intent.putExtra(Intent.EXTRA_TEXT, body.toString());

        final ComponentName componentName = intent.resolveActivity(mContext.getPackageManager());
        if (componentName != null) {
            //Add screenshot as attachment
            final ArrayList<Uri> attachmentsUris = new ArrayList<>();
            if (feedback.screenshotFileUri != null) {
                //Grant READ permission to the intent
                mContext.grantUriPermission(componentName.getPackageName(),
                        feedback.screenshotFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                attachmentsUris.add(feedback.screenshotFileUri);
            }
            //Add logs file as attachment
            if (feedback.logsFileUri != null) {
                //Grant READ permission to the intent
                mContext.grantUriPermission(componentName.getPackageName(),
                        feedback.logsFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                attachmentsUris.add(feedback.logsFileUri);
            }
            if (!attachmentsUris.isEmpty()) {
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentsUris);
            }
            mContext.startActivity(intent);
        }

        return true;
    }
}
