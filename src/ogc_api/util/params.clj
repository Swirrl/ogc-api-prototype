(ns ogc-api.util.params
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [integrant.core :as ig]
   [ogc-api.data.util.vocabs :as vocabs]))

(defn collection-id [request]
  (-> request :path-params :collection-id))

(defn feature-id [request]
  (-> request :path-params :feature-id))

(defn collection-uri [request]
  (vocabs/wn (collection-id request)))

(defn feature-uri [request]
  (vocabs/wnid (str (str/lower-case (collection-id request))
                    "/"
                    (feature-id request))))

(defn point [request]
  (-> request :parameters :query :point))

(defn bbox [request]
  (-> request :parameters :query :bbox))

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
  pos-int?)
