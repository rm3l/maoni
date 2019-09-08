[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:maoni-slack.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni-slack)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Getting started](#getting-started)
- [Caveats](#caveats)
- [Credits](#credits)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

**maoni-slack** aims at sending the valuable feedback of your app users to the specified Slack channel (using a Slack WebHook URL you will pass).


## Getting started

First set up [an incoming WebHook integration](https://my.slack.com/services/new/incoming-webhook) in Slack and copy the Webhook URL.

Now in your app, add this to your `build.gradle`:

```gradle
  dependencies {
    implementation 'org.rm3l:maoni:8.0.10@aar'
    implementation 'org.rm3l:maoni-slack:8.0.10@aar'
  }
```

And set it as the listener for your Maoni instance:
```java
    //Customize the maoni-slack listener, with things like your WebHook URL, ...
    final org.rm3l.maoni.slack.MaoniSlackListener listenerForMaoni = 
            new org.rm3l.maoni.slack.MaoniSlackListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-slack
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

**You're good to go!** Maoni will take care of validating / collecting your users' feedbacks 
and call maoni-slack as needed. 

## Caveats

At this time, Slack Incoming Webhook integration does not provide any ways to upload images or files.
Application logs are however read to a text and included as wehbook message attachment.
However, the capability to attach screen captures to user feedback is not supported 
by `maoni-slack`.
Please note however that you can always provide your own callback implementation by extending `MaoniSlackListener` class and 
overriding its `onSendButtonClicked(Feedback)` method, so as to:
1. upload the feedback attachments on any remote services of your choice; 
2. then update the `Feedback` object to include the attachments URLs;
3. and call `super.onSendButtonClicked(Feedback)` to post the user feedback on Slack.

## Credits

This is written in the excellent [Kotlin](https://kotlinlang.org/) programming language, and leverages some other excellent Open-Source libraries:
* [Anko](https://github.com/Kotlin/anko), by Kotlin
* [khttp](http://khttp.readthedocs.io/en/latest/#), by [jkcclemens](https://github.com/jkcclemens)
