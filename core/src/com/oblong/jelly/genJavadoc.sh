javadoc --ignore-source-errors \
  -cp ~/git/plasma-java-jelly/lib/jcip-annotations.jar:~/git/plasma-java-jelly/lib/log4j-1.2.17.jar:~/git/plasma-java-jelly/lib/ob-tls-helpers.jar:~/git/plasma-java-jelly/lib/snakeyaml-1.7.jar \
 `find .              -name '*.java'` \
 `find ../util        -name '*.java'` \
 `find ../../../javax -name '*.java'` |&tee javadoc.log
