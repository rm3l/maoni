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
package org.rm3l.maoni.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Utilities for manipulating the App logs
 */
public final class LogcatUtils {

    private static final String TAG = LogcatUtils.class.getCanonicalName();
    private static final String PROCESS_ID = Integer.toString(android.os.Process.myPid());
    private static final String[] LOGCAT_CMD = { "logcat", "-d", "-v", "threadtime" };

    private LogcatUtils() {
        throw new UnsupportedOperationException("Not instantiable");
    }

    public static String getLogsAsString() {

        final StringBuilder builder = new StringBuilder();

        InputStreamReader in = null;
        BufferedReader bufferedReader = null;
        try {
            final Process process = Runtime.getRuntime().exec(LOGCAT_CMD);
            in = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(
                    in);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(PROCESS_ID)) {
                    builder.append(line);
                }
            }
        } catch (final IOException ioe) {
            Log.e(TAG, "getLog did not succeed", ioe);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not close InputStreamReader", e);
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Could not close BufferedReader", e);
                }
            }
        }

        return builder.toString();
    }

    public static void getLogsToFile(@NonNull final File outputFile) {

        PrintWriter out = null;
        BufferedReader bufferedReader = null;
        InputStreamReader in = null;
        try {
            //Empty out file first
            new PrintWriter(outputFile).close();

            out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));

            final Process process = Runtime.getRuntime().exec(LOGCAT_CMD);
            in = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(in);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(PROCESS_ID)) {
                    out.println(line);
                }
            }
        } catch (final IOException ioe) {
            Log.e(TAG, "getLog did not succeed", ioe);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not close InputStreamReader", e);
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Could not close BufferedReader", e);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }

    }
}
