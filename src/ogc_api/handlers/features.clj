(ns ogc-api.handlers.features
  (:require
    [clojure.data.json :as json]
    [integrant.core :as ig]
    [ogc-api.data.features :as data]
    [ogc-api.responses.single-feature :as single-feature-resp]
    [ogc-api.responses.multiple-features :as multi-feature-resp]
    [ogc-api.responses.nearest-to-point :as nearest-to-point]
    [ogc-api.util.validations :as validate]
    [ogc-api.responses.util :as ru]
    [geo.io :as gio]
    [ring.util.response :as rr]
    [ogc-api.util.params :as params]))

(defn- validate-params [request]
  (let [bbox (if (params/bbox request)
               (validate/bbox request) {:valid? true})
        offset (params/offset request)
        limit (or (params/limit request) 10)]
    [(:valid? bbox)
     (merge bbox {:limit limit :offset offset})]))

(defn collection-item-data [item]
  {:id (:id item)
   :type "Feature"
   :geometry (json/read-str (gio/to-geojson (gio/read-wkt (:geometry item))))
   :properties "???"
   "?raw" item})

(defn collection-items [query-path repo collection-uri params]
  (map collection-item-data
    (data/fetch-collection-items query-path repo collection-uri params)))

(defn collection-links [base-uri {:keys [offset limit]} collection items]
  (keep identity
    [(ru/self-link base-uri "collections" (:id collection) "items")
     (when (>= (count items) limit)
       (ru/link "next" base-uri "collections" (:id collection)
                (str "items?offset=" (+ (or offset 0) limit) "&limit=" limit)))]))

(defn- handle-items-request [{:keys [base-uri repo collections]} request]
  (prn base-uri)
  (if-let [collection (collections (params/collection-id request))]
    (let
      [collection-uri (:uri collection)
       query-path (:query collection)
       [valid params] (validate-params request)]
      (if valid
        (let [features (collection-items query-path repo collection-uri params)]
          (rr/response
            {:type "FeatureCollection"
             :features features
             :numberReturned (count features)
             :links (collection-links base-uri params collection features)}))
        (ru/error-response 400 (:message params))))
    (ru/error-response 404 "Collection not found")))

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

