#!/bin/sh
android update project --subprojects -p ./ -n ${PWD##*/} --target android-8


