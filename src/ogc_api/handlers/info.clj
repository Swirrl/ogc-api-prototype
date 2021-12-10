
(ns ogc-api.handlers.info
  (:require [integrant.core :as ig]
            [ring.util.response :as rr]))

(defmethod ig/init-key :ogc-api.handlers.info/index [_ _]
  (fn [request]
    (rr/response {:todo ["add API info"]})))

