# Maoni [![Build Status](https://travis-ci.org/rm3l/maoni.svg?branch=master)](https://travis-ci.org/rm3l/maoni)

**Maoni** is a lightweight open-source library for integrating 
a way to collect user feedbacks from within Android applications.

Built from the ground up with the [Material Design](https://www.google.com/design/spec/material-design) 
principles in mind, it allows to capture a screenshot of the activity the user is currently viewing 
and attach it to the feedback.

Just provide callbacks implementations and you're good to go. 
Maoni will take care of collecting your users' feedbacks and call those implementations.

Below is a quick overview of the features included:
- **Contextual information**. Device and application information, if available.
    - Device screen resolution, mobile data and GPS states, ...
- **Screenshot capture**. 
    - Because receiving a feedback with contextual information is much much 
    better for analysis, Maoni allows to take a screenshot of the calling activity. 
    Of course, users always are free to include or not this screenshot in their feedback.
    - Touch to preview screenshot
- **Customization**.
    - Besides the default form fields, you are free to include an extra layout 
    with additional views. And you always have access to the underlying view elements.
    - Theme elements and styles can be adjusted.
- **Callbacks**.
    - Form validation. You can provide your own if needed for example for your extra fields.
    - Listeners. Upon validation, Maoni calls the callbacks implementations you provided earlier.
    So you just have limitless possibilities for an integration with any other systems.

Take a look at the [sample application](https://play.google.com/store/apps/details?id=org.rm3l.maoni) 
for a quick overview.


## Motivations

While working on a new version of [DD-WRT Companion](https://play.google.com/store/apps/details?id=org.rm3l.ddwrt), 
one my Android apps, I needed a simple yet pleasant way to collect users' feedbacks, 
along with some contextual information.
I experimented with a simple dialog, then tried a bunch of other libraries, 
but could not find one with screenshot capturing capabilities, not vendor lock-in, 
and which is almost a no-brainer as to integrating with any remote services.

So as a way to give back to the Open Source community, 
I decided to create Maoni as a separate library project.

As a side note, Maoni is a Swahili word for comments or opinions.


## Sample App

<a href="https://play.google.com/store/apps/details?id=org.rm3l.maoni">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>


## Screenshots

<div align="center">
    <img width="30%" src="https://raw.githubusercontent.com/rm3l/maoni/master/doc/screenshots/raw/1_Maoni_main_activity.png"/>
    <img height="0" width="8px"/>
    <img width="30%" src="https://raw.githubusercontent.com/rm3l/maoni/master/doc/screenshots/raw/2_Maoni_main_activity_with_screenshot_thumbnail.png"/>
    <img height="0" width="8px"/>
    <img width="30%" src="https://raw.githubusercontent.com/rm3l/maoni/master/doc/screenshots/raw/3_Maoni_main_activity_with_screenshot_touch_to_preview.png"/>
</div>


## Getting started

Grab via Gradle, by adding this to your `build.gradle`:

```gradle
  dependencies {
    // ...
    compile 'org.rm3l:maoni:1.2.0@aar'
  }
```

### Putting it together

Integrating with Maoni is intended to be seamless and straightforward for most existing 
Android applications.

Just leverage the fluent Maoni Builder to construct and start an Maoni instance at the right place 
within your application workflow (for example a button click listener, or a touch of a menu item).

For example, to start with just the defaults:
```java
    // MyHandlerForMaoni is a custom implementation of Handler, 
    // which is a shortcut interface for defining both a validator and listeners for Maoni
    final MyHandlerForMaoni handlerForMaoni = new MyHandlerForMaoni(MaoniSampleMainActivity.this);
    
    //The optional file provider authority allows you to 
    //share the screenshot capture file to other apps (depending on your callback implementation)
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withHandler(myHandlerForMaoniInstance) //Custom Callback for Maoni
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

To customize every aspect of your Maoni activity, call the fluent methods of `Maoni.Builder`, e.g.:
```java
    // MyHandlerForMaoni is a custom implementation of Maoni.Handler, 
    // which is a shortcut interface for defining both a validator and listeners for Maoni
    final MyHandlerForMaoni handlerForMaoni = new MyHandlerForMaoni(MaoniSampleMainActivity.this);
    
    //The optional file provider authority allows you to 
    //share the screenshot capture file to other apps (depending on your callback implementation)
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withWindowTitle("Send Feedback") //Set to an empty string to clear it
        .withMessage("Hey! Love or hate this app? We would love to hear from you.")
        .withExtraLayout(R.layout.my_feedback_activity_extra_content)
        .withHandler(myHandlerForMaoniInstance) //Custom Callback for Maoni
        .withFeedbackContentHint("[Custom hint] Write your feedback here")
        .withIncludeScreenshotText("[Custom text] Include screenshot")
        .withTouchToPreviewScreenshotText("Touch To Preview")
        .withContentErrorMessage("Custom error message")
        .withScreenshotHint("Custom test: Lorem Ipsum Dolor Sit Amet...")
        //... there are other aspects you can customize
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

**You're good to go!** Maoni will take care of validating / collecting your users' feedbacks 
and call your callbacks implementations. 

#### Available callbacks
Some common callbacks for Maoni are available as external dependencies to include in your application.

##### maoni-email

This callback opens up an Intent for sending an email with the feedback collected.

Add this additional line to your `build.gradle`:

```gradle
  dependencies {
    // ...
    compile 'org.rm3l:maoni-email:1.2.0'
  }
```

And set it as the listener for your Maoni instance:
```java
    final org.rm3l.maoni.email.MaoniEmailListener listenerForMaoni = 
            new org.rm3l.maoni.email.MaoniEmailListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-email
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```


#### Sharing the screenshot with other apps

The optional file provider authority specified in the `Maoni.Builder` constructor allows you to 
share the screenshot capture file to other apps (depending on your callback implementation).
By default, Maoni stores the screenshot file in your application cache directory, 
but this is (again) entirely customizable.

You must declare a file content provider in your `AndroidManifest.xml` file with an explicit list 
of sharable directories for other apps to be able to read the screenshot file. 
For example:
```xml
<application>
    <!-- ... -->
    <!-- If not defined yet, declare a file provider to be able to share screenshots captured by Maoni -->
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="org.rm3l.maoni.sample.fileprovider"
        android:grantUriPermissions="true"
        android:exported="false">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/filepaths" />
    </provider>
</application>
```

Along with the XML file that specifies the sharable directories (under `res/xml/filepaths.xml` as specified above):
```xml
<paths>
    <!-- By default, Maoni stores screenshots captures in the application cache directory. 
    So you must declare the path '.' as shareable. Specify something else if you are using a different path -->
    <cache-path name="maoni-shares" path="." />
    <!-- <files-path path="maoni-working-dir/" name="myCustomWorkingDirForMaoni" /> -->
</paths>
```

See https://goo.gl/31nStZ for further instructions on how to setup file sharing.


## Contribute and Improve Maoni!

Contributions and issue reporting are more than welcome. 
So to help out, do feel free to fork this repo and open up a pull request. 
I'll review and merge as quickly as possible.

You can use [GitHub issues](https://github.com/rm3l/maoni/issues) to report bugs. 
However, please make sure your description is clear enough and has sufficient instructions 
to be able to reproduce the issue.

You can also use the [sample app](https://play.google.com/store/apps/details?id=org.rm3l.maoni) 
to send your feedback with Maoni. ;-)


## In use in the following apps

(If you use this library, please drop me a line at &lt;apps+maoni@rm3l.org&gt; 
(or again, fork, modify this file and submit a pull request), so I can list your app(s) here)

* [DD-WRT Companion](https://play.google.com/store/apps/details?id=org.rm3l.ddwrt) (integration is rolling out to App marketplaces)


## Credits

* Armel Soro
 * [rm3l.org](https://rm3l.org) - &lt;apps+maoni@rm3l.org&gt;
 * [paypal.me/rm3l](https://paypal.me/rm3l)


## License

    The MIT License (MIT)
    
    Copyright (c) 2016 Armel Soro
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

