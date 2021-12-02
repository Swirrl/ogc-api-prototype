(ns ogc-api.responses.nearest-to-point
  (:require
   [ring.util.response :as rr]
   [ogc-api.responses.single-feature :as single-feature-resp]))

(defn rdf->response [{:keys [db id distance-from-point]} collection-uri point]
  (-> db
      (single-feature-resp/rdf->feature collection-uri id)
      (assoc :distanceFromSearchPoint {:value distance-from-point
                                       :unitOfMeasure "m"})
      rr/response))
