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

if [ "$CIRCLE_PULL_REQUEST" == "" ]; then

  echo -e "Starting translation import...\n"

  #go to home and setup git
  cd $HOME
  git config --global user.email "circle_ci@rm3l.org"
  git config --global user.name "Cir Cle"

  git clone --branch=master https://$GITHUB_API_KEY@github.com/rm3l/maoni.git master > /dev/null

  cd master
  wget https://crowdin.com/downloads/crowdin-cli.jar
  # Update sources
  java -jar crowdin-cli.jar upload sources
  # Download translations
  java -jar crowdin-cli.jar download
  rm crowdin-cli.jar

  # import listing graphics
  for playLangPath in ./maoni-sample/src/main/play/*-*; do
    mkdir -p ${playLangPath}/listing
    cp -r ./maoni-sample/src/main/play/en-US/listing/* ${playLangPath}/listing/
  done

  #add, commit and push files
  # git add .
  # git remote rm origin
  # git remote add origin https://rm3l:$GITHUB_API_KEY@github.com/rm3l/maoni.git
  git add .
  git commit -m "Automatic translation import (build #$CIRCLE_BUILD_NUM)." \
    -m "Commit $CIRCLE_SHA1"
  git pull --rebase
  git push origin master 2>&1

  echo -e "... Done with importing translations from Crowdin\n"
fi
