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
package org.rm3l.maoni;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.rm3l.maoni.model.Feedback;

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
