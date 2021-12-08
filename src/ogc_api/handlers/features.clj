(ns ogc-api.handlers.features
  (:require
   [integrant.core :as ig]
   [ogc-api.data.features :as data]
   [ogc-api.responses.single-feature :as single-feature-resp]
   [ogc-api.responses.multiple-features :as multi-feature-resp]
   [ogc-api.responses.nearest-to-point :as nearest-to-point]
   [ogc-api.util.validations :as validate]
   [ogc-api.responses.util :as ru]
   [ring.util.response :as rr]
   [ogc-api.util.params :as params]))

(defn- validate-params [request]
  (let [bbox (if (params/bbox request)
               (validate/bbox request) {:valid? true})
        offset (params/offset request)
        limit (params/limit request)]
    [(:valid? bbox)
     (merge bbox {:limit limit :offset offset})]))

(defn collection-item-data [item]
  {:id (:notation item)
   :type "Feature"
   :geometry "???"
   :properties "???"
   "?raw" item})

(defn collection-items [query-path repo collection-uri params]
  (map collection-item-data
    (data/fetch-collection-items query-path repo collection-uri params)))

(defn- handle-items-request [{:keys [repo collections]} request]
  (let [collection (collections (params/collection-id request))
        collection-uri (:uri collection)
        query-path (:query collection)
        [valid params] (validate-params request)]
    (if valid
      (let [features (collection-items query-path repo collection-uri params)]
        (rr/response
          {:type "FeatureCollection"
           :features features
           :numberReturned (count features)}))
      (ru/error-response 400 (:message params)))))

(defmethod ig/init-key :ogc-api.handlers.features/index [_ opts]
  (fn [request] (handle-items-request opts request)))

(defmethod ig/init-key :ogc-api.handlers.features/item [_ {:keys [repo collections]}]
  (fn [request]
    (let [collection-uri (:uri (collections (params/collection-id request)))
          feature-uri (params/feature-uri request)
          item (data/fetch-item repo {:collection-uri collection-uri :feature-uri feature-uri})]
      (if item
        (single-feature-resp/rdf->response item collection-uri feature-uri)
        (ru/error-response 404 "Feature not found")))))

