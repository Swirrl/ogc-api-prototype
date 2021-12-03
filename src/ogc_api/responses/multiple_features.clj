(ns ogc-api.responses.multiple-features
  (:require
   [grafter.matcha.alpha :as mc]
   [grafter.vocabularies.geosparql :refer [geosparql:asWKT
                                           geosparql:hasGeometry]]
   [grafter.vocabularies.rdf :refer [rdf:a]]
   [ring.util.response :as rr]
   [ogc-api.data.util.geojson :as geojson]
   [ogc-api.data.util.vocabs :refer :all]))

(defn- build-properties [collection-uri db]
  (mc/construct
   {:id ?id
    :gmlId ?gmlId
    :beginLifespanVersion ?begin
    :wkt ?wkt
    :hydroNodeCategory ?cat
    :easting ?easting
    :northing ?northing}
   [[?id rdf:a collection-uri]
    [?id wn:gmlId ?gmlId]
    [?id wn:beginLifespanVersion ?begin]
    [?id geosparql:hasGeometry ?geometry]
    [?geometry geosparql:asWKT ?wkt]
    [?id wn:hydroNodeCategory ?cat]
    [?id sr:easting ?easting]
    [?id sr:northing ?northing]]
   db))

(defn- build-features [db collection-uri]
  (->> db
       (build-properties collection-uri)
       (map geojson/feature)))

(defn rdf->feature-collection [db collection-uri]
  (rr/response
   (geojson/feature-collection
    {:features (build-features db collection-uri)})))
