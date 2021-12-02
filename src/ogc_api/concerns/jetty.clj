(ns ogc-api.concerns.jetty
  (:require [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]))

(defmethod ig/init-key :ogc-api.concerns/jetty [_ {:keys [handler opts]}]
  (println "Server running on port " (:port opts))
  (jetty/run-jetty handler opts))

(defmethod ig/halt-key! :ogc-api.concerns/jetty [_ jetty]
  (.stop jetty))
