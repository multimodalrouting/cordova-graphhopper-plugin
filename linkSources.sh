#!/bin/bash

WORKDIR=$(pwd)
DESTDIR=$1
PLUGINDIR="cordova-graphhopper-plugin"

IFS="
"

for matches in `grep ".*<source-file" plugin.xml`;do
    SRC=$(echo ${matches} | sed 's/.*src="\(.*\)" target-dir="\(.*\)".*/\1/')
    DEST=$(echo ${matches} | sed 's/.*src="\(.*\)" target-dir="\(.*\)".*/\2/')
    echo mkdir -p "${DESTDIR}/${DEST}"
    mkdir -p "${DESTDIR}/${DEST}"
    echo ln -fs "${WORKDIR}/${SRC}" "${DESTDIR}/${DEST}"
    ln -fs "${WORKDIR}/${SRC}" "${DESTDIR}/${DEST}"
done;


mkdir -p "${DESTDIR}/platforms/android/app/src/main/assets/www/plugins/${PLUGINDIR}"
ln -sf "${WORKDIR}/www" "${DESTDIR}/platforms/android/app/src/main/assets/www/plugins/${PLUGINDIR}/www"




