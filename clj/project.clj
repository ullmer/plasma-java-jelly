(defproject jelly "0.1.5-SNAPSHOT"
  :description "jelly's Clojure bindings and REPL"
  :repositories {"local" "file:repo"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.codec "0.1.0"]
		 [jelly-standalone "0.1"]]

  :dev-dependencies [[swank-clojure "1.3.4"]
                     [lein-marginalia "0.7.0"]]

  :disable-implicit-clean false

  :java-options {:debug "true" :fork "true" :target "1.6"}

  :main jelly.main)
