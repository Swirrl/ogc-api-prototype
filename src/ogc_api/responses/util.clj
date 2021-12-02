(ns ogc-api.responses.util
  (:require
   [clojure.data.json :as json]
   [ogc-api.data.util.conversions :as conv]))

(defn error-response [status message]
  {:status status
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/write-str message)})

(defn map-ids [coll]
  (map conv/uri->id coll))

(defn add-license-link-to-response [partial-resp]
  (let [license-link {"href" "https://placeholder.license.url"
                      "rel" "license"
                      "title" "Placeholder license"
                      "type" "text/html"}]
    (update-in partial-resp [:links] conj license-link)))
