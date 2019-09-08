[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:maoni-github.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni-github)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Getting started](#getting-started)
- [Caveats](#caveats)
- [Credits](#credits)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

**maoni-github** aims at sending the valuable feedback of your app users to the specified Github repository (as a Github issue).


## Getting started

Add this to your `build.gradle`:

```gradle
  dependencies {
    implementation 'org.rm3l:maoni:8.0.10@aar'
    implementation 'org.rm3l:maoni-github:8.0.10@aar'
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

**You're good to go!** Maoni will take care of validating / collecting your users' feedbacks 
and call maoni-github as needed. 

## Caveats

At this time, the Github API does not provide any ways to upload images or files to issues created. 
As such, the capability to attach screen captures and application logs to user feedback is not supported 
by `maoni-github`.
Please note however that you can always provide your own callback implementation by extending `MaoniGithubListener` class and 
overriding its `onSendButtonClicked(Feedback)` method, so as to:
1. upload the feedback attachments on any remote services of your choice; 
2. then update the `Feedback` object to include the attachments URLs;
3. and call `super.onSendButtonClicked(Feedback)` to create the issue on your Github repo.

## Credits

This is written in the excellent [Kotlin](https://kotlinlang.org/) programming language, and leverages some other excellent Open-Source libraries:
* [Anko](https://github.com/Kotlin/anko), by Kotlin
* [khttp](http://khttp.readthedocs.io/en/latest/#), by [jkcclemens](https://github.com/jkcclemens)
