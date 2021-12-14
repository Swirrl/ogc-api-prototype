(ns ogc-api.data.features
  (:require
   [geo.spatial :as spatial]
   [grafter-2.rdf4j.sparql :as sparql]
   [grafter-2.rdf4j.repository :as repo]
   [grafter.db.triplestore.query :refer [defquery]]
   [grafter.matcha.alpha :as mc]
   [grafter.vocabularies.geosparql :refer [geosparql:asWKT
                                           geosparql:hasGeometry]]
   [grafter.vocabularies.rdf :refer [rdf:a]]
   [ogc-api.data.util.conversions :as conv]
   [ogc-api.data.util.gridref :as gr]
   [ogc-api.data.util.vocabs :refer :all]
   [ogc-api.util.misc :as mu]
   [ogc-api.util.queries :as qu]))

(defquery
  fetch-features-by-id*
  "ogc_api/data/queries/features-by-id.sparql"
  [:feature-uris])

(defn- get-ids-in-bbox [repo [min-lat min-long max-lat max-long]]
  (let [[min-x min-y] (conv/lat-long->easting-northing [min-lat min-long])
        [max-x max-y] (conv/lat-long->easting-northing [max-lat max-long])]
    (->> {:min_x min-x :min_y min-y :max_x max-x :max_y max-y}
         (qu/execute-selmer-query
           repo
           "ogc_api/data/queries/features-in-bbox.selmer.sparql")
         (mc/build ?s {?p ?o} [[?s ?p ?o]])
         (map :grafter.rdf/uri))))

(defn fetch-items-within-bbox
  [repo bbox]
  (let [ids (get-ids-in-bbox repo bbox)]
    (fetch-features-by-id* repo {:feature-uris ids})))

(defquery
  fetch-all-items*
  "ogc_api/data/queries/collection-items.sparql"
  [:collection-uri])

(defn fetch-collection-items-selmer
  [repo collection-uri {:keys [bbox limit offset]}]
  (qu/execute-selmer-query
    repo
    "ogc_api/data/queries/collection-items.selmer.sparql"
    ; {:filter_bbox (some? bbox)}
    (cond->
      {:collection_uri collection-uri}
      (some? bbox) (assoc :bbox_lat1 (bbox 0) :bbox_lon1 (bbox 1)
                          :bbox_lat2 (bbox 2) :bbox_lon2 (bbox 3)
                          :filter_bbox true)
      (some? offset) (assoc :offset offset)
      (some? limit) (assoc :limit limit)
      (and (some? bbox) (some? limit)) (assoc :bbox_limit limit))))

(defn fetch-collection-items
  [query-path repo collection-uri {:keys [bbox limit offset feature-id]}]
  (with-open [conn (repo/->connection repo)]
    (into []
          (sparql/query
            query-path
            (cond-> {:collection (java.net.URI. collection-uri)}
              (some? feature-id) (assoc :id feature-id)
              (some? offset) (assoc ::sparql/offsets {0 offset})
              (some? limit) (assoc ::sparql/limits {10 limit}))
            conn))))

(defn fetch-all-items [repo collection-uri]
  (prn [:fetch-all-items repo collection-uri])
  (->> (fetch-all-items* repo {:ty collection-uri})
       (mc/build ?s {?p ?o} [[?s ?p ?o]])))

(defquery fetch-watercourse-link*
  "ogc_api/data/queries/watercourse-link.sparql"
  [:collection-uri :feature-uri])

(defquery fetch-hydronode*
  "ogc_api/data/queries/hydronode.sparql"
  [:collection-uri :feature-uri])

(defmulti fetch-item
  (fn [_repo {:keys [collection-uri] :as _opts}]
    collection-uri))

(defmethod fetch-item wn:WatercourseLink [repo opts]
  (fetch-watercourse-link* repo opts))

(defmethod fetch-item wn:HydroNode [repo opts]
  (fetch-hydronode* repo opts))

(defmethod fetch-item :default [_ _] nil)

