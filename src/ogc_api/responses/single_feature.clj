(ns ogc-api.responses.single-feature
  (:require
   [grafter.matcha.alpha :as mc]
   [ring.util.response :as rr]
   [ogc-api.data.util.conversions :as conv]
   [ogc-api.data.util.geojson :as geojson]
   [ogc-api.data.util.vocabs :refer :all]
   [ogc-api.responses.util :as ru]))

;; TODO: concept uris to labels. No concept scheme data in DB yet.
;; https://github.com/Swirrl/defra-ogc-api/issues/46

;; TODO: data is missing river name, stream order
;; https://github.com/Swirrl/defra-ogc-api/issues/26

(defn- get-extra-watercourse-link-properties [db id]
  (-> (mc/build-1 id
                  {:startNode ?startNode
                   :endNode ?endNode
                   :catchmentName ?catchmentName
                   :catchmentId ?catchmentId
                   :length ?length
                   :width ?width
                   :gradient ?gradient
                   :level ?level
                   :fictitious ?fictitious
                   :flowDirection ?direction
                   :managedNavigation ?managedNav
                   :permanence ?perm
                   :primacy ?primacy
                   :provenance ?prov
                   :form ?form
                   :levelOfDetail ?detail}
                  [[id wn:startNode ?startNode]
                   [id wn:endNode ?endNode]
                   [id wn:catchmentName ?catchmentName]
                   [id wn:catchmentId ?catchmentId]
                   [id wn:fictitious ?fictitious]
                   [id wn:flowDirection ?direction]
                   [id wn:level ?level]
                   [id wn:managedNavigation ?managedNav]
                   [id wn:permanence ?perm]
                   [id wn:primacy ?primacy]
                   [id wn:provenance ?prov]
                   [id wn:form ?form]
                   [id wn:levelOfDetail ?detail]]
                  db)
      (dissoc :grafter.rdf/uri)
      (update :startNode conv/uri->id)
      (update :endNode conv/uri->id)))

;; See matcha known issue re: multiple optionals https://github.com/Swirrl/matcha/issues/21
(defn- upstream-from-watercourse [db id]
  (->> db
       (mc/select [?upstream]
                  [[id wn:startNode ?startNode]
                   [?startNode wn:upstreamLink ?upstream]])
       ru/map-ids))

(defn- downstream-from-watercourse [db id]
  (->> db
       (mc/select [?downstream]
                  [[id wn:endNode ?endNode]
                   [?endNode wn:downstreamLink ?downstream]])
       ru/map-ids))

(defn- get-extra-hydronode-properties [db id]
  (mc/construct-1 {:hydroNodeCategory ?cat
                   :easting ?easting
                   :northing ?northing}
                  [[id wn:hydroNodeCategory ?cat]
                   [id sr:easting ?easting]
                   [id sr:northing ?northing]]
                  db))

(defn- upstream-from-hydronode [db id]
  (->> db
       (mc/select [?upstream]
                  [[id wn:upstreamLink ?upstream]])
       ru/map-ids))

(defn- downstream-from-hydronode [db id]
  (->> db
       (mc/select [?downstream]
                  [[id wn:downstreamLink ?downstream]])
       ru/map-ids))

(defn- add-optional-property [item db id predicate]
  (if-let [v (->> db
                  (mc/select-1 [?o]
                               [[id predicate ?o]]))]
    (assoc-in item [:properties (conv/uri->keyword predicate)] v)
    item))

(defmulti rdf->feature
  (fn [db collection-uri feature-uri]
    collection-uri))

(defmethod rdf->feature wn:WatercourseLink [db _ id]
  (-> db
      (geojson/construct-geojson-base id)
      (update :properties merge (get-extra-watercourse-link-properties db id))
      (assoc-in [:properties :upstreamWatercourseLinkIds] (upstream-from-watercourse db id))
      (assoc-in [:properties :downstreamWatercourseLinkIds] (downstream-from-watercourse db id))
      (add-optional-property db id wn:gradient)
      (add-optional-property db id wn:length)
      (add-optional-property db id wn:width)))

(defmethod rdf->feature wn:HydroNode [db _ id]
  (-> db
      (geojson/construct-geojson-base id)
      (update :properties merge (get-extra-hydronode-properties db id))
      (assoc-in [:properties :upstreamWatercourseLinkIds] (upstream-from-hydronode db id))
      (assoc-in [:properties :downstreamWatercourseLinkIds] (downstream-from-hydronode db id))))

(defn rdf->response [db collection-uri id]
  (-> db (rdf->feature collection-uri id) rr/response))
