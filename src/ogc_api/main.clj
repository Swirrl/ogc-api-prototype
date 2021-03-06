(ns ogc-api.main
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [ogc-api.concerns.integrant :as igc]
            [meta-merge.core :as mm]))

(defn read-config [config]
  (ig/read-string {:readers {'env igc/env 'resource io/resource}}
                  config))

(defn load-config [profile]
  (cond
    (map? profile) profile
    (nil? profile) {}
    :else (->> profile slurp read-config)))

;; Execution

(defn merge-profiles [profiles]
  (apply mm/meta-merge (map load-config profiles)))

(defn prep-config [profiles]
  (let [system (doto (merge-profiles profiles)
                 (ig/load-namespaces))]
    (log/info "Config prepped for profiles " profiles)
    system))

(def core-profiles (map io/resource ["base.edn" "app.edn"]))

(defn exec-config [profiles]
  (->> profiles
       (map load-config)
       prep-config
       ig/init))

(defn -main [& args]
  (let [profiles (if-let [supplied-profile (first args)]
                   (conj core-profiles supplied-profile)
                   core-profiles)]
    (println "Starting profiles " profiles)
    (exec-config profiles)))

