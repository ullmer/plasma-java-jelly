* Directory contents

  This folder contains Oblong's Android apps using Jelly.

  - libs: jelly-standalone-0.1.jar, a copy of jelly bundled together with some libraries these demos depend on

  - imagine: a demo sending images to a remote pool.

  - ponder: a pond, i mean, pool monitor.

* App build instructions

*** Using Ant to build apps

    You'll need ant 1.8 or better.

    In each application you want to build, a file called
    `local.properties' must be created, containing the location of
    your android installation directory. The easiest way to create it
    is by is via the command:

       $ android update project -p $PWD

     more complete command for applications:

       $ android update project --subprojects -p ./ -n ${PWD##*/} --target android-8

     or for libraries

       $ android update lib-project -p ./

    run inside the app directory. That's it: you can use ant to
    compile and install the application. Use `ant -p` for a list
    of targets. For example to compile in debug mode:

        $ ant debug

    or you can compile and install the app to a connected device

        $ ant debug install

*** Complete example on Ubuntu 12.04

    The script ubuntu-build-demo.sh installs the Android SDK and prerequisites, then builds the samples.

*** Using Eclipse
>>>> Recent eclipse version (Juno, recommended):

     Once you have setup the environment and installed the ADT plugin open eclipse in a workspace then go to
     file -> import -> existing android code
     navigate to the folder you have downloaded jelly /droid and then chose one of the apps.

>>>> older eclipse versions:

    After installing the ADT plugin, just create a new Android
    project, and direct the wizard to use existing sources, pointing
    to the app directory (e.g., <top>/droid/imagine).

    You also need to add a new source directory, using Project
    properties > Java Build Path > Source > Link source, pointing to
    <top>/core/src (name it jelly-src, say), and add all the libraries
    in <top>/core/lib to your android app project (using Project
    properties > Java Build Path > Libraries > Add External Jars.

