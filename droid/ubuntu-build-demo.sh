#!/bin/sh
#
# Script showing how to build the Android jelly demos on a
# completely fresh 64 bit Ubuntu 12.04 machine.
# See README.txt

set -e
set -x

# Get the tools, if you haven't already
sh ubuntu-install-tools.sh

for JAVA_HOME in /usr/lib/jvm/java-6-openjdk-amd64 /usr/lib/jvm/java-6-openjdk
do
    test -d $JAVA_HOME && break
done
export JAVA_HOME

ANDROID_HOME=`pwd`/android-sdk-linux
export ANDROID_HOME

PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${JAVA_HOME}/bin

for demo in imagine ponder
do
    cd $demo
    android update project --subprojects -p ./ -n $demo --target android-8
    ant debug
    # ant install
    cd ..
done
