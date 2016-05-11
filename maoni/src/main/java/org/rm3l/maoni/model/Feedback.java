package org.rm3l.maoni.model;

import android.net.wifi.SupplicantState;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rm3l on 08/05/16.
 */
public class Feedback {

    @NonNull
    public final String id;

    @NonNull
    public final Phone phoneInfo;

    @NonNull
    public final App appInfo;

    @NonNull
    public final String userComment;

    public final boolean includeScreenshot;

    @Nullable
    public final File screenshotFilePath;

    @NonNull
    private final Map<String, Object> additionalData = new HashMap<>();

    public Feedback(@NonNull String id,
                    @NonNull Phone phoneInfo,
                    @NonNull App appInfo,
                    @NonNull String userComment,
                    boolean includeScreenshot,
                    @Nullable String screenshotFilePath) {

        this.id = id;
        this.phoneInfo = phoneInfo;
        this.appInfo = appInfo;
        this.userComment = userComment;
        this.includeScreenshot = includeScreenshot;
        this.screenshotFilePath =
                (screenshotFilePath != null ? new File(screenshotFilePath) : null);
    }

    public void put(@NonNull final String key, @Nullable final Object value) {
        this.additionalData.put(key, value);
    }

    @Nullable
    public Object get(@NonNull final String key) {
        return this.additionalData.get(key);
    }

    @NonNull
    public String getId() {
        return id;
    }

    public static class Phone {

        public final String model;

        public final String androidVersion;

        public final SupplicantState wifiState;

        public final boolean mobileDataEnabled;

        public final boolean gpsEnabled;

        public final String screenResolution;

        public Phone(String model,
                     String androidVersion,
                     SupplicantState wifiState,
                     boolean mobileDataEnabled,
                     boolean gpsEnabled,
                     String screenResolution) {

            this.model = model;
            this.androidVersion = androidVersion;
            this.wifiState = wifiState;
            this.mobileDataEnabled = mobileDataEnabled;
            this.gpsEnabled = gpsEnabled;
            this.screenResolution = screenResolution;
        }

    }

    public static class App {

        public final String caller;

        public final boolean debug;

        public final String applicationId;

        public final int versionCode;

        public final String flavor;

        public final String buildType;

        public final String versionName;

        public App(String caller, boolean debug,
                   String applicationId,
                   int versionCode,
                   String flavor,
                   String buildType,
                   String versionName) {

            this.caller = caller;
            this.debug = debug;
            this.applicationId = applicationId;
            this.versionCode = versionCode;
            this.flavor = flavor;
            this.buildType = buildType;
            this.versionName = versionName;
        }

    }
}
