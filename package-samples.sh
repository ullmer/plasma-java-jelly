# Package the Android examples for use on https://platform-int.oblong.com/download/java
# Assumes you've already built jelly-standalone-0.1.jar

rm -rf jelly-droid-samples
mkdir -p jelly-droid-samples/

cp -a droid/imagine jelly-droid-samples/
cp -a droid/ponder jelly-droid-samples/
cp droid/*.sh jelly-droid-samples
rm -f jelly-droid-samples/ponder/custom_rules.xml
rm -f jelly-droid-samples/imagine/custom_rules.xml
rm -f jelly-droid-samples/ponder/local.properties
rm -f jelly-droid-samples/imagine/local.properties
rm -rf jelly-droid-samples/ponder/libs
rm -rf jelly-droid-samples/imagine/libs

# NOTE: we probably want to distribue just one version of jelly, so
# I'd like to just get rid of jelly-standalone, and
# have the samples use the same jelly.jar that we distribute.
mkdir jelly-droid-samples/libs
cp -a core/dist/jelly-standalone* jelly-droid-samples/libs
# NOTE: this won't work on windows
ln -s ../libs jelly-droid-samples/imagine/libs
ln -s ../libs jelly-droid-samples/ponder/libs

cp droid/README-user.txt jelly-droid-samples/README.txt

tar -czvf jelly-droid-samples.tar.gz jelly-droid-samples
