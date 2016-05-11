package org.rm3l.maoni;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.rm3l.maoni.model.Feedback;

/**
 * Created by rm3l on 11/05/16.
 */
public final class MaoniConfiguration {

    private static MaoniConfiguration SINGLETON = null;

    @LayoutRes
    @Nullable
    private Integer extraLayout;

    @Nullable
    private Validator validator;

    @Nullable
    private Listener listener;

    @Nullable
    private UiListener uiListener;

    private MaoniConfiguration() {}

    @NonNull
    public static MaoniConfiguration getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new MaoniConfiguration();
        }
        return SINGLETON;
    }

    @Nullable
    public Integer getExtraLayout() {
        return extraLayout;
    }

    public MaoniConfiguration setExtraLayout(@Nullable Integer extraLayout) {
        this.extraLayout = extraLayout;
        return this;
    }

    @Nullable
    public Validator getValidator() {
        return validator;
    }

    public MaoniConfiguration setValidator(@Nullable Validator validator) {
        this.validator = validator;
        return this;
    }

    @Nullable
    public Listener getListener() {
        return listener;
    }

    public MaoniConfiguration setListener(@Nullable Listener listener) {
        this.listener = listener;
        return this;
    }

    @Nullable
    public UiListener getUiListener() {
        return uiListener;
    }

    public MaoniConfiguration setUiListener(@Nullable UiListener uiListener) {
        this.uiListener = uiListener;
        return this;
    }

    public MaoniConfiguration setHandler(@Nullable Handler handler) {
        setListener(handler);
        setValidator(handler);
        setUiListener(handler);
        return this;
    }

    public interface Validator {

        boolean validateForm(@NonNull final View rootView);
    }

    public interface Listener {

        void onDismiss();

        void onSendButtonClicked(@NonNull final Feedback feedback);

    }

    public interface UiListener {

        void onCreate(@NonNull final View rootView, Bundle savedInstanceState);

    }

    /**
     * Shortcut
     */
    public interface Handler extends Validator, Listener, UiListener {

    }

}
