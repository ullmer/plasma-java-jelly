(defproject jelly "0.1-SNAPSHOT"
  :description "jelly's Clojure bindings and REPL"

  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [net.jcip/jcip-annotations "1.0"]
                 [org.slf4j/slf4j-api "1.6.1"]
                 [org.slf4j/slf4j-jdk14 "1.6.1"]]

  :dev-dependencies [[swank-clojure "1.2.1"]
                     [lein-javac "1.2.1-SNAPSHOT"]]

  :disable-implicit-clean true

  :java-source-path "../core/src"
  :java-options {:debug "true" :fork "true" :target "1.6"}

  :main jelly.main)
