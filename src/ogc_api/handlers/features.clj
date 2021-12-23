(ns ogc-api.handlers.features
  (:require
    [clojure.data.json :as json]
    [integrant.core :as ig]
    [ogc-api.data.features :as data]
    [ogc-api.util.validations :as validate]
    [ogc-api.responses.util :as ru]
    [geo.io :as gio]
    [ring.util.response :as rr]
    [ogc-api.util.params :as params]))

(defn- validate-params [request]
  (let [bbox (if (params/bbox request)
               (validate/bbox request) {:valid? true})
        datetime (params/datetime request)
        offset (params/offset request)
        limit (or (params/limit request) 10)]
    [(:valid? bbox)
     (merge bbox {:datetime datetime
                  :limit limit :offset offset})]))

(defn collection-item-data [{:keys [id geometry] :as item}]
  {:id id
   :type "Feature"
   :geometry (-> geometry gio/read-wkt gio/to-geojson json/read-str)
   :properties (dissoc item :id :geometry)})

(defn collection-item-links
  [base-uri property-links collection-id item]
  (concat
    (keep identity
          (map
            (fn [[field {:keys [collection]}]]
              (if-let [ref-id (-> item :properties field)]
                (ru/link [base-uri "collections" collection "items" ref-id]
                         {:type :geojson :rel (name field)})))
            property-links))
    [(ru/link [base-uri "collections" collection-id "items" (:id item)]
              {:type :geojson :rel "self"})]))

(defn collection-item
  [base-uri collection-id property-links exclude-properties item]
  (let [item (collection-item-data item)]
    (assoc item
           :properties (apply dissoc (:properties item) exclude-properties)
           :links
           (collection-item-links base-uri property-links collection-id item))))

(defn fetch-collection-items [query-path repo params]
  (data/fetch-collection-items query-path repo params))

(defn fetch-collection-item [query-path repo feature-id]
  (first
    (data/fetch-collection-items query-path repo {:feature-id feature-id})))

(defn collection-links [base-uri {:keys [offset limit]} collection item-count]
  (let [limit (or limit 10)]
    (keep identity
          [(ru/link [base-uri "collections" (:id collection) "items"]
                    {:type :geojson :rel "self"})
           (when (>= item-count limit)
             (ru/link [base-uri "collections" (:id collection) "items"]
                      {:type :geojson :rel "next"
                       :query {"offset" (+ (or offset 0) limit)
                               "limit" limit}}))])))

(defn- handle-items-request [{:keys [base-uri repo collections]} request]
  (if-let [collection (collections (params/collection-id request))]
    (let
      [{:keys [query property-links exclude-properties]} collection
       [valid params] (validate-params request)]
      (if valid
        (let [features (fetch-collection-items query repo params)]
          (ru/geojson
            {:type "FeatureCollection"
             :features (map #(collection-item base-uri (:id collection) property-links exclude-properties %) features)
             :numberReturned (count features)
             :links (collection-links base-uri params collection (count features))}))
        (ru/error-response 400 (:message params))))
    (ru/error-response 404 "Collection not found")))

(defmethod ig/init-key :ogc-api.handlers.features/index [_ opts]
  (fn [request] (handle-items-request opts request)))

(defmethod ig/init-key :ogc-api.handlers.features/item [_ {:keys [base-uri repo collections]}]
  (fn [request]
  (if-let [collection (collections (params/collection-id request))]
    (let
      [{:keys [query property-links exclude-properties]} collection
       feature-id (params/feature-id request)]
      (if-let [feature (fetch-collection-item query repo feature-id)]
        (ru/geojson
          (collection-item base-uri (:id collection) property-links exclude-properties feature))
        (ru/error-response 404 "Feature not found"))))))

