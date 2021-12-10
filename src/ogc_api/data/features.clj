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

; (def kilometres http://www.opengis.net/def/uom/OGC/1.0/kilometre)

(defquery fetch-near-point
  "ogc_api/data/queries/jena-nearest-point.sparql"
  [:ty :lat :lon :dist :limit])

(defquery
  fetch-near-items*
  "ogc_api/data/queries/features-nearest-to-point.sparql"
  [:gridrefs])

(defn- construct-features [collection-uri db]
  (mc/construct {:id ?id :wkt ?wkt}
                [[?id rdf:a collection-uri]
                 [?id geosparql:hasGeometry ?geometry]
                 [?geometry geosparql:asWKT ?wkt]]
                db))

(defn- compute-distance [point {:keys [wkt] :as feature}]
  (prn [:compute-distance point feature])
  (let [p1 (conv/wkt-literal->geo-lib-point wkt)
        p2 (apply spatial/point point)]
    (assoc feature
           :distance-from-point
           (mu/round 1 (spatial/distance p1 p2)))))

(defn fetch-nearest-item-to-point-old [repo collection-uri point]
  (when-let [grid-refs (gr/gridref-uris point)]
    (let [db (fetch-near-items* repo {:gridrefs grid-refs})]
      (when (seq db)
        (let [nearest-item (->> db
                                (construct-features collection-uri)
                                (map (partial compute-distance point))
                                (apply min-key :distance-from-point))]
          (-> nearest-item
              (dissoc :wkt)
              (assoc :db db)))))))

(defn fetch-nearest-item-to-point [repo collection-uri point]
  (let [db (fetch-near-point repo {:ty collection-uri
                                   :lat (point 0) :lon (point 1)
                                   :dist 1000000
                                   :limit 2})]
    (prn (first db))
    (first db)))

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
  [query-path repo collection-uri {:keys [bbox limit offset]}]
  (prn [:fetch-collection-items collection-uri])
  (with-open [conn (repo/->connection repo)]
    (into []
      (sparql/query
        query-path
        (cond-> {:collection-uri (java.net.URI. collection-uri)}
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
