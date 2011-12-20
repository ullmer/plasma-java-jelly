(ns jelly.pool
  (:use [jelly.slaw])
  (:import (com.oblong.jelly Pool PoolAddress PoolServerAddress
                             PoolServer PoolServers PoolOptions)))

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

(defmacro with-hose [hs & body]
  `(let [~(first hs) (participate ~(second hs))]
     (try ~@body (finally (withdraw ~(first hs))))))

(defmacro def-hose-funs [& ps]
  `(do ~@(map (fn [x] `(defn ~(first x) ~(nth x 2) (~(second x) ~@(nth x 2))))
              ps)))

(def-hose-funs
  (hose-name .name [hose])
  (set-hose-name! .setName [hose new-name])
  (version .version [hose])
  (raw-info .info [hose])
  (withdraw .withdraw [hose])
  (connected? .isConnected [hose])
  (oldest-index .oldestIndex [hose])
  (newest-index .newestIndex [hose])
  (current-index .index [hose])
  (to-last .toLast [hose])
  (run-out .runOut [hose])
  (rewind .rewind [hose])
  (seek-to .seekTo [hose idx])
  (seek-by .seekBy [hose offset])
  (deposit .deposit [hose prot])
  (current-protein .current [hose])
  (nth-protein .nth [hose idx]))

(def pool-info (comp slaw-value raw-info))

(defn into-array-o-slaw [& x] (into-array (map slaw x)))

(defn next-matching [hose descrip] (.next hose (into-array-o-slaw descrip)))
(defn previous-matching
  "Seeks the previous descrip that matches the given slaw"
  [hose descrip] (.previous hose (into-array-o-slaw descrip)))
