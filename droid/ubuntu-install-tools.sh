#!/bin/sh
#
# Script showing how to install an Android environment on a
# completely fresh 64 bit Ubuntu 12.04 machine
# Your mileage may vary.

set -e
set -x

for JAVA_HOME in /usr/lib/jvm/java-6-openjdk-amd64 /usr/lib/jvm/java-6-openjdk
do
    test -d $JAVA_HOME && break
done
export JAVA_HOME

ANDROID_HOME=`pwd`/android-sdk-linux
export ANDROID_HOME

PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${JAVA_HOME}/bin

# Install prerequisite tools

if test "`which ant`" = ""
then
    sudo apt-get install -y ant
fi

if test "`which java`" = ""
then
    sudo apt-get install -y openjdk-6-jre ia32-libs
fi

if test "`which javac`" = ""
then
    sudo apt-get install -y openjdk-6-jdk
fi

if ! test -x $ANDROID_HOME/tools/android
then
    # This is probably out of date; you might want to get a more recent SDK
    if ! test -f android-sdk_r20.0.3-linux.tgz
    then
	wget http://dl.google.com/android/android-sdk_r20.0.3-linux.tgz
    fi

    rm -rf android-sdk-linux
    tar -xzf android-sdk_r20.0.3-linux.tgz

    # See http://tools.android.com/recent/sdkmanagerchangesintoolsr20
    #android --clear-cache sdk
    rm -rf ~/.android/cache

    # See http://code.google.com/p/android/issues/detail?id=19504
    # IDs are from following command:
    android list sdk --extended
    # Download the things your particular project needs
    android update sdk -u --filter platform-tools,android-8

    killall adb || true
fi

# Oddly, the aapt tool needs even more 32 bit libraries installed.
# aapt looks like a typo, but it's not
aapt 2>/dev/null || sudo apt-get install -y libc6:i386 zlib1g:i386 libstdc++6:i386
