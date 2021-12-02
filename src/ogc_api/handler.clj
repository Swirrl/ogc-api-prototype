(ns ogc-api.handler
  (:require [integrant.core :as ig]
            [ring.util.response :as rr]))

(defmethod ig/init-key :ogc-api.handler/index [_ _]
  (fn [request]
    (rr/response {:todo ["add API info"]})))
