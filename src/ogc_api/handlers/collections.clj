(ns ogc-api.handlers.collections
  (:require
   [integrant.core :as ig]
   [ring.util.response :as rr]
   [ogc-api.responses.util :as ru]
   [ogc-api.responses.collections :as collections-resp]
   [ogc-api.util.params :as params]))

(defmethod ig/init-key :ogc-api.handlers.collections/index [_ opts]
  (fn [_]
    (rr/response (collections-resp/get-collections-metadata opts))))

(defmethod ig/init-key :ogc-api.handlers.collections/collection [_ opts]
  (fn [request]
    (let [collection-uri (params/collection-uri request)
          res (collections-resp/get-single-collection-metadata collection-uri opts)]
      (if res
        (rr/response res)
        (ru/error-response 404 "Collection not found")))))

