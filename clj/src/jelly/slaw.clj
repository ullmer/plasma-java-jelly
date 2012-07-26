(ns jelly.slaw
  (:import [java.io FileOutputStream]
           [com.oblong.jelly Slaw Protein NumericIlk]
           [com.oblong.jelly.slaw.java SlawNil SlawBool SlawString SlawNumber
                                       SlawCons SlawComplex SlawList SlawMap
                                       SlawVector SlawArray]))

;;  Taking a page from David Nolen, http://dosync.posterous.com/51626638
(defprotocol ISeqable
  (seqable? [x]))

(defmacro extend-to-seqable [& xs]
  `(extend-protocol ISeqable
    ~@(mapcat (fn [x] [x `(~'seqable? [~'x] true)]) xs)))

(extend-to-seqable clojure.lang.ISeq clojure.lang.Seqable
   Iterable CharSequence String java.util.Map nil)

(extend-type Object
  ISeqable
  (seqable? [x]
            (let [c (.getClass x)]
             (if (.isArray c)
               (do
                 (extend-type (.getClass x)
                   ISeqable
                   (seqable? [x] true))
                 true)
               false))))

;; Slaw constructors
; known issue: putting 'symbols into slaw doesn't work
; todo: what to do with this?  
;       keywords get coerced to strings, so:
; (slaw :boo)
; ;=> #<SlawString !!string "boo">
(defmulti slaw (fn [& args] (into [] (map class args))))

(derive Slaw ::slaw)
(defmethod slaw [::slaw] [s] s)

(defmethod slaw [nil]     [x] (Slaw/nil))
(defmethod slaw [Boolean] [b] (Slaw/bool b))
(defmethod slaw [String]  [s] (Slaw/string s))
(defmethod slaw [Byte]    [n] (Slaw/int8 n))
(defmethod slaw [Short]   [n] (Slaw/int16 n))
(defmethod slaw [Integer] [n] (Slaw/int32 n))
(defmethod slaw [Long]    [n] (Slaw/int64 n))
(defmethod slaw [Float]   [n] (Slaw/float32 n))
(defmethod slaw [Double]  [n] (Slaw/float64 n))
(defmethod slaw [BigInteger] [n] (Slaw/int64 n))

(defmethod slaw [clojure.lang.Keyword] [s] (Slaw/string (name s)))

(derive Number ::nummy)
(defmethod slaw [::nummy ::nummy] [a b] (Slaw/complex (slaw a) (slaw b)))

(def slaw-ilks {:int8 NumericIlk/INT8
                :unt8 NumericIlk/UNT8
                :int16 NumericIlk/INT16
                :unt16 NumericIlk/UNT16
                :int32 NumericIlk/INT32
                :unt32 NumericIlk/UNT32
                :int64 NumericIlk/INT64
                :unt64 NumericIlk/UNT64
                :float32 NumericIlk/FLOAT32
                :float64 NumericIlk/FLOAT64
                :float NumericIlk/FLOAT32
                :double NumericIlk/FLOAT64})

(defmethod slaw [::nummy clojure.lang.Keyword] [n ilk]
  (if (= ilk :unt64)
    (Slaw/unt64 (BigInteger/valueOf n))
    (let [i (ilk slaw-ilks)]
      (Slaw/number i (if (. i isIntegral) (long n) (double n))))))

(derive Object ::any)
(defmethod slaw [::any ::any] [a b] (Slaw/cons (slaw a) (slaw b)))

(prefer-method slaw [::nummy ::nummy] [::any ::any])
(prefer-method slaw [::nummy clojure.lang.Keyword] [::any ::any])

(defmethod slaw [clojure.lang.PersistentArrayMap] [v]
  (Slaw/map (map slaw (apply concat v))))

(defmethod slaw [clojure.lang.PersistentHashMap] [v]
  (Slaw/map (map slaw (apply concat v))))

(defmethod slaw :default [v]
  (if (seqable? v) (Slaw/list (doall (map slaw v)))
                   (Slaw/list '())))

(defn slaw-vector
  ([x y]     (Slaw/vector (slaw x) (slaw y)))
  ([x y z]   (Slaw/vector (slaw x) (slaw y) (slaw z)))
  ([x y z w] (Slaw/vector (slaw x) (slaw y) (slaw z) (slaw w))))

(defn protein
  ([prot-map]
    (if (or (find prot-map "descrips") (find prot-map "ingests"))
      (protein (prot-map "descrips") (prot-map "ingests"))))
  ([descrips ingests] (protein descrips ingests nil))
  ([descrips ingests ^bytes rude-data]
    (Slaw/protein (slaw (apply vector descrips))
                  (slaw ingests)
                  rude-data)))

; todo: make it so this works: (.deposit hh (protein "hoohklh" nil))
(defn protein
  ([prot-map]
    ;(println "protein factory!" prot-map)
    (if (or (prot-map "descrips") (prot-map "ingests"))
      (protein (prot-map "descrips") (prot-map "ingests"))
      ;(protein (prot-map descrips) (prot-map ingests))
      ))
  ([descrips ingests] (protein descrips ingests nil))
  ([descrips ingests ^bytes rude-data]
    (Slaw/protein (slaw (apply vector descrips))
                  (slaw ingests)
                  rude-data)))

(defn index [p] (.index p))
(defn stamp [p] (.timestamp p))

(defn ingests  [p] (.ingests p))
(defn descrips [p] (.descrips p))

(defn rogue-datum     [p n] (.datum p n))
(defn rogue-data      [p] (.copyData p))
(defn rogue-data-len  [p] (.dataLength p))
(defn save-rogue-data [p f]
  (with-open [os (FileOutputStream. f)] (.write os (rogue-data p))))

(defmulti slaw-value class)

(defmethod slaw-value SlawNil    [s] nil)
(defmethod slaw-value SlawBool   [b] (.emitBoolean b))
(defmethod slaw-value SlawString [s] (.emitString s))
(defmethod slaw-value SlawNumber [n]
  (let [ilk (.numericIlk n)]
    (if (.isIntegral ilk)
      (if (and (not (.isSigned ilk)) (> (.width ilk) 4))
        (.emitBigInteger n)
        (.emitLong n))
      (.emitDouble n))))

(defn- pair-value [s] (list (slaw-value (.car s)) (slaw-value (.cdr s))))
(defn- key-value  [s] [(slaw-value (.car s)) (slaw-value (.cdr s))])
(defn- list-value [s] (map #(slaw-value (.nth s %1)) (range (.count s))))

(defmethod slaw-value SlawCons    [s] (pair-value s))
(defmethod slaw-value SlawComplex [s] (pair-value s))
(defmethod slaw-value SlawVector  [s] (into [] (list-value s)))
(defmethod slaw-value SlawArray   [s] (into [] (list-value s)))
(defmethod slaw-value SlawList    [s] (list-value s))

(defmethod slaw-value SlawMap [s]
  (into {} (map #(key-value (.nth s %1)) (range (.count s)))))

(defmethod slaw-value Protein [p]
  {"descrips" (slaw-value (.descrips p))
   "ingests"  (slaw-value (.ingests p))})

(defmethod slaw-value :default [p]
  ;(println "slaw-value" p "ended up at its default -- did you want that?")
  ())
