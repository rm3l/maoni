package org.rm3l.maoni.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.rm3l.maoni.model.Feedback;
import org.rm3l.maoni.ui.MaoniActivity;

/**
 * Created by rm3l on 08/05/16.
 */
public class MyFeedbackActivity extends MaoniActivity {

    public static final String EMAIL = "EMAIL";

    private TextInputLayout mEmailInputLayout;
    private EditText mEmail;

    private EditText mExtraEditText;
    private RadioGroup mExtraRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmailInputLayout = (TextInputLayout) findViewById(R.id.extra_email_inputlayout);
        mEmail = (EditText) findViewById(R.id.extra_email);

        mExtraEditText = (EditText) findViewById(R.id.extra_edittext);
        mExtraRadioGroup = (RadioGroup) findViewById(R.id.extra_radiogroup);

        //If user ID is available (e.g., from preferences), you may prefill the field accordingly
        mEmail.setText("a@b.cd", TextView.BufferType.EDITABLE);
    }

    @Nullable
    @Override
    protected Integer getExtraLayout() {
        return R.layout.my_feedback_activity_extra_content;
    }

    @Override
    protected void onDismiss() {
        Toast.makeText(MyFeedbackActivity.this, "Activity Dismissed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSendButtonClicked(@NonNull Feedback feedback) {
        // Depending on your use case, you may add specific data i the feedback object returned,
        // and manipulate it accordingly
        feedback.put(EMAIL, mEmail.getText());
        feedback.put("EXTRA_EDIT_TEXT", mExtraEditText.getText());
        feedback.put("EXTRA_RADIO_GROUP", mExtraRadioGroup.getCheckedRadioButtonId());
        Toast.makeText(MyFeedbackActivity.this, "'Send Feedback' Callback", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean validateForm(@NonNull View rootView) {
        final boolean result = super.validateForm(rootView);
        if (!result) {
            return false;
        }
        if (mEmail != null) {
            if (TextUtils.isEmpty(mEmail.getText())) {
                if (mEmailInputLayout != null) {
                    mEmailInputLayout.setErrorEnabled(true);
                    mEmailInputLayout.setError(getString(R.string.validate_must_not_be_blank));
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
}
