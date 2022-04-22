/*
 * Copyright (c) 2016-2022 Armel Soro
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
package org.rm3l.maoni.common.contract;

import org.rm3l.maoni.common.model.Feedback;

/**
 * Listener for Maoni. Also referred to as Maoni Callback.
 * <p>
 * Note that all the methods here are called in the UI thread and in a synchronous manner.
 */
public interface Listener {

    /**
     * Called when the user has dismissed the Maoni feedback form,
     * without having touched the "Send Feedback" button
     */
    void onDismiss();

    /**
     * Called upon a successful form validation.
     *
     * @param feedback the feedback object,
     *                 which you can manipulate to interact with other remote feedback systems.
     *
     * @return {@code true} if activity should be destroyed or {@code false} otherwise.
     */
    boolean onSendButtonClicked(final Feedback feedback);
}