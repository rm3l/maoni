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

import org.rm3l.maoni.Maoni;
import org.rm3l.maoni.model.Feedback;
import org.rm3l.maoni.sample.R;

public class MyHandlerForMaoni implements Maoni.Handler {

    public static final String EMAIL = "EMAIL";

    private TextInputLayout mEmailInputLayout;
    private EditText mEmail;

    private EditText mExtraEditText;
    private RadioGroup mExtraRadioGroup;
    
    private Context mContext;
    
    public MyHandlerForMaoni(Context context) {
        this.mContext = context;
    }
    
    @Override
    public void onDismiss() {
        Toast.makeText(mContext, "Activity Dismissed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendButtonClicked(@NonNull Feedback feedback) {
        // Depending on your use case, you may add specific data i the feedback object returned,
        // and manipulate it accordingly
        feedback.put(EMAIL, mEmail.getText());
        feedback.put("EXTRA_EDIT_TEXT", mExtraEditText.getText());
        feedback.put("EXTRA_RADIO_GROUP", mExtraRadioGroup.getCheckedRadioButtonId());
        Toast.makeText(mContext, "'Send Feedback' Callback", Toast.LENGTH_SHORT).show();
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

        //You may prefill some fields accordingly, before they are displayed to the user
        mEmail.setText("a@b.cd", TextView.BufferType.EDITABLE);
    }
}
