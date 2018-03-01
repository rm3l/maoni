#
# Copyright (c) 2016 Armel Soro
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
#  of this software and associated documentation files (the "Software"), to deal
#  in the Software without restriction, including without limitation the rights
#  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#  copies of the Software, and to permit persons to whom the Software is
#  furnished to do so, subject to the following conditions:
#
#  The above copyright notice and this permission notice shall be included in all
#  copies or substantial portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
#  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
#  SOFTWARE.
#
#

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/rm3l/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# For AboutLibraries, exclude R from ProGuard to enable the library auto detection
-keep class .R
-keep class **.R$* {
    <fields>;
}

# Maoni
-keep class me.panavtec.drawableview.** { *; }
-keep class me.panavtec.drawableview.gestures.** { *; }
-dontwarn me.panavtec.drawableview.internal.**


-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn javax.jms.**
-dontwarn javax.naming.**
-dontwarn javax.mail.**
-dontwarn java.beans.**
-dontwarn java.management.**

-dontwarn com.google.vending.licensing.**

-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**

### Kotlin ###
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
#-keep class kotlin.reflect.jvm.internal.**
#-keep class kotlin.internal.**

-keep class org.rm3l.maoni.common.model.** { *; }

#-keepattributes Signature
#-keep class sun.misc.Unsafe { *; }
#-keep class * implements java.io.Serializable { *; }

-keep class khttp.**
-dontwarn java.nio.**

-dontwarn org.json.**
-dontwarn java.util.Base64
-dontwarn java.util.Base64$Encoder

-keep class android.** { *; }

# ensure that the appcompat libraries are in the Proguard exclusion list
-keep class android.support.v4.app.** { *; }
-keep class android.support.v4.view.** { *; }
-keep class android.support.v4.widget.** { *; }

-keep interface android.support.v4.app.** { *; }
-keep interface android.support.v4.view.** { *; }

-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep class android.support.v7.widget.** { *; }


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.billing.IInAppBillingService
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keep class com.android.common.view.** { *; }

# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }
-dontwarn com.squareup.leakcanary.**

#Retrofit (from Square)
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Configuration for Guava 18.0
#
# disagrees with instructions provided by Guava project: https://code.google.com/p/guava-libraries/wiki/UsingProGuardWithGuava

-dontwarn com.google.errorprone.annotations.**
-dontwarn om.google.j2objc.annotations.**

-dontwarn com.google.common.collect.**

-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static com.google.common.base.Joiner on(java.lang.String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

-dontwarn com.google.common.collect.MinMaxPriorityQueue

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn javax.jms.**
-dontwarn javax.naming.**
-dontwarn javax.mail.**
-dontwarn java.beans.**
-dontwarn java.management.**

-dontwarn org.apache.log4j.**

### Kotlin ###
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
#-keep class kotlin.reflect.jvm.internal.**
#-keep class kotlin.internal.**

-keep class khttp.**
-dontwarn java.nio.**

-dontwarn org.json.**
-dontwarn java.util.Base64
-dontwarn java.util.Base64$Encoder

#aboutlibraries (with auto-detection)
#-keep class .R
#-keep class **.R$* {
#    <fields>;
#}
-keepclasseswithmembers class **.R$* {
    public static final int define_*;
}

# Stetho
-keep class com.facebook.stetho.** { *; }

-keep class me.panavtec.drawableview.** { *; }
-keep class me.panavtec.drawableview.gestures.** { *; }
-dontwarn me.panavtec.drawableview.internal.**

-dontwarn org.apache.avalon.**
