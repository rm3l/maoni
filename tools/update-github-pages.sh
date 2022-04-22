#!/usr/bin/env bash
#
# Copyright (c) 2016-2022 Armel Soro
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

set -euxo pipefail

if [ "$CIRCLE_PULL_REQUEST" == "" ]; then
  echo -e "Starting gh-pages update...\n"

  #go to home and setup git
  cd $HOME
  git config --global user.email "${GIT_COMMIT_USER_EMAIL:-circle_ci@rm3l.org}"
  git config --global user.name "${GIT_COMMIT_USER_NAME:-'Circle CI'}"

  git clone --branch=gh-pages https://rm3l:$GITHUB_API_KEY@github.com/rm3l/maoni.git gh-pages > /dev/null

  cd gh-pages
  # Import README.md from master
  git show origin/master:README.md > index.md

  #add, commit and push files
  [[ -z $(git status --porcelain) ]] || \
      (
  git add index.md && \
  git commit -q -m "Automatic README.md => index.md import (build #$CIRCLE_BUILD_NUM)." -m "Commit $CIRCLE_SHA1" && \
  git push -q origin gh-pages > /dev/null )

  echo -e "... Done with updating gh-pages\n"
fi
