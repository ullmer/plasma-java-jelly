(ns jelly.pool
  (:use [jelly.slaw])
  (:import [com.oblong.jelly Pool Slaw PoolAddress PoolServerAddress
                             PoolServer PoolServers PoolOptions]))

(declare ^:dynamic *current-server*)

(defn get-server [svr]
  (cond (not svr) *current-server*
        (string? svr) (PoolServers/get (PoolServerAddress/fromURI svr))
        (instance? PoolServerAddress svr) (PoolServers/get svr)
        (instance? PoolAddress svr) (PoolServers/get (.serverAddress svr))
        (instance? PoolServer svr) svr))

(def ^:dynamic *current-server* (get-server "tcp://localhost:65456"))

(defmacro with-server [s & body]
  `(binding [*current-server* (get-server ~s)]
     ~@body))

(defn pool-opts
  ([] (PoolOptions. nil))
  ([size] (PoolOptions. size))
  ([size icap] (PoolOptions. size icap)))

(defn pools
  ([] (pools false))
  ([server] (into #{} (.pools (get-server server)))))

(defn- make-address [server name]
  (PoolAddress. (.address (get-server server)) name))

(defn create
  ([name] (create name (pool-opts) false))
  ([name opts] (create name opts false))
  ([name opts server] (Pool/create (make-address server name) opts)))

(defn dispose
  ([name] (dispose name false))
  ([name server] (Pool/dispose (make-address server name))))

(defn participate
  ([name] (participate name false))
  ([name server] (Pool/participate (make-address server name))))

(defn participate*
  ([name] (participate* name (pool-opts) false))
  ([name opts] (participate* name opts false))
  ([name opts server] (Pool/participate (make-address server name) opts)))

(defn participate-creatingly
  ([name] (try (create name)
               (catch Exception e))
          (participate name))
  ([name opts] (try (create name opts)
                    (catch Exception e))
               (participate name)))

(defmacro with-hose [hs & body]
  `(let [~(first hs) (participate ~(second hs))]
     (try ~@body (finally (withdraw ~(first hs))))))

(defmacro def-hose-funs [& ps]
 `(do ~@(map (fn [x] `(defn ~(first x) ~(nth x 2) (~(second x) ~@(nth x 2))))
             ps)))

; todo: think about wrapping exceptions

(def-hose-funs
  (version .version [hose])
  (hose-name .name [hose])
  (set-hose-name! .setName [hose new-name])
  (is-connected .isConnected [hose])
  (withdraw .withdraw [hose])
  (current-index .index [hose])
  (seek-to .seekTo [hose idx])
  (seek-by .seekBy [hose offset])
  (to-last .toLast [hose])
  (run-out .runOut [hose])
  (rewind .rewind [hose])
  (deposit .deposit [hose prot])
  (nth-protein .nth [hose idx])
  (current-protein .current [hose]))

(defn newest-index [hose] 
  (try (.newestIndex hose)
       (catch Exception e -1)))

(defn oldest-index [hose] 
  (try (.oldestIndex hose)
       (catch Exception e -1)))

(defn await-next 
  ([hose] (try (.awaitNext hose)
               (catch Exception e nil)))
  ([hose seconds]
    (try (.awaitNext hose seconds java.util.concurrent.TimeUnit/SECONDS)
         (catch Exception e nil))))

(defn make-slaw-vararg [pattern]
  (cond (string? pattern) (into-array Slaw (slaw pattern))
        (nil? pattern)    (into-array Slaw '())
        :else             (into-array Slaw (map slaw pattern))))

(defn next-matching 
  "Seeks the next protein whose descrip matches the given slaw"
  [hose descrip]
  (try (.next hose (make-slaw-vararg descrip))
       (catch Exception e nil)))

(defn previous-matching
  "Seeks the previous protein whose descrip matches the given slaw"
  [hose descrip]
  (try (.previous hose (make-slaw-vararg descrip))
       (catch Exception e nil)))

(defn protein-matches [p pattern]
  (.matches p (make-slaw-vararg pattern)))
