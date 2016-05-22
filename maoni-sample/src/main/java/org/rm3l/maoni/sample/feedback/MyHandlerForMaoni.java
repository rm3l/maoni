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

package org.rm3l.maoni.sample.feedback;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.rm3l.maoni.common.contract.Handler;
import org.rm3l.maoni.common.model.Feedback;
import org.rm3l.maoni.email.MaoniEmailListener;
import org.rm3l.maoni.sample.BuildConfig;
import org.rm3l.maoni.sample.R;

/**
 * {@link MaoniEmailListener} is a Maoni listener class allowing to send emails.
 * It comes as an external contrib, which can be included to your {@literal build.gradle},
 * as follows:
 * <p/>
 * <pre>
 *     <code>
 *         dependencies {
 *             //...
 *             compile 'org.rm3l:maoni-email:<versionToReplace>'
 *         }
 *     </code>
 * </pre>
 * <p/>
 * Anyways, you are free to just implement {@link Handler} and provide your own implementation.
 */
public class MyHandlerForMaoni extends MaoniEmailListener implements Handler {

    public static final String EMAIL = "EMAIL";
    private final Context mContext;
    private TextInputLayout mEmailInputLayout;
    private EditText mEmail;
    private EditText mExtraEditText;
    private RadioGroup mExtraRadioGroup;

    public MyHandlerForMaoni(Context context) {
        this(context,
                "text/html",
                "Feedback for Maoni Sample App (" +
                        BuildConfig.APPLICATION_ID + ":" +
                        BuildConfig.VERSION_NAME + ")",
                null,
                null,
                new String[]{"apps+maoni@rm3l.org"},
                null,
                new String[]{"apps+maoni_sample@rm3l.org"});
    }

    private MyHandlerForMaoni(Context context,
                              String mimeType,
                              String subject,
                              String bodyHeader,
                              String bodyFooter,
                              String[] toAddresses,
                              String[] ccAddresses,
                              String[] bccAddresses) {
        super(context,
                mimeType, subject, bodyHeader, bodyFooter,
                toAddresses, ccAddresses, bccAddresses);
        this.mContext = context;
    }

    @Override
    public void onDismiss() {
        Toast.makeText(mContext, "Activity Dismissed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendButtonClicked(@NonNull Feedback feedback) {
        // Depending on your use case, you may add specific data in the feedback object returned,
        // and manipulate it accordingly
        feedback.put(EMAIL, mEmail.getText());
        feedback.put("EXTRA_EDIT_TEXT", mExtraEditText.getText());
        feedback.put("EXTRA_RADIO_GROUP", mExtraRadioGroup.getCheckedRadioButtonId());
        super.onSendButtonClicked(feedback);
    }

    @Override
    public boolean validateForm(@NonNull View rootView) {
        if (mEmail != null) {
            if (TextUtils.isEmpty(mEmail.getText())) {
                if (mEmailInputLayout != null) {
                    mEmailInputLayout.setErrorEnabled(true);
                    mEmailInputLayout.setError(mContext.getString(R.string.maoni_validate_must_not_be_blank));
                }
                return false;
            } else {
                if (mEmailInputLayout != null) {
                    mEmailInputLayout.setErrorEnabled(false);
                }
            }
        }
        return true;
    }

    @Override
    public void onCreate(@NonNull View rootView, @Nullable final Bundle savedInstanceState) {
        mEmailInputLayout = (TextInputLayout) rootView.findViewById(R.id.extra_email_inputlayout);
        mEmail = (EditText) rootView.findViewById(R.id.extra_email);

        mExtraEditText = (EditText) rootView.findViewById(R.id.extra_edittext);
        mExtraRadioGroup = (RadioGroup) rootView.findViewById(R.id.extra_radiogroup);

        //You may pre-fill some fields accordingly, before they are displayed to the user
        mEmail.setText("a@b.cd", TextView.BufferType.EDITABLE);
    }
}
