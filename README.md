[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:maoni-doorbell.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Amaoni-doorbell) 
[![Travis branch](https://img.shields.io/travis/rm3l/maoni-doorbell/master.svg)](https://travis-ci.org/rm3l/maoni-doorbell) 
[![Coverage Status](https://coveralls.io/repos/github/rm3l/maoni-doorbell/badge.svg?branch=master)](https://coveralls.io/github/rm3l/maoni-doorbell?branch=master)  
[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat)](https://github.com/rm3l/maoni-doorbell/blob/master/LICENSE) 

[![GitHub watchers](https://img.shields.io/github/watchers/rm3l/maoni-doorbell.svg?style=social&label=Watch)](https://github.com/rm3l/maoni-doorbell) 
[![GitHub stars](https://img.shields.io/github/stars/rm3l/maoni-doorbell.svg?style=social&label=Star)](https://github.com/rm3l/maoni-doorbell) 
[![GitHub forks](https://img.shields.io/github/forks/rm3l/maoni-doorbell.svg?style=social&label=Fork)](https://github.com/rm3l/maoni-doorbell)

[**Maoni**](http://maoni.rm3l.org) is a lightweight open-source library for integrating 
a way to collect user feedbacks from within Android applications.

Maoni takes care of collecting user feedback using a beautiful and customizable activity. 
And anyone can provide callbacks that will perform the appropriate actions with the feedback collected.

**maoni-doorbell** is an example of such callback for Maoni. 
It aims at sending the valuable feedback of your app users (along with app screen capture and logs, if applicable) to [Doorbell](https://doorbell.io).


## Getting started

Please include Maoni dependency first, by reading the instructions provided [here](http://maoni.rm3l.org).

Then grab `maoni-doorbell` via Gradle, by adding this to your `build.gradle`:

```gradle
  dependencies {
    // ...
    //As this will be plugged as a callback for Maoni, it requires Maoni dependency as well.
    //See http://maoni.rm3l.org for more details.
    //compile ('org.rm3l:maoni:<appropriate_version>@aar') {
    //   transitive = true;
    //}
    compile 'org.rm3l:maoni-doorbell:2.4.0'
  }
```

And set it as the listener for your Maoni instance:
```java
    //Customize the maoni-doorbell listener, with things like your application ID/Key on Doorbell
    final org.rm3l.maoni.doorbell.MaoniDoorbellListener listenerForMaoni = 
            new org.rm3l.maoni.doorbell.MaoniDoorbellListener(...);
    
    new Maoni.Builder(MY_FILE_PROVIDER_AUTHORITY)
        .withListener(listenerForMaoni) //Callback from maoni-doorbell
        //...
        .build()
        .start(MaoniSampleMainActivity.this); //The screenshot captured is relative to this calling context 
```

**You're good to go!** Maoni will take care of validating / collecting your users' feedbacks 
and call maoni-doorbell as needed. 


## Contribute and Improve maoni-doorbell!

Contributions and issue reporting are more than welcome. 
So to help out, do feel free to fork this repo and open up a pull request. 
I'll review and merge your changes as quickly as possible.

You can use [GitHub issues](https://github.com/rm3l/maoni-doorbell/issues) to report bugs. 
However, please make sure your description is clear enough and has sufficient instructions 
to be able to reproduce the issue.

## Developed by

* Armel Soro
  * [keybase.io/rm3l](https://keybase.io/rm3l)
  * [rm3l.org](https://rm3l.org) - &lt;apps+maoni-doorbell@rm3l.org&gt; - [@rm3l](https://twitter.com/rm3l)
  * [paypal.me/rm3l](https://paypal.me/rm3l)
  * [coinbase.com/rm3l](https://www.coinbase.com/rm3l)

## Credits

This leverages some other excellent Open-Source libraries:
* [Retrofit](https://square.github.io/retrofit/) and [OkHttp](http://square.github.io/okhttp/), by Square
* [Needle](http://zsoltsafrany.github.io/needle/), by [ZsoltSafrany](https://github.com/ZsoltSafrany)

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

