(ns ogc-api.responses.util
  (:require
    [clojure.string :as str]
    [clojure.data.json :as json]
    [ring.util.response :as rr]
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

(def data-types
  {:json "application/json"
   :geojson "application/geo+json"})

(defn link [path {:keys [type rel query]}]
  {:href
   (let [base (str/join "/" path)]
     (if (empty? query) base
       (str base "?" (str/join "&" (map (fn [[k v]] (str k "=" v)) query)))))
   :rel rel
   :type (or (data-types (or type :json)) type)})

(defn add-license-link-to-response [partial-resp]
  (update-in partial-resp [:links] conj license-link))

(defn geojson [body]
  (rr/response body))

