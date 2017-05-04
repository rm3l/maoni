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

-dontwarn kotlin.**
-dontwarn khttp.**

-keep class org.rm3l.maoni.common.model.** { *; }
#-keep class * implements java.io.Serializable { *; }