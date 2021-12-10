(ns ogc-api.handlers.collections
  (:require
   [integrant.core :as ig]
   [ring.util.response :as rr]
   [ogc-api.responses.util :as ru]
   [ogc-api.responses.collections :as collections-resp]
   [ogc-api.util.params :as params]))

(defn collection-data [base-uri collection]
  {:id (:id collection)
   :title (:title collection)
   :links [(ru/self-link base-uri "collections" (:id collection))
           (ru/link "items" base-uri "collections" (:id collection) "items")]})

(defn handle-collection-list-request [{:keys [base-uri repo collections]} request]
  (prn [:handle-collection-list-request collections])
  (rr/response
    {:collections (map (partial collection-data base-uri) (vals collections))
     :links [(ru/self-link base-uri "collections") ru/license-link]}))

(defn handle-collection-request [{:keys [base-uri repo collections]} request]
  (if-let [collection (collections (params/collection-id request))]
    (rr/response (collection-data base-uri collection))
    (ru/error-response 404 "Collection not found")))

(defmethod ig/init-key :ogc-api.handlers.collections/index [_ opts]
  (fn [request]
    (handle-collection-list-request opts request)))

(defmethod ig/init-key :ogc-api.handlers.collections/collection [_ opts]
  (fn [request] (handle-collection-request opts request)))

