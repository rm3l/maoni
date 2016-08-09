#!/usr/bin/env bash
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

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting translation import...\n"

  #go to home and setup git
  cd $HOME
  git config --global user.email "travis_ci@rm3l.org"
  git config --global user.name "Tra Vis"

  git clone --branch=master https://$GITHUB_API_KEY@github.com/rm3l/maoni.git master > /dev/null

  cd master
  wget https://crowdin.com/downloads/crowdin-cli.jar
  # Update sources
  java -jar crowdin-cli.jar upload sources
  # Download translations
  java -jar crowdin-cli.jar download
  rm crowdin-cli.jar

  #add, commit and push files
  git add -f .
  git remote rm origin
  git remote add origin https://rm3l:$GITHUB_API_KEY@github.com/rm3l/maoni.git
  git add -f .
  git commit -q -m "Automatic translation import (build #$TRAVIS_BUILD_NUMBER)." \
    -m "Commit $TRAVIS_COMMIT"
  git push -q -f origin master > /dev/null

  echo -e "... Done with importing translations from Crowdin\n"
fi
