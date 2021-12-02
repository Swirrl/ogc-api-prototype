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

(defmethod ig/init-key :ogc-api.handlers.features/item [_ {:keys [repo]}]
  (fn [request]
    (let [collection-uri (params/collection-uri request)
          feature-uri (params/feature-uri request)
          item (data/fetch-item repo {:collection-uri collection-uri :feature-uri feature-uri})]
      (if item
        (single-feature-resp/rdf->response item collection-uri feature-uri)
        (ru/error-response 404 "Feature not found")))))

(defn- items-in-bbox-response [repo request]
  (let [collection-uri (params/collection-uri request)
        bbox (params/bbox request)
        db (data/fetch-items-within-bbox repo bbox)]
    (when (seq db)
      (multi-feature-resp/rdf->feature-collection db bbox collection-uri))))

(defn- handle-bbox-request [{:keys [repo]} request]
  (let [{:keys [valid?] :as result} (validate/bbox request)]
    (if valid?
      (items-in-bbox-response repo request)
      (ru/error-response 422 (:message result)))))

(defn- handle-point-request [{:keys [repo]} request]
  (let [collection-uri (params/collection-uri request)
        point (params/point request)
        result (data/fetch-nearest-item-to-point repo collection-uri point)]
    (when result
      (nearest-to-point/rdf->response result collection-uri point))))

(defn- handle-items-request [repo request]
  (let [collection-uri (params/collection-uri request)]
  (when-let [items (data/fetch-all-items repo collection-uri)]
    (-> items multi-feature-resp/rdf->feature-collection rr/response))))

(defmethod ig/init-key :ogc-api.handlers.features/index [_ opts]
  (fn [request]
    (let [response (cond
                     (params/bbox request) (handle-bbox-request opts request)
                     (params/point request) (handle-point-request opts request)
                     ; :else (handle-items-request opts request)
                     )]
      (or response
          (ru/error-response 404 "No features found near point")))))

