(ns ogc-api.handlers.collections
  (:require
   [integrant.core :as ig]
   [ring.util.response :as rr]
   [ogc-api.responses.util :as ru]
   [ogc-api.responses.collections :as collections-resp]
   [ogc-api.util.params :as params]))

(defn collection-data
  [collection]
  {:id (:id collection)
   :title (:title collection)})

(defn handle-collection-list-request [{:keys [base-uri repo collections]} request]
  (prn [:handle-collection-list-request collections])
  (rr/response
    {:collections (map collection-data (vals collections))
     :links [(ru/self-link base-uri request) ru/license-link]}))

(defn handle-collection-request [{:keys [base-uri repo collections]} request]
  (let [collection-id (params/collection-id request)
        collection (collections collection-id)
        collection-uri (:uri collection)]
    (if collection-uri
        (rr/response
          (merge (collection-data collection)
            {:links [(ru/self-link base-uri request) ru/license-link]}))
        (ru/error-response 404 "Collection not found"))))

(defmethod ig/init-key :ogc-api.handlers.collections/index [_ opts]
  (fn [request]
    (handle-collection-list-request opts request)))

(defmethod ig/init-key :ogc-api.handlers.collections/collection [_ opts]
  (fn [request] (handle-collection-request opts request)))

