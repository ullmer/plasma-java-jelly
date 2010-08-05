(ns jelly.main
  (:use [clojure main]
        [jelly slaw pool])
  (:gen-class))

(defn -main [& args]
  (println "Welcome to jelly's shell")
  (repl :init #(in-ns 'jelly.main)))
