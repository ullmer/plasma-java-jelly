(defproject jelly "0.1-SNAPSHOT"
  :description "jelly's Clojure bindings and REPL"

  :dependencies [[org.clojure/clojure "1.2.0-RC1"]
                 [org.clojure/clojure-contrib "1.2.0-RC1"]
                 [net.jcip/jcip-annotations "1.0"]
                 [org.slf4j/slf4j-api "1.6.1"]
                 [org.slf4j/slf4j-jdk14 "1.6.1"]]

  :dev-dependencies [[swank-clojure "1.2.1"]
                     [lein-javac "1.2.1-SNAPSHOT"]]

  :disable-implicit-clean true

  :java-source-path "../src"
  :javac-fork "true"
  :javac-target "1.6"
  :javac-debug "true"

  :main jelly.main)
