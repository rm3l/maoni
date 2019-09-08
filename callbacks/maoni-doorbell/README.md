[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:maoni-doorbell.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni-doorbell)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Getting started](#getting-started)
- [Credits](#credits)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

**maoni-doorbell** aims at sending the valuable feedback of your app users (along with app screen capture and logs, if applicable) to [Doorbell](https://doorbell.io).

## Getting started

Add this to your `build.gradle`:

```gradle
  dependencies {
    implementation 'org.rm3l:maoni:8.0.10@aar'
    implementation 'org.rm3l:maoni-doorbell:8.0.10@aar'
  }
```

And set it as the listener for your Maoni instance:
```java
    //Customize the maoni-doorbell listener, with things like your Application ID/Key on Doorbell
    final org.rm3l.maoni.doorbell.MaoniDoorbellListener listenerForMaoni = 
            new org.rm3l.maoni.doorbell.MaoniDoorbellListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-doorbell
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

**You're good to go!** Maoni will take care of validating / collecting feedback
and call `maoni-doorbell` as needed. 

## Credits

This leverages some other excellent Open-Source libraries:
* [Retrofit](https://square.github.io/retrofit/) and [OkHttp](http://square.github.io/okhttp/), by Square
* [Needle](http://zsoltsafrany.github.io/needle/), by [ZsoltSafrany](https://github.com/ZsoltSafrany)
