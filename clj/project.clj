(defproject jelly "0.1.5-SNAPSHOT"
  :description "jelly's Clojure bindings and REPL"

  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.codec "0.1.0"]
                 [org.yaml/snakeyaml "1.7"]
                 [net.jcip/jcip-annotations "1.0"]
                 [log4j/log4j "1.2.14"]]

  :dev-dependencies [[swank-clojure "1.3.4"]
                     [lein-marginalia "0.7.0"]]

  :disable-implicit-clean false

  :java-source-path "../core/src"
  :java-options {:debug "true" :fork "true" :target "1.6"}

  :main jelly.main)
