[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:maoni-email.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni-email)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Getting started](#getting-started)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

**maoni-email** aims at opening up an Android email Intent, so users can send their feedback (along with the app screen capture and logs, if applicable) via email.

## Getting started

Add this to your `build.gradle`:

```gradle
  dependencies {
    implementation 'org.rm3l:maoni:8.0.10@aar'
    implementation 'org.rm3l:maoni-email:8.0.10@aar'
  }
```

And set it as the listener for your Maoni instance (at the right place within your application workflow):
```java
    //Customize the maoni-email listener, with things like the 'to', 'bcc', 'cc', 'subject', ... fields of the email
    final org.rm3l.maoni.email.MaoniEmailListener listenerForMaoni = 
            new org.rm3l.maoni.email.MaoniEmailListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-email
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

**You're good to go!** Maoni will take care of validating / collecting feedback
and call `maoni-email` as needed. 
