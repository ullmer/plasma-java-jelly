(ns jelly.slaw-test
  (:use [jelly.slaw] :reload-all)
  (:use [clojure.test]))

(deftest round-trip
  (are [v] (= v (slaw-value (slaw v)))
       nil 1 -2 0 (byte 102) (float 2.323) 3.13342
       "foo" "a longer string" "" 
       '() '("x" 2 nil) 
       [1 2] [2.8 1.2 3.1] [1 2 3 4 5 6]
       {} {"key" 1 "key2" 2} {"key" [1 2 3] "keee" nil}
      
       )
  ; not currently supporting keywords
  (are [v] (not (= v (slaw-value (slaw v))))
     :hi {:foo 1})
  )

(deftest p-round-trip
  (are [p] (= p (protein (slaw-value p)))
    (protein [1 2 3] {"a" "b"})
    (protein {"descrips" [1 2 3] "ingests" {"a" "b"}})
    (protein nil {"a" "b"})
    (protein {"a" "b"} nil)
    (protein nil nil)
    ))

(deftest vecs
  (is (= [1 2] (slaw-value (slaw-vector 1 2))))
  (is (= [1.0 2.0 3.0] (slaw-value (slaw-vector 1.0 2.0 3.0))))
  (is (= [(float 1) (float 2) (float 3)]
         (slaw-value (slaw-vector (float 1) 2 3)))))

