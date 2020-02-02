[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:maoni.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni)
[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat)](https://github.com/rm3l/maoni/blob/master/LICENSE)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Maoni-blue.svg?style=flat)](http://android-arsenal.com/details/1/3925)
[![Website](https://img.shields.io/website-up-down-green-red/http/maoni.rm3l.org.svg)](http://maoni.rm3l.org)
<!-- [![Join the chat at https://gitter.im/rm3l/maoni](https://badges.gitter.im/rm3l/maoni.svg)](https://gitter.im/rm3l/maoni?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) -->

[![CircleCI](https://circleci.com/gh/rm3l/maoni/tree/master.svg?style=svg)](https://circleci.com/gh/rm3l/maoni/tree/master)
[![Issue Count](https://codeclimate.com/github/rm3l/maoni/badges/issue_count.svg)](https://codeclimate.com/github/rm3l/maoni)

[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=rm3l/maoni)](https://dependabot.com)

[![GitHub watchers](https://img.shields.io/github/watchers/rm3l/maoni.svg?style=social&label=Watch)](https://github.com/rm3l/maoni)
[![GitHub stars](https://img.shields.io/github/stars/rm3l/maoni.svg?style=social&label=Star)](https://github.com/rm3l/maoni)
[![GitHub forks](https://img.shields.io/github/forks/rm3l/maoni.svg?style=social&label=Fork)](https://github.com/rm3l/maoni)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Motivations](#motivations)
- [Sample App](#sample-app)
- [Preview](#preview)
- [Getting started](#getting-started)
  - [Putting it together](#putting-it-together)
    - [Available callbacks](#available-callbacks)
      - [maoni-email](#maoni-email)
      - [maoni-slack](#maoni-slack)
      - [maoni-github](#maoni-github)
      - [maoni-jira](#maoni-jira)
      - [maoni-doorbell](#maoni-doorbell)
    - [Sharing the files captured with other apps](#sharing-the-files-captured-with-other-apps)
- [Contribute and Improve Maoni!](#contribute-and-improve-maoni)
  - [Building from source](#building-from-source)
  - [Translations](#translations)
  - [Contributing callbacks for Maoni](#contributing-callbacks-for-maoni)
    - [Publishing a new release](#publishing-a-new-release)
- [In use in the following apps](#in-use-in-the-following-apps)
- [Credits](#credits)
- [Developed by](#developed-by)
- [Contributors](#contributors)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

**Maoni** is a lightweight open-source library for integrating 
a way to collect user feedback from within Android applications.

Built from the ground up with the [Material Design](https://www.google.com/design/spec/material-design) 
principles in mind, it allows to capture a screenshot of the activity the user is currently viewing 
and attach it to the feedback.

Just provide callbacks implementations and you're good to go. 
Maoni will take care of collecting feedback and call those implementations.

Below is a quick overview of the features included:
- **Contextual information**. Device and application information, if available.
    - Device screen resolution, mobile data and GPS states, ...
- **Screenshot and logs capture**. 
    - Because receiving a feedback with contextual information is much much 
    better for analysis, Maoni allows to take a screen capture of the calling activity, along with the application logs.
    Note that the inclusion of such screenshot and logs in the feedback object is opt-out, at the user's discretion.
    - Touch to preview screenshot
    - Ability for users of your app to highlight or blackout items in the screen capture. They may choose to 
    highlight relevant items or blackout any sensitive information.
- **Customization**.
    - Besides the default form fields, you are free to include an extra layout 
    with additional views. And you always have access to the underlying view elements.
    - Theme elements and styles can be adjusted.
- **Callbacks**.
    - Form validation. You can provide your own if needed for example for your extra fields.
    - Listeners. Upon validation, Maoni calls the callbacks implementations you provided earlier.
    So you just have limitless possibilities for an integration with any remote feedback services. For reference, the following implementations are provided as external dependencies:
        - [maoni-email](callbacks/maoni-email/), so your users can send their feedback via email
        - [maoni-slack](callbacks/maoni-slack/), so your users can send their feedback to Slack
        - [maoni-jira](callbacks/maoni-jira/), to send user feedback as JIRA issues (to the JIRA host of your choice)
        - [maoni-github](callbacks/maoni-github/), to send user feedback as Github issues (to the Github repository of your choice)
        - [maoni-doorbell](callbacks/maoni-doorbell), to send user feedback to [Doorbell.io](https://doorbell.io/home)
       

Take a look at the [sample application](https://play.google.com/store/apps/details?id=org.rm3l.maoni.sample) 
for a quick overview.


## Motivations

While working on a new version of [DD-WRT Companion](https://ddwrt-companion.app/), 
one of my Android apps, I needed a simple yet pleasant way to collect user feedback, 
along with some contextual information.
I experimented with a simple dialog, then tried a bunch of other libraries, 
but could not find one with screenshot capturing capabilities, not vendor lock-in, 
and which is almost a no-brainer as to integrating with any remote services.
I was also looking for screen capture highlight / blackout capabilities, as in use for issue reporting in 
several apps from Google.

So as a way to give back to the Open Source community, 
I decided to create Maoni as a separate library project.

By the way, as a side note, Maoni is a Swahili word for comments or opinions.


## Sample App

<a href="https://play.google.com/store/apps/details?id=org.rm3l.maoni.sample">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>


## Preview

<img width="40%" src="https://raw.githubusercontent.com/rm3l/maoni/master/doc/screenshots/raw/maoni_2.3.1.gif"/>
    
<!--
<div align="center">
    <img width="30%" src="https://raw.githubusercontent.com/rm3l/maoni/master/doc/screenshots/raw/1_Maoni_main_activity.png"/>
    <img height="0" width="8px"/>
    <img width="30%" src="https://raw.githubusercontent.com/rm3l/maoni/master/doc/screenshots/raw/2_Maoni_main_activity_with_screenshot_logs_thumbnail.png"/>
    <img height="0" width="8px"/>
    <img width="30%" src="https://raw.githubusercontent.com/rm3l/maoni/master/doc/screenshots/raw/3_Maoni_main_activity_with_screenshot_touch_to_preview_highlight_blackout.png"/>
</div>
-->

## Getting started

Grab via Gradle, by adding this to your `build.gradle`:

```gradle
  dependencies {
    // ...
    implementation 'org.rm3l:maoni:8.1.0@aar'
  }
```

### Putting it together

Integrating with Maoni is intended to be seamless and straightforward for most existing 
Android applications.

Just leverage the fluent Maoni Builder to construct and start an Maoni instance at the right place 
within your application workflow (for example a button click listener, or a touch of a menu item).

For example, to start with just the defaults:
```java
    //The optional file provider authority allows you to 
    //share the screenshot capture file to other apps (depending on your callback implementation)
    new Maoni.Builder(<myContextObject>, MY_FILE_PROVIDER_AUTHORITY)
        .withDefaultToEmailAddress("feedback@my.company.com")
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling activity 
```

To customize every aspect of your Maoni activity, call the fluent methods of `Maoni.Builder`, e.g.:
```java
    // MyHandlerForMaoni is a custom implementation of Maoni.Handler, 
    // which is a shortcut interface for defining both a validator and listeners for Maoni
    final MyHandlerForMaoni myHandlerForMaoni = new MyHandlerForMaoni(MaoniSampleMainActivity.this);
    
    //The optional file provider authority allows you to 
    //share the screenshot capture file to other apps (depending on your callback implementation)
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withWindowTitle("Send Feedback") //Set to an empty string to clear it
        .withMessage("Hey! Love this app? We would love to hear from you.")
        .withExtraLayout(R.layout.my_feedback_activity_extra_content)
        .withHandler(myHandlerForMaoni) //Custom Callback for Maoni
        .withFeedbackContentHint("[Custom hint] Write your feedback here")
        .withIncludeScreenshotText("[Custom text] Include screenshot")
        .withTouchToPreviewScreenshotText("Touch To Preview and Edit")
        .withContentErrorMessage("Custom error message")
        .withScreenshotHint("Custom test: Lorem Ipsum Dolor Sit Amet...")
        //... there are other aspects you can customize
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling activity 
```

**You're good to go!** Maoni will take care of validating / collecting user feedback 
and call your callbacks implementations. 

#### Available callbacks
Some common callbacks for Maoni are available as external dependencies to include in your application.

##### [maoni-email](callbacks/maoni-email/README.md)

This callback opens up an Intent for sending an email with the feedback collected.
This is the default fallback listener used in case no other listener has been set explicitly.
In other words, you need not import `maoni-email` as an extra dependency.
Just import `maoni` as depicted above, and you're good to go.

Add this additional line to your `build.gradle`:

```gradle
  dependencies {
    // ...
    implementation 'org.rm3l:maoni:8.1.0@aar'
  }
```

And set it as the listener for your Maoni instance:
```java
    final org.rm3l.maoni.email.MaoniEmailListener emailListenerForMaoni = 
            new org.rm3l.maoni.email.MaoniEmailListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(emailListenerForMaoni) //Callback from maoni-email
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling activity 
```

Visit the dedicated [README](callbacks/maoni-email/README.md) for further details.


##### [maoni-slack](callbacks/maoni-slack/README.md)

This callback sends feedback collected to Slack via an [an incoming WebHook integration](https://my.slack.com/services/new/incoming-webhook).
 
 To use it, you must first [set up an incoming WebHook integration](https://my.slack.com/services/new/incoming-webhook), and grab the Webhook URL.

Add this additional line to your `build.gradle`:

```gradle
  dependencies {
    // ...
    implementation 'org.rm3l:maoni:8.1.0@aar'
    implementation 'org.rm3l:maoni-slack:8.1.0@aar'
  }
```

And set it as the listener for your Maoni instance
```java
    final org.rm3l.maoni.slack.MaoniSlackListener slackListenerForMaoni = 
            new org.rm3l.maoni.slack.MaoniSlackListener(...); //Pass the Slack WebHook URL, channel, ...
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(slackListenerForMaoni) //Callback from maoni-slack
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling activity 
```

Visit the dedicated [README](callbacks/maoni-slack/README.md) for further details.

##### [maoni-github](callbacks/maoni-github/README.md)

This callback sends feedback collected as a Github issue to a specified Github repository.

To use it, you will need to have an account there, and grab your Personal Access Token.
You may want to create a dedicated `reporter` user for that purpose.

Add this additional line to your `build.gradle`:

```gradle
  dependencies {
    implementation 'org.rm3l:maoni:8.1.0@aar'
    implementation 'org.rm3l:maoni-github:8.1.0@aar'
  }
```

And set it as the listener for your Maoni instance:
```java
    //Customize the maoni-github listener, with things like your user personal Access Token on Github
    final org.rm3l.maoni.github.MaoniGithubListener listenerForMaoni = 
            new org.rm3l.maoni.github.MaoniGithubListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-github
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

Visit the dedicated [README](callbacks/maoni-github/README.md) for further details.


##### [maoni-jira](callbacks/maoni-jira/README.md)

This callback sends feedback collected as a JIRA issue to a specified JIRA project.

You may want to create a dedicated `reporter` user on your JIRA Host for that purpose.

Add this additional line to your `build.gradle`:

```gradle
  dependencies {
    implementation 'org.rm3l:maoni:8.1.0@aar'
    implementation 'org.rm3l:maoni-jira:8.1.0@aar'
  }
```

And set it as the listener for your Maoni instance:
```java
    //Customize the maoni-jira listener, with things like your REST URL, username, password
    final org.rm3l.maoni.jira.MaoniJiraListener listenerForMaoni = 
            new org.rm3l.maoni.jira.MaoniJiraListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-jira
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

Visit the dedicated [README](callbacks/maoni-jira/README.md) for further details.


##### [maoni-doorbell](callbacks/maoni-doorbell/README.md)

This callback sends feedback collected to [Doorbell](https://www.doorbell.io).

To use it, you will need to have an account there, and grab your application ID and secret key.

Add this additional line to your `build.gradle`:

```gradle
  dependencies {
    // ...
    implementation 'org.rm3l:maoni:8.1.0@aar'
    implementation 'org.rm3l:maoni-doorbell:8.1.0@aar'
  }
```

And set it as the listener for your Maoni instance:
```java
    final org.rm3l.maoni.doorbell.MaoniDoorbellListener doorbellListenerForMaoni = 
            new org.rm3l.maoni.doorbell.MaoniDoorbellListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(doorbellListenerForMaoni) //Callback from maoni-doorbell
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling activity 
```

Visit the dedicated [README](callbacks/maoni-doorbell/README.md) for further details.


#### Sharing the files captured with other apps

The file provider authority specified in the `Maoni.Builder` constructor allows you to 
share the screenshot capture and logs files to other apps (depending on your callback implementation).
By default, Maoni stores the files captured in your application cache directory, 
but this is (again) entirely customizable.

You must declare a file content provider in your `AndroidManifest.xml` file with an explicit list 
of sharable directories for other apps to be able to read the screenshot file. 
For example:

- If you use AndroidX:

```xml
<application>
    <!-- ... -->
    <!-- If not defined yet, declare a file provider to be able to share screenshots captured by Maoni -->
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="com.mydomain.fileprovider"
        android:grantUriPermissions="true"
        android:exported="false">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/filepaths" />
    </provider>
</application>
```

- Otherwise:

```xml
<application>
    <!-- ... -->
    <!-- If not defined yet, declare a file provider to be able to share screenshots captured by Maoni -->
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.mydomain.fileprovider"
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
    <!-- By default, Maoni stores files captured (screenshots and logs) in the application cache directory. 
    So you must declare the path '.' as shareable. Specify something else if you are using a different path -->
    <cache-path name="maoni-shares" path="." />
    <!-- <files-path path="maoni-working-dir/" name="myCustomWorkingDirForMaoni" /> -->
</paths>
```

See [https://goo.gl/31nStZ](https://goo.gl/31nStZ) for further instructions on how to setup file sharing.


## Contribute and Improve Maoni!

Contributions and issue reporting are more than welcome. 
So to help out, do feel free to fork this repo and open up a pull request. 
I'll review and merge your changes as quickly as possible.

You can use [GitHub issues](https://github.com/rm3l/maoni/issues) to report bugs. 
However, please make sure your description is clear enough and has sufficient instructions 
to be able to reproduce the issue.

You can also use the [sample app](https://play.google.com/store/apps/details?id=org.rm3l.maoni.sample) 
to send your feedback with Maoni. ;-)

### Building from source

Make sure you have the Android SDK installed.

Also make sure you have the appropriate Build Tools installed. You can install them via the Android's `sdkmanager`:
```bash
sdkmanager "build-tools;28.0.3"
```

Now you can build the project with the `Gradle Wrapper`:
```bash
./gradlew lintDebug testDebug assembleDebug
```

You will then find the artifacts under the following folders:
- `maoni/build/outputs/aar/`
- `maoni-common/build/libs/`
- `maoni-sample/build/outputs/apk/`

### Translations
 
I use Crowdin as the translation system. All related resources for localization are automatically generated from files got with Crowdin. 

To help out with any translation, please head to [Crowdin](http://crowdin.net/project/maoni) 
and request to join the translation team. 
If your language is not listed there, just drop me an e-mail at &lt;apps+maoni@rm3l.org&gt;.

Please do **not** submit GitHub pull requests with translation fixes as any changes will be overwritten 
with the next update from Crowdin.

### Contributing callbacks for Maoni

You can create separate Android Library Projects that implement any of the Maoni callbacks interfaces 
(`Validator`, `Listener`, `UiListener`, `Handler` or any combination), 
so users can use them in their projects.

You just have to include `maoni-common` as a dependency in your project, e.g., with Gradle:

```gradle
  dependencies {
    // ...
    api 'org.rm3l:maoni-common:8.1.0@aar'
  }
```
You can write your project in any JVM language of your choice (e.g., [Kotlin](https://kotlinlang.org/), as with [maoni-slack](https://github.com/rm3l/maoni-slack) and [maoni-github](https://github.com/rm3l/maoni-github)), as long as the callback implementation can be called from Maoni.

#### Publishing a new release

All releases (Git tags) are published to [Bintray](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni).

To publish to Bintray, you need to have the appropriate rights. 
Additionally, your Bintray credentials are expected to be put on your local machine 
in `${HOME}/.droid/maoni.bintray.properties`, which should at least contain 
the following properties:
- `user` : the username used for publishing
- `key` : your Bintray API Key, which you can retrieve from your Bintray account

The following command can then be run to publish a new version:

```bash
./gradlew build javadoc generatePomFileForReleasePublication bintrayUpload
```

## In use in the following apps

(If you use Maoni, please drop me a line at &lt;apps+maoni@rm3l.org&gt; 
(or again, fork, modify this file and submit a pull request), so I can list your app(s) here)

* [DD-WRT Companion](https://ddwrt-companion.app)
* [DD-WRT Companion Tasker Plugin](https://play.google.com/store/apps/details?id=org.rm3l.ddwrt.tasker)
* [Androcker](https://play.google.com/store/apps/details?id=org.rm3l.container_companion)


## Credits

* [DrawableView](https://github.com/PaNaVTEC/DrawableView), by [Christian Panadero Martinez](https://github.com/PaNaVTEC)


## Developed by

* Armel Soro
  * [keybase.io/rm3l](https://keybase.io/rm3l)
  * [rm3l.org](https://rm3l.org) - &lt;apps+maoni@rm3l.org&gt; - [@rm3l](https://twitter.com/rm3l)
  * [paypal.me/rm3l](https://paypal.me/rm3l)
  * [coinbase.com/rm3l](https://www.coinbase.com/rm3l)


## Contributors

Thanks to the following people who help(ed) improve Maoni, 
either by suggesting translations or by reporting an issue and/or submitting pull requests.

In no particular order:

* [Marius Volkhart](https://github.com/MariusVolkhart)
* [Lean Rada](https://github.com/Kalabasa)
* [isacastillor](https://crowdin.com/profile/isacastillor)
* [bobsthinking](https://crowdin.com/profile/bobsthinking)
* [omersurer](https://crowdin.com/profile/omersurer)
* [ihtiht](https://crowdin.com/profile/ihtiht)
* [naofum](https://crowdin.com/profile/naofum)
* [fuzeh](https://crowdin.com/profile/sebastianbacia)
* [utopian.io/@kedi](https://crowdin.com/profile/Apsimati)
* [Dimitar Dinchev](https://github.com/ddinchev)
* [vlad-roid](https://github.com/vlad-roid)
* [gmiklos-inst](https://github.com/gmiklos-inst)


## License

    The MIT License (MIT)
    
    Copyright (c) 2016-2019 Armel Soro
    
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

