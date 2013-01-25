(ns jelly.delete_dossiers
  (:require clojure.main)
  (:use clojure.java.io [jelly slaw pool])
  (import [com.oblong.jelly Slaw]))

;;
;; USAGE:
;; lein repl
;; (load-file "src/jelly/delete_dossiers.clj")
;;
;; CAVEAT:
;; Works only if the Mezz session is NOT locked.
;;
;; SIDE-EFFECT:
;; If a dossier is open, it will close (& not re-open) it.
;;

(def trans-num (atom 100))
(def fake-provenance "android-87ac0542-abbd-4385-911b-xxxxxxxxxxxx")
(defn make-req-descrips
  "Given a req-type (e.g. 'mez-state', 'close-dossier'),
   return a Slaw for the proper descrips."
  [req-type]
  (slaw ["mezzanine" "prot-spec v2.0" "request" req-type
         "from:" [fake-provenance (Slaw/int32 (swap! trans-num inc))]]))

(defn make-request
  "Create a request protein.
   @req-type: the name of the request (e.g. 'mez-state').
   @ingests: optional hash (e.g. 'uid':'flurbl')."
  ([req-type]
     (make-request req-type {}))
  ([req-type ingests]
     (protein (make-req-descrips req-type) (slaw ingests))))

(defn await-next-matching
  "Receive proteins as they arrive.
   Ignore those which don't match @pattern.
   Return the first protein which does match."
  [hose pattern]
  (println (str "-- awaiting next '" pattern "' from rec-hose --"))
  (first (filter #(protein-matches? % pattern)
                 (repeatedly #(await-next hose)))))

(defn start-protein-agent
  "Create agent (thread).
   Start it looking for protein to match @pattern.
   When it finishes, it holds a protein."
  [hose pattern]
  (let [a (agent hose)]
    (send a await-next-matching pattern)
    a))

(defn write-protein-to-file
  "For debugging.
   Write protein @p to file with name @f-name.  Return unit."
  [p f-name]
  (with-open [wrtr (writer f-name)]
    (.write wrtr (.toString p))))

(defn get-response
  "Send a request protein of @request-name.
   Await its response protein (with @response-name) and return it.
   Ignore all intervening incoming proteins."
  ([hoses request-name]
     (get-response hoses request-name request-name))
  ([hoses request-name response-name]
     (let [response-agent (start-protein-agent (:recv hoses) response-name)]
       (. Thread (sleep 100))  ; necessary?
       (deposit (:send hoses) (make-request request-name))
       (await response-agent)
       (write-protein-to-file @response-agent (str "./" response-name ".txt"))
       @response-agent)))

(defn get-ingests [p]
  ((slaw-value p) "ingests"))

(defn get-state [p]
  ((get-ingests p) "state"))

(defn get-dossiers-protein
  "Get a response protein with the list of dossiers.
   First, request mez-state info.
   If in portal mode, return that protein.
   If in dossier mode, then request close-dossier, and return its response."
  [hoses]
  (let [state-response (get-response hoses "mez-state")]
    (if (= "portal" (get-state state-response))
      state-response
      (get-response hoses "close-dossier"))))

(defn get-dossier-names-and-uids
  "From a protein @p with a list of dossiers,
   for each dossier, extract the 'name' and 'uid'.
   Return seq of hashes."
  [p]
  (map (fn [h] {:name (h "name") :uid (h "uid")})
       ((get-ingests p) "dossiers")))

(def send-hose-name "mz-into-native")
(def recv-hose-name "mz-from-native")
(defn get-hoses
  "Given the name of a @server-address,
   return a hash w/ hoses: :send and :recv."
  [server-address]
  (let [serv (get-server server-address)
        attach #(with-server serv (participate %))]
    {:send (attach send-hose-name)
     :recv (attach recv-hose-name)}))

(defn detach [hoses]
  (doseq [h (vals hoses)]
    (withdraw h)))

(defn get-uids-to-delete
  "Given a @dossiers-protein (w/ a list of dossiers),
   return a seq of uids of dossiers w/ the right @name-to-delete."
  [dossiers-protein name-to-delete]
  (let [hs (get-dossier-names-and-uids dossiers-protein)]
    (map :uid (filter #(= (:name %) name-to-delete) hs))))

(defn delete-dossier-with-uid
  "Request deletion of a single dossier with @uid.
   (Return request protein -- ignored.)"
  [hoses uid]
  (let [delete-protein (make-request "delete-dossier" {"uid" uid})]
    (deposit (:send hoses) delete-protein)))

(defn delete-dossiers-with-name
  "Attach to Mezz server @server-address, w/ hoses for sending and receiving.
   Request to join the Mezz session.
   Request a list of dossiers.
   Request deletion of each dossier with @name-to-delete.
   Detach."
  [server-address name-to-delete]
  (let [hoses (get-hoses server-address)
        _ (get-response hoses "join")
        ;; N.B. We're just assuming we get a "permission":"true" response.
        dossiers-protein (get-dossiers-protein hoses)]
    (with-open [wrtr (writer "./uids.txt")]
      (doseq [uid (get-uids-to-delete dossiers-protein name-to-delete)]
        (.write wrtr (str uid "\n"))
        (delete-dossier-with-uid hoses uid)))
    (detach hoses)))

;; -- MAIN --

(def server-address "tcp://mezzdelpi.local")
(def name-of-dossiers-to-delete "windshield add delete test")
(delete-dossiers-with-name
  server-address
  name-of-dossiers-to-delete)
