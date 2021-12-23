(ns ogc-api.concerns.reitit
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [meta-merge.core :as mm]
   [muuntaja.core :as m]
   [ring.middleware.content-type :as content-type]
   [reitit.coercion :as rc]
   [reitit.coercion.malli :as mc]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ogc-api.responses.util :as ru]))

(defmethod ig/init-key :ogc-api.concerns.reitit/ring-handler
  [_ {:keys [router default-handler opts]}]
  (ring/ring-handler router default-handler opts))

(defn- collect-humanised-error-messages [coercion-error]
  (->> coercion-error ex-data rc/encode-error :humanized
       (map (fn [[k v]]
              (str (name k) " " (str/join " and " v))))
       (str/join ", ")))

(defn- coercion-handler [status]
  (fn [e _]
    (ru/error-response status (str "Bad request, " (collect-humanised-error-messages e)))))

(def ^:private exception-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    {::rc/request-coercion (coercion-handler 400)
     ::rc/response-coercion (coercion-handler 500)
     ::exception/default (constantly
                          (ru/error-response 500 "Sorry, there was an unexpected error"))})))

(defmethod ig/init-key :ogc-api.concerns.reitit/router [_ {:keys [data opts]}]
  (ring/router data (mm/meta-merge
                     {:data {:coercion mc/coercion
                             :muuntaja m/instance
                             :middleware [parameters/parameters-middleware ;; query-params & form-params
                                          content-type/wrap-content-type
                                          muuntaja/format-negotiate-middleware ;; content-negotiation
                                          muuntaja/format-response-middleware ;; encoding response body
                                          ; exception-middleware ;; exception handling
                                          muuntaja/format-request-middleware ;; decoding request body
                                          coercion/coerce-response-middleware ;; coercing response bodies
                                          coercion/coerce-request-middleware ;; coercing request parameters
                                          multipart/multipart-middleware ;; multipart
                                          ]}}
                     opts)))

(defmethod ig/init-key :ogc-api.concerns.reitit/default-handler [_ _]
  (ring/routes
   (ring/redirect-trailing-slash-handler {:method :strip})
   (ring/create-default-handler
    {:not-found (constantly (ru/error-response 404 "Page not found"))
     :method-not-allowed (constantly (ru/error-response 405 "Method not allowed"))
     :not-acceptable (constantly (ru/error-response 406 "Request not acceptable"))})))
