[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:maoni-jira.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni-jira)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Getting started](#getting-started)
- [Credits](#credits)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

**maoni-jira** aims at sending the valuable feedback of your app users to the specified JIRA host (as a JIRA issue).

## Getting started

Add this to your `build.gradle`:

```gradle
  dependencies {
    implementation 'org.rm3l:maoni:8.0.10@aar'
    implementation 'org.rm3l:maoni-jira:8.0.10@aar'
  }
```

And set it as the listener for your Maoni instance:
```java
    //Customize the maoni-jira listener, with things like the JIRA Host REST API Base URL and the credentials to use to connect
    final org.rm3l.maoni.github.MaoniJiraListener listenerForMaoni = 
            new org.rm3l.maoni.github.MaoniJiraListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-jira
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

**You're good to go!** Maoni will take care of validating / collecting your users' feedbacks 
and call maoni-jira as needed. 

## Credits

This is written in the excellent [Kotlin](https://kotlinlang.org/) programming language, and leverages some other excellent Open-Source libraries:
* [Anko](https://github.com/Kotlin/anko), by Kotlin
* [khttp](http://khttp.readthedocs.io/en/latest/#), by [jkcclemens](https://github.com/jkcclemens)
