package org.rm3l.maoni.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.rm3l.maoni.MaoniConfiguration;
import org.rm3l.maoni.model.Feedback;

/**
 * Created by rm3l on 08/05/16.
 */
public class MyHandlerForMaoni implements MaoniConfiguration.Handler {

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
    public void onCreate(@NonNull View rootView, Bundle savedInstanceState) {
        mEmailInputLayout = (TextInputLayout) rootView.findViewById(R.id.extra_email_inputlayout);
        mEmail = (EditText) rootView.findViewById(R.id.extra_email);

        mExtraEditText = (EditText) rootView.findViewById(R.id.extra_edittext);
        mExtraRadioGroup = (RadioGroup) rootView.findViewById(R.id.extra_radiogroup);

        //If user ID is available (e.g., from preferences), you may prefill the field accordingly
        mEmail.setText("a@b.cd", TextView.BufferType.EDITABLE);
    }
}
