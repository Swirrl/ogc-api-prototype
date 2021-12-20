(ns ogc-api.responses.util
  (:require
    [clojure.string :as str]
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

(def link-types
  {:json "application/json"
   :geojson "application/geo+json"})
(defn link [path {:keys [type rel query]}]
  {:href (str/join "/" path)
   :rel rel
   :type (or (link-types (or type :json)) type)})

(defn self-link [& path] (link path {:rel :self}))

(defn add-license-link-to-response [partial-resp]
  (update-in partial-resp [:links] conj license-link))

