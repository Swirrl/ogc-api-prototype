(ns ogc-api.data.features
  (:require
   [grafter-2.rdf4j.sparql :as sparql]
   [grafter-2.rdf4j.repository :as repo]))

(defn fetch-collection-items
  [query-path repo {:keys [bbox limit offset feature-id datetime]}]
  (with-open [conn (repo/->connection repo)]
    (into []
          (sparql/query
            query-path
            (cond-> {:datetime_from (or (:from datetime) (Boolean. false))
                     :datetime_to (or (:to datetime) (Boolean. false))
                     :filter_bbox (Boolean. (some? bbox))}
              (some? bbox) (assoc :bbox_lat1 (bbox 0) :bbox_lon1 (bbox 1)
                                  :bbox_lat2 (bbox 2) :bbox_lon2 (bbox 3))
              (some? feature-id) (assoc :id feature-id)
              (some? offset) (assoc ::sparql/offsets {0 offset})
              (some? limit) (assoc ::sparql/limits {10 limit}))
            conn))))

