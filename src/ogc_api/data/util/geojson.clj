(ns ogc-api.data.util.geojson
  (:require
   [clojure.data.json :as json]
   [geo.io :as geoio]
   [grafter-2.rdf.protocols :as pr]
   [grafter.matcha.alpha :as mc]
   [grafter.vocabularies.geosparql :refer [geosparql:asWKT
                                           geosparql:hasGeometry]]
   [grafter.vocabularies.rdf :refer [rdf:a]]
   [ogc-api.data.util.conversions :as conv]
   [ogc-api.data.util.vocabs :refer :all]
   [ogc-api.util.misc :as mu]))

(defn- point? [x]
  (and (coll? x) (every? float? x) (= 2 (count x))))

(defn- round-to-6-decimal-places [coords]
  (if (point? coords)
    (->> coords (map (partial mu/round 6)))
    (map round-to-6-decimal-places coords)))

(defn wkt->geojson [wkt-literal]
  (-> wkt-literal
      pr/raw-value
      geoio/read-wkt
      geoio/to-geojson
      json/read-str
      (update "coordinates" round-to-6-decimal-places)))

(defn construct-geojson-base [db id]
  (-> {:type "Feature"
       :geometry ?wkt
       :properties {:id (conv/uri->id id)
                    :gmlId ?gmlId
                    :beginLifespanVersion ?begin}}
      (mc/construct-1
       [[id rdf:a ?type]
        [id wn:gmlId ?gmlId]
        [id wn:beginLifespanVersion ?begin]
        [id geosparql:hasGeometry ?geometry]
        [?geometry geosparql:asWKT ?wkt]]
       db)
      (update :geometry wkt->geojson)))

(defn feature [properties]
  {:type "Feature"
   :geometry (-> properties :wkt wkt->geojson)
   :properties (-> properties
                   (dissoc :wkt)
                   (update :id conv/uri->id))})

(defn feature-collection [vals]
  (assoc vals :type "FeatureCollection"))
