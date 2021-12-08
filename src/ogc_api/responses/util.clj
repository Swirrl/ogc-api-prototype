(ns ogc-api.responses.util
  (:require
   [clojure.data.json :as json]
   [ogc-api.data.util.conversions :as conv]))

(defn error-response [status message]
  {:status status
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/write-str {:code status :description message})})

(defn map-ids [coll]
  (map conv/uri->id coll))

(def license-link
  {:href "https://placeholder.license.url"
   :rel "license"
   :title "Placeholder license"
   :type "text/html"})

(defn self-link [base-uri request]
  {:href (str base-uri (:uri request))
   :rel "self"
   :type "application/json"})

(defn add-license-link-to-response [partial-resp]
  (update-in partial-resp [:links] conj license-link))

