(ns ogc-api.data.collections
  (:require
   [integrant.core :as ig]))

(def name->uri
  ; TODO remove
  {"WaterBody" "http://environment.data.gov.uk/catchment-planning/def/water-framework-directive/WaterBody"})

(defmethod ig/init-key :ogc-api.data.collections/collections [_ collections]
  collections)

