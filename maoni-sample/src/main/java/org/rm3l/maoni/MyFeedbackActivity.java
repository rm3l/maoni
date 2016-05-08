package org.rm3l.maoni;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import org.rm3l.maoni.model.Feedback;
import org.rm3l.maoni.ui.MaoniActivity;

/**
 * Created by rm3l on 08/05/16.
 */
public class MyFeedbackActivity extends MaoniActivity {


    @Override
    protected void onDismiss() {
        Toast.makeText(MyFeedbackActivity.this, "onDismiss", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSendButtonClicked(@NonNull Feedback feedback) {

    }
}
