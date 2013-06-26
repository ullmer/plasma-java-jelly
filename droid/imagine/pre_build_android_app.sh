#!/bin/sh
#command to run before building app
android update project --subprojects -p ./ -n ${PWD##*/} --target android-8


