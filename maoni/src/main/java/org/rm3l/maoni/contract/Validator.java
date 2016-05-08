package org.rm3l.maoni.contract;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by rm3l on 05/05/16.
 */
public interface Validator {

    boolean validateForm(@NonNull final View rootView);
}
