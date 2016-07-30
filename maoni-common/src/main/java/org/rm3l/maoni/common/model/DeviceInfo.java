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
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Information about the current device (at the moment Maoni is called)
 */
public class DeviceInfo {

    public static final String GET_MOBILE_DATA_ENABLED = "getMobileDataEnabled";

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
     * Density Reference
     * ---
     */
    public final float DENSITY_DEFAULT = DisplayMetrics.DENSITY_DEFAULT;
    public final float DENSITY_LOW = DisplayMetrics.DENSITY_LOW;
    public final float DENSITY_MEDIUM = DisplayMetrics.DENSITY_MEDIUM;
    public final float DENSITY_HIGH = DisplayMetrics.DENSITY_HIGH;

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

        SupplicantState supplicantState = null;
        try {
            final WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
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

        this.resolution = String.format("%dx%d", this.widthPixels, this.heightPixels);

    }

    @Override
    public String toString() {
        return "- board='" + board + '\'' + "\n" +
                "- brand='" + brand + '\'' + "\n" +
                "- device='" + device + '\'' + "\n" +
                "- model='" + model + '\'' + "\n" +
                "- product='" + product + '\'' + "\n" +
                "- tags='" + tags + '\'' + "\n" +
                "- linuxVersion='" + linuxVersion + '\'' + "\n" +
                "- manufacturer='" + manufacturer + '\'' + "\n" +
                "- hardware='" + hardware + '\'' + "\n" +
                "- androidReleaseVersion='" + androidReleaseVersion + '\'' + "\n" +
                "- sdkVersion=" + sdkVersion + "\n" +
                "- buildVersion='" + buildVersion + '\'' + "\n" +
                "- buildDisplay='" + buildDisplay + '\'' + "\n" +
                "- buildFingerprint='" + buildFingerprint + '\'' + "\n" +
                "- buildId='" + buildId + '\'' + "\n" +
                "- buildTime=" + buildTime + "\n" +
                "- buildType='" + buildType + '\'' + "\n" +
                "- buildUser='" + buildUser + '\'' + "\n" +
                "- density=" + density + "\n" +
                "- densityDpi=" + densityDpi + "\n" +
                "- scaledDensity=" + scaledDensity + "\n" +
                "- xdpi=" + xdpi + "\n" +
                "- ydpi=" + ydpi + "\n" +
                "- DENSITY_DEFAULT=" + DENSITY_DEFAULT + "\n" +
                "- DENSITY_LOW=" + DENSITY_LOW + "\n" +
                "- DENSITY_MEDIUM=" + DENSITY_MEDIUM + "\n" +
                "- DENSITY_HIGH=" + DENSITY_HIGH + "\n" +
                "- heightPixels=" + heightPixels + "\n" +
                "- widthPixels=" + widthPixels + "\n" +
                "- resolution='" + resolution + '\'' + "\n" +
                "- gpsEnabled=" + gpsEnabled + "\n" +
                "- supplicantState=" + supplicantState + "\n" +
                "- mobileDataEnabled=" + mobileDataEnabled;
    }
}
