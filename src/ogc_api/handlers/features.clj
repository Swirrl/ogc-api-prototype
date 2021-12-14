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

(defn collection-item-data [{:keys [id geometry] :as item}]
  {:id id
   :type "Feature"
   :geometry (-> geometry gio/read-wkt gio/to-geojson json/read-str)
   :properties (dissoc item :id :geometry)})

(defn collection-items [query-path repo collection-uri params]
  (map collection-item-data
    (data/fetch-collection-items query-path repo collection-uri params)))

(defn collection-item [query-path repo collection-uri feature-id]
  (some->
    (data/fetch-collection-items query-path repo collection-uri
                                 {:feature-id feature-id})
    first
    collection-item-data))

(defn collection-item-links
  [base-uri collection-id feature-id]
  [(ru/self-link base-uri "collections" collection-id "items" feature-id)])

(defn collection-links [base-uri {:keys [offset limit]} collection items]
  (keep identity
    [(ru/self-link base-uri "collections" (:id collection) "items")
     (when (>= (count items) limit)
       (ru/link "next" base-uri "collections" (:id collection)
                (str "items?offset=" (+ (or offset 0) limit) "&limit=" limit)))]))

(defn- handle-items-request [{:keys [base-uri repo collections]} request]
  (if-let [collection (collections (params/collection-id request))]
    (let
      [collection-uri (:uri collection)
       query-path (:query collection)
       [valid params] (validate-params request)]
      (if valid
        (let [features (collection-items query-path repo collection-uri params)]
          (rr/response
            {:type "FeatureCollection"
             :features
             (map #(assoc % :links (collection-item-links base-uri (:id collection) (:id %))) features)
             :numberReturned (count features)
             :links (collection-links base-uri params collection features)}))
        (ru/error-response 400 (:message params))))
    (ru/error-response 404 "Collection not found")))

(defmethod ig/init-key :ogc-api.handlers.features/index [_ opts]
  (fn [request] (handle-items-request opts request)))

(defmethod ig/init-key :ogc-api.handlers.features/item [_ {:keys [base-uri repo collections]}]
  (fn [request]
  (if-let [collection (collections (params/collection-id request))]
    (let
      [collection-uri (:uri collection)
       feature-id (params/feature-id request)
       query-path (:query collection)]
      (if-let [feature (collection-item query-path repo collection-uri feature-id)]
        (rr/response
             (assoc feature :links (collection-item-links base-uri (:id collection) (:id feature))))
        (ru/error-response 404 "Feature not found"))))))

