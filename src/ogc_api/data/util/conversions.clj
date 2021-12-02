(ns ogc-api.data.util.conversions
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [geo.crs :as crs]
   [geo.io :as geoio]
   [geo.jts :as jts]
   [grafter-2.rdf.protocols :as pr]))

(defn uri->id [item-uri]
  (-> item-uri str (str/split #"/") last edn/read-string))

(defn uri->keyword [pred-uri]
  (-> pred-uri str (str/split #"/") last keyword))

(defn lat-long->easting-northing [[lat long]]
  (let [coord (jts/point lat long)
        t (crs/create-transform 4326 27700)
        ngr-coord (jts/transform-geom coord t)
        easting (Math/round (.getX ngr-coord))
        northing (Math/round (.getY ngr-coord))]
    [easting northing]))

(defn uri-coll->template-vars [coll]
  (->>  coll
        (map #(str "<" % ">"))
        (str/join "\n")))

(defn point->wkt-literal [[lat long]]
  (str "POINT (" long " " lat ")"))

(defn wkt-literal->geo-lib-point [wkt]
  (-> wkt pr/raw-value geoio/read-wkt))
