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
package org.rm3l.maoni.common.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLES10;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Information about the current device (at the moment Maoni is called)
 */
public class DeviceInfo {

    private static final String GET_MOBILE_DATA_ENABLED = "getMobileDataEnabled";

    /*
     * ---
     * App / SDK
     * ---
     */
    public final int sdkVersion = Build.VERSION.SDK_INT;

    /*
     * ---
     * Device
     * ---
     */
    public final String board = android.os.Build.BOARD;
    public final String brand = Build.BRAND;
    public final String device = Build.DEVICE;
    public final String model = Build.MODEL;
    public final String product = Build.PRODUCT;
    public final String tags = Build.TAGS;
    public final String linuxVersion = System.getProperty("os.version");
    public final String manufacturer = Build.MANUFACTURER;
    public final String hardware = Build.HARDWARE;
    public final String cpuAbi = Build.CPU_ABI;
    public final String cpuAbi2 = Build.CPU_ABI2;
    public final String[] supportedAbis = {cpuAbi, cpuAbi2};
    public final boolean isTablet;

    /*
     * ---
     * OS
     * ---
     */
    public final String androidReleaseVersion = Build.VERSION.RELEASE;
    public final String buildVersion = Build.VERSION.INCREMENTAL;
    public final String buildDisplay = Build.DISPLAY;
    public final String buildFingerprint = Build.FINGERPRINT;
    public final String buildId = Build.ID;
    public final long buildTime = Build.TIME;
    public final String buildType = Build.TYPE;
    public final String buildUser = Build.USER;

    public final String language = Locale.getDefault().getDisplayName();

    public final String openGlVersion = GLES10.glGetString(GLES10.GL_VERSION);

    /*
     * ---
     * Density
     * ---
     */
    public final float density;
    public final int densityDpi;
    public final float scaledDensity;
    public final float xdpi;
    public final float ydpi;

    /*
     * ---
     * Screen
     * ---
     */
    public final int heightPixels;
    public final int widthPixels;
    public final String resolution;

    /*
     * ---
     * Other useful info
     * ---
     */
    public final Boolean gpsEnabled;
    public final SupplicantState supplicantState;
    public final Boolean mobileDataEnabled;

    /**
     * Constructor
     *
     * @param activity the calling activity
     */
    @SuppressLint("DefaultLocale")
    public DeviceInfo(final Activity activity) {

        this.isTablet = ((activity.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        SupplicantState supplicantState = null;
        try {
            final WifiManager wifiManager = (WifiManager)
                activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            @SuppressWarnings("MissingPermission")
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            supplicantState = wifiInfo.getSupplicantState();
        } catch (Exception e) {
            //No worries
        }
        this.supplicantState = supplicantState;

        final ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        Boolean mobileDataEnabled = null;
        try {
            final Class cmClass = Class.forName(cm.getClass().getName());
            @SuppressWarnings("unchecked")
            final Method method = cmClass.getDeclaredMethod(GET_MOBILE_DATA_ENABLED);
            method.setAccessible(true);
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Private API access - no worries
        }
        this.mobileDataEnabled = mobileDataEnabled;

        Boolean gpsEnabled = null;
        try {
            final LocationManager manager =
                    (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            //No worries
        }
        this.gpsEnabled = gpsEnabled;

        final WindowManager windowManager = activity.getWindowManager();
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        this.density = metrics.density;
        this.densityDpi = metrics.densityDpi;
        this.scaledDensity = metrics.scaledDensity;
        this.xdpi = metrics.xdpi;
        this.ydpi = metrics.ydpi;

        this.heightPixels = metrics.heightPixels;
        this.widthPixels = metrics.widthPixels;

        this.resolution = String.format("%d x %d", this.widthPixels, this.heightPixels);

    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Map<String, Object> rawMap = toRawMap();
        for (final Map.Entry<String, Object> entry : rawMap.entrySet()) {
            final Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            stringBuilder.append(
                    String.format("- %s=%s\n",
                            entry.getKey(),
                            (value instanceof String[]) ?
                                    Arrays.toString((String[]) value) : value));
        }
        return stringBuilder.toString();
    }

    public Map<String, Object> toRawMap() {
        final SortedMap<String, Object> output = new TreeMap<>();
        //Introspect to get all fields
        final Field[] fields = DeviceInfo.class.getFields();
        for (final Field field : fields) {
            final Object fieldValue;
            try {
                fieldValue = field.get(this);
            } catch (IllegalAccessException e) {
                //No worries
                continue;
            }
            if (fieldValue == null) {
                continue;
            }
            output.put(field.getName(), fieldValue);
        }
        return Collections.unmodifiableMap(output);
    }

}
