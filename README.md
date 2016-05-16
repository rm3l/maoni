TODO - Documentation in Progress

[![Build Status](https://travis-ci.org/rm3l/maoni.svg?branch=master)](https://travis-ci.org/rm3l/maoni)

Maoni is a lightweight library for integrating a way to collect user feedbacks from within Android applications.
Using a beautiful Material-ized activity, it allows to capture a screenshot of the current activity and attach it to the feedback.

Just provide callback implementations and Maoni will take care of collecting your user feedbacks 
and call your callbacks.

= Usage = 

Add this to your build.gradle:

```gradle
repositories {
    //...
    
    maven {
        url 'https://dl.bintray.com/rm3l/maven'
    }
}

dependencies {
    // ...
    
    compile 'org.rm3l:maoni:1.0@aar'
}
```

And refer to MaoniBuilder to start Maoni.

Please also check out the sample app for more details.
