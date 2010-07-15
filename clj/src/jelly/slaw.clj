(ns jelly.slaw
  (:import (com.oblong.jelly Slaw Protein)
           (com.oblong.jelly.slaw SlawNil SlawBool SlawString SlawNumber
                                  SlawCons SlawComplex SlawList SlawMap
                                  SlawVector)))

;; Slaw constructors
(defmulti slaw (fn [& args] (into [] (map class args))))

(derive Slaw ::slaw)
(defmethod slaw [::slaw] [s] s)

(defmethod slaw [nil] [x] (Slaw/nil))
(defmethod slaw [Boolean] [b] (Slaw/bool b))
(defmethod slaw [String] [s] (Slaw/string s))

(defmethod slaw [Byte Boolean] [n u] (if u (Slaw/unt8 n) (Slaw/int8 n)))
(defmethod slaw [Short Boolean] [n u] (if u (Slaw/unt16 n) (Slaw/int16 n)))
(defmethod slaw [Integer Boolean] [n u] (if u (Slaw/unt32 n) (Slaw/int32 n)))
(defmethod slaw [Long Boolean] [n u] (if u (Slaw/unt64 n) (Slaw/int64 n)))
(defmethod slaw [BigInteger Boolean]
  [n u] (if u (Slaw/unt64 n) (Slaw/int64 n)))

(defmethod slaw [Float] [n] (Slaw/float32 n))
(defmethod slaw [Double] [n] (Slaw/float64 n))

(defmethod slaw [Byte] [n] (Slaw/int8 n))
(defmethod slaw [Short] [n] (Slaw/int16 n))
(defmethod slaw [Integer] [n] (Slaw/int32 n))
(defmethod slaw [Long] [n] (Slaw/int64 n))
(defmethod slaw [BigInteger] [n] (Slaw/int64 n))
(defmethod slaw [Float] [n] (Slaw/float32 n))
(defmethod slaw [Double] [n] (Slaw/float64 n))

(defmethod slaw [BigInteger] [n] (Slaw/int64 n))
(defmethod slaw [clojure.lang.Keyword] [s] (Slaw/string (name s)))

(derive Number ::nummy)
(defmethod slaw [::nummy ::nummy] [a b] (Slaw/complex (slaw a) (slaw b)))

(derive Object ::any)
(defmethod slaw [::any ::any] [a b] (Slaw/cons (slaw a) (slaw b)))

(prefer-method slaw [::nummy ::nummy] [::any ::any])

(defmethod slaw [clojure.lang.PersistentList] [v] (Slaw/list (map slaw v)))

(defn slaw-vect
  ([x y] (Slaw/vector x y))
  ([x y z] (Slaw/vector x y z))
  ([x y z w] (Slaw/vector x y z w)))

(defmethod slaw [clojure.lang.PersistentVector] [v]
  (apply slaw-vect (map slaw v)))

(defmethod slaw [clojure.lang.PersistentArrayMap] [v]
  (Slaw/map (map slaw (apply concat v))))

(defn protein [descrips ingests]
  (Slaw/protein (slaw descrips) (slaw ingests)))

(defn index [p] (.index p))
(defn stamp [p] (and (instance? Protein p) (.timestamp p)))

(defn ingests [p] (and (instance? Protein p) (.ingests p)))
(defn descrips [p] (and (instance? Protein p) (.descrips p)))

(defmulti slaw-value class)

(defmethod slaw-value SlawNil [s] nil)
(defmethod slaw-value SlawBool [b] (.emitBoolean b))
(defmethod slaw-value SlawString [s] (.emitString s))
(defmethod slaw-value SlawNumber [n]
  (if (.. n numericIlk isIntegral)
    (if (> (.. n numericIlk width) 4) (.emitBigInteger n) (.emitLong n))
    (.emitDouble n)))

(defn- pair-value [s] (list (slaw-value (.car s)) (slaw-value (.cdr s))))
(defn- key-value [s] [(slaw-value (.car s)) (slaw-value (.cdr s))])
(defn- list-value [s] (map #(slaw-value (.nth s %1)) (range (.count s))))

(defmethod slaw-value SlawCons [s] (pair-value s))

(defmethod slaw-value SlawComplex [s] (pair-value s))

(defmethod slaw-value SlawVector [s] (into [] (list-value s)))

(defmethod slaw-value SlawList [s] (list-value s))

(defmethod slaw-value SlawMap [s]
  (into {} (map #(key-value (.nth s %1)) (range (.count s)))))

(defmethod slaw-value Protein [p]
  [(slaw-value (.descrips p)) (slaw-value (.ingests p))])
