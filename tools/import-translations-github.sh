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

echo -e "Starting translation import...\n"

#go to home and setup git
cd $HOME
git config --global user.name 'github-actions[bot]'
git config --global user.email 'github-actions[bot]@users.noreply.github.com'

git clone --branch=master https://rm3l:$GITHUB_API_KEY@github.com/rm3l/maoni.git master > /dev/null

cd master
mkdir -p crowdin-cli-gh
cd crowdin-cli-gh
wget -q https://github.com/crowdin/crowdin-cli/releases/download/3.19.4/crowdin-cli.zip
unzip -qj crowdin-cli.zip
cd ..

# Update sources
java -jar ./crowdin-cli-gh/crowdin-cli.jar upload sources
# Download translations
java -jar ./crowdin-cli-gh/crowdin-cli.jar download

rm -rf ./crowdin-cli-gh

# import listing graphics
for playLangPath in ./maoni-sample/src/main/play/*-*; do
  # Crowdin reformats the 'title' file, which makes it unusable by the Play Publisher Gradle plugin
  awk 'BEGIN{f=1} /#X-Generator/{f=0} f{print} $0{f=1}' ${playLangPath}/listing/title > ${playLangPath}/listing/title.tmp
  mv ${playLangPath}/listing/title.tmp ${playLangPath}/listing/title
  sed -i 's/Maoni=//g' ${playLangPath}/listing/title
  for d in featureGraphic icon phoneScreenshots promoGraphic sevenInchScreenshots tenInchScreenshots tvBanner tvScreenshots; do
    cp -r ./maoni-sample/src/main/play/en-US/listing/${d} ${playLangPath}/listing/ || true
  done
done

#Sanitize, so it compiles with what we have in Google Play Store
rm -rf ./maoni-sample/src/main/play/af && mv ./maoni-sample/src/main/play/af-ZA ./maoni-sample/src/main/play/af
rm -rf ./maoni-sample/src/main/play/ar && mv ./maoni-sample/src/main/play/ar-SA ./maoni-sample/src/main/play/ar
rm -rf ./maoni-sample/src/main/play/ca && mv ./maoni-sample/src/main/play/ca-ES ./maoni-sample/src/main/play/ca
rm -rf ./maoni-sample/src/main/play/ro && mv ./maoni-sample/src/main/play/ro-RO ./maoni-sample/src/main/play/ro
rm -rf ./maoni-sample/src/main/play/sr && mv ./maoni-sample/src/main/play/sr-SP ./maoni-sample/src/main/play/sr
rm -rf ./maoni-sample/src/main/play/uk && mv ./maoni-sample/src/main/play/uk-UA ./maoni-sample/src/main/play/uk
rm -rf ./maoni-sample/src/main/play/vi && mv ./maoni-sample/src/main/play/vi-VN ./maoni-sample/src/main/play/vi
rm -rf ./maoni-sample/src/main/play/{he-IL,af-ZA,ar-SA,ca-ES,ro-RO,sr-SP,uk-UA,vi-VN}
  #rm -rf ./maoni-sample/src/main/play/{ar,el-GR,fi-FI,nl-NL,ca,pt-PT,sr,it-IT,fr-FR,ja-JP,ru-RU,no-NO,da-DK,uk,sv-SE,de-DE,pl-PL,cs-CZ,zh-CN,es-ES,vi,zh-TW,hu-HU,tr-TR,pt-BR,af,ro,ko-KR,ar}/whatsnew

[[ -z $(git status --porcelain) ]] || \
  ( git add . && \
  git commit -m "Automatic translation import (build #$CI_BUILD_NUM)" \
    -m "Commit $CI_SHA1" \
    -m "Co-authored-by: $GITHUB_ACTOR <$GITHUB_ACTOR@users.noreply.github.com>" && \
  git pull --rebase && \
  git push origin master 2>&1 )

echo -e "... Done with importing translations from Crowdin\n"