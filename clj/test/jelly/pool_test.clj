(ns jelly.pool-test
  (:use [jelly.pool] :reload-all)
  (:use [jelly.slaw] :reload-all)
  (:use [clojure.test]))

; For generating random strings
(def random (java.util.Random.))
(def charlist (map char (concat (range 48 58) (range 65 91) (range 97 123))))
(defn one-random-char []
  (nth charlist (.nextInt random (count charlist))))
(defn random-string [length]
  (apply str (take length (repeatedly one-random-char))))

; Pre-test setup
(def poolnom (str "clojuretest" (random-string 5)))
(create poolnom)

(deftest 
  pool-creation
  (is (contains? (pools) poolnom))
  (is (thrown? RuntimeException (create poolnom)))
  )

(deftest 
  slaw-varargs
  (are [x y] (= (seq x) (seq y))
       (make-slaw-vararg nil) (make-slaw-vararg [])
       (make-slaw-vararg '()) (make-slaw-vararg [])
       (make-slaw-vararg "hi") (slaw "hi")
       (make-slaw-vararg '("hi" "bye")) (make-slaw-vararg ["hi" "bye"])  
       ))

