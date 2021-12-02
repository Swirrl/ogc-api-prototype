(ns ogc-api.responses.collections
  (:require [ogc-api.data.util.vocabs :refer :all]
            [ogc-api.responses.util :as ru]))

(defn- get-hydronode-metadata [base-uri]
  {:id "HydroNode"
   :title "Hydro Node"
   :description "Placeholder HydroNode description"
   :extent {:spatial {:bbox [["to be determined"]]}
            :temporal {:interval [["to be determined"]]}}
   :links [{:href (str base-uri "/collections/HydroNode/items")
            :rel "items"
            :type "application/geo+json"
            :title "HydroNode"}]})

(defn- get-watercourselink-metadata [base-uri]
  {:id "WatercourseLink"
   :title "Watercourse Link"
   :description "Placeholder WatercourseLink description"
   :extent {:spatial {:bbox [["to be determined"]]}
            :temporal {:interval [["to be determined"]]}}
   :links [{:href (str base-uri "/collections/WatercourseLink/items")
            :rel "items"
            :type "application/geo+json"
            :title "WatercourseLink"}]})

(defn get-collections-metadata [{:keys [_ base-uri]}]
  (let [partial-resp {:links [{:href (str base-uri "/collections")
                               :rel "self"
                               :type "application/json"
                               :title "Collections"}]
                      :collections [(get-hydronode-metadata base-uri) (get-watercourselink-metadata base-uri)]}]
    (ru/add-license-link-to-response partial-resp)))

(defmulti get-single-collection-metadata
          (fn [collection-uri _opts]
            collection-uri))

(defmethod get-single-collection-metadata wn:HydroNode [_ {:keys [_ base-uri]}]
  (ru/add-license-link-to-response (get-hydronode-metadata base-uri)))

(defmethod get-single-collection-metadata wn:WatercourseLink [_ {:keys [_ base-uri]}]
  (ru/add-license-link-to-response (get-watercourselink-metadata base-uri)))

(defmethod get-single-collection-metadata :default [_ {:keys [_ base-uri]}]
  nil)

