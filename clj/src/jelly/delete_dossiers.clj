(ns jelly.delete_dossiers
  (:require clojure.main)
  (:use clojure.java.io [jelly slaw pool])
  (import [com.oblong.jelly Slaw]))

(def trans-num (atom 100))
(def fake-provenance "android-87ac0542-abbd-4385-911b-xxxxxxxxxxxx")
(defn mk-req-descrips [req-type]
  (slaw ["mezzanine" "prot-spec v2.0" "request" req-type
         "from:" [fake-provenance (Slaw/int32 (swap! trans-num inc))]]))

(defn mk-request
  ([req-type]
     (mk-request req-type {}))
  ([req-type ingests]
     (protein (mk-req-descrips req-type) (slaw ingests))))

(defn await-next-matching [hose pattern]
  (println (str "-- awaiting next '" pattern "' from rec-hose --"))
  (first (filter #(protein-matches? % pattern)
                 (repeatedly #(await-next hose)))))

(defn start-agent [hose pattern]
  (let [a (agent hose)]
    (send a await-next-matching pattern)
    a))

(defn write-protein-to-file [p f-name]
  (with-open [wrtr (writer f-name)]
    (.write wrtr (.toString p))))

(defn get-response
  "Send a request.
   Await response protein and return it.
   Ignore all intervening incoming proteins."
  ([hoses pattern]
     (get-response hoses pattern pattern))
  ([hoses request-pattern response-pattern]
     (let [response-agent (start-agent (:rec hoses) response-pattern)]
       (. Thread (sleep 100))  ; necessary?
       (deposit (:snd hoses) (mk-request request-pattern))
       (await response-agent)
       (write-protein-to-file @response-agent (str "./" response-pattern ".txt"))
       @response-agent)))

(defn get-ingests [p]
  ((slaw-value p) "ingests"))

(defn get-state [p]
  ((get-ingests p) "state"))

(defn get-dossiers-protein [hoses]
  (let [state-response (get-response hoses "mez-state")]
    (if (= "portal" (get-state state-response))
      state-response
      (get-response hoses "close-dossier"))))

(defn get-dossier-names-and-uids [p]
  (map (fn [h] {:name (h "name") :uid (h "uid")})
       ((get-ingests p) "dossiers")))

(defn get-hoses []
  (let [serv (get-server "tcp://mezzdelpi.local")
        attach #(with-server serv (participate %))]
    {:snd (attach "mz-into-native")
     :rec (attach "mz-from-native")}))

(defn detach [hoses]
  (doseq [h (vals hoses)]
    (withdraw h)))

(defn get-uids-to-delete
  [dossiers-protein name-to-delete]
  (let [hs (get-dossier-names-and-uids dossiers-protein)]
    (map :uid (filter #(= (:name %) name-to-delete) hs))))

(defn delete-dossier-with-uid
  [hoses uid]
  (let [delete-protein (mk-request "delete-dossier" {"uid" uid})]
    (deposit (:snd hoses) delete-protein)))

(defn delete-dossiers-with-name
  [name-to-delete]
  (let [hoses (get-hoses)
        _ (get-response hoses "join")
        ;; N.B. We're just assuming we get a "permission":"true" response.
        dossiers-protein (get-dossiers-protein hoses)]
    (with-open [wrtr (writer "./uids.txt")]
      (doseq [uid (get-uids-to-delete dossiers-protein name-to-delete)]
        (.write wrtr (str uid "\n"))
        (delete-dossier-with-uid hoses uid)))
    (detach hoses)))

(delete-dossiers-with-name "windshield add delete test")
