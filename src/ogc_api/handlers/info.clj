
(ns ogc-api.handlers.info
  (:require [integrant.core :as ig]
            [ogc-api.responses.util :as ru]
            [ring.util.response :as rr]))

(defmethod ig/init-key :ogc-api.handlers.info/index [_ {:keys [base-uri]}]
  (fn [request]
    (rr/response
      {:links
       [(ru/link [base-uri] {:rel "self"})
        (ru/link [base-uri "schema"]
                 {:rel "service-desc"
                  :title "The OpenAPI schema"
                  :type "application/vnd.oai.openapi+json;version=3.0"})

              ; - href: 'http://data.example.org/api.html'
              ;   rel: service-doc
              ;   type: text/html
              ;   title: the API documentation
        (ru/link [base-uri "conformance"]
                 {:rel "conformance"
                  :title "OGC API conformance classes implemented by this server"})
        (ru/link [base-uri "collections"]
                 {:rel "data"
                  :title "Information about the feature collections"})]})))

(defmethod ig/init-key :ogc-api.handlers.info/schema [_ _]
  (fn [request]
    {:status 200
     :headers {"Content-Type" "application/vnd.oai.openapi+json;version=3.0"}
     :body (slurp (clojure.java.io/resource "ogc_api/data/schema.json"))}))

(defmethod ig/init-key :ogc-api.handlers.info/conformance [_ _]
  (fn [request]
    (rr/response
      {:conformsTo
       [
        "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core"
        ; "http://www.opengis.net/spec/ogcapi-features-2/1.0/conf/crs"
        ; "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/oas30"
        ; "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/html"
        "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson"
        ]})))


