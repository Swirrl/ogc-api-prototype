(ns ogc-api.util.params
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [integrant.core :as ig]
   [ogc-api.data.collections :as collections]
   [ogc-api.data.util.vocabs :as vocabs]))

(defn collection-id [request]
  (-> request :path-params :collection-id))

(defn feature-id [request]
  (-> request :path-params :feature-id))

(defn collection-uri [request] (collections/name->uri (collection-id request)))

(defn feature-uri [request]
  (vocabs/wnid (str (str/lower-case (collection-id request))
                    "/"
                    (feature-id request))))

(defn point [request]
  (-> request :parameters :query :point))

(defn bbox [request]
  (-> request :parameters :query :bbox))

(defn limit [request]
  (-> request :parameters :query :limit))

(defn offset [request]
  (-> request :parameters :query :offset))

(defn split-string-of-numbers-param [param]
  (->> (str/split param #",")
       (map edn/read-string)
       (map double)
       (into [])))

(defmethod ig/init-key :ogc-api.util.params/point [_ _]
  split-string-of-numbers-param)

(defmethod ig/init-key :ogc-api.util.params/bbox [_ _]
  split-string-of-numbers-param)

(defmethod ig/init-key :ogc-api.util.params/pos-int? [_ _]
  nat-int?)

(defmethod ig/init-key :ogc-api.util.params/feature-id? [_ _]
  string?)

