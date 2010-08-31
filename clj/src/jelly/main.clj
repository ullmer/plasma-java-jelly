(ns jelly.main
  (:require [clojure.main])
  (:use [jelly slaw pool])
  (:gen-class))

(defn -main [& args]
  (println "Welcome to jelly's shell")
  (clojure.main/repl :init #(in-ns 'jelly.main)))
