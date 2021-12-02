(ns ogc-api.data.util.vocabs
  (:require
   [grafter.vocabularies.core :refer [prefixer]]))

(def ogc-def "http://environment.data.gov.uk/ogc/def/")
(def ogc-id "http://environment.data.gov.uk/ogc/id/")

(def wn (prefixer ogc-def))
(def wnid (prefixer ogc-id))

(def wn:WatercourseLink (wn "WatercourseLink"))
(def wn:HydroNode (wn "HydroNode"))

;common to hydro nodes and watercourse links
(def wn:gmlId (wn "gmlId"))
(def wn:beginLifespanVersion (wn "beginLifespanVersion"))
(def wn:inNetwork (wn "inNetwork"))
(def wn:reasonForChange (wn "reasonForChange"))

; watercourse links
(def wn:endNode (wn "endNode"))
(def wn:startNode (wn "startNode"))
(def wn:length (wn "length"))
(def wn:width (wn "width"))
(def wn:gradient (wn "gradient"))
(def wn:flowDirection (wn "flowDirection"))
(def wn:fictitious (wn "fictitious"))
(def wn:form (wn "form"))
(def wn:level (wn "level"))
(def wn:provenance (wn "provenance"))
(def wn:primacy (wn "primacy"))
(def wn:catchmentName (wn "catchmentName"))
(def wn:catchmentId (wn "catchmentId"))
(def wn:permanence (wn "permanence"))
(def wn:managedNavigation (wn "managedNavigation"))
(def wn:levelOfDetail (wn "levelOfDetail"))

; hydro nodes
(def wn:hydroNodeCategory (wn "hydroNodeCategory"))
(def wn:upstreamLink (wn "upstreamLink"))
(def wn:downstreamLink (wn "downstreamLink"))

; location helpers
(def wn:inOsGridRef (wn "inOsGridRef"))
(def wn:osGridRefCode (wn "osGridRefCode"))

(def ogc-app "http://swirrl.com/apps/ogc/def/")

(def os-gridref-id (prefixer "http://environment.data.gov.uk/ogc/id/os-grid-ref/"))

(def sr (prefixer "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/"))
(def sr:easting (sr "easting"))
(def sr:northing (sr "northing"))
