(ns ogc-api.concerns.integrant
  "This namespace defines various integrant concerns, e.g.
    data-readers and derived constant keys etc.
  This file should be explicitly required by entry points to the
  app."
  (:require [integrant.core :as ig]))

(defmethod ig/init-key :ogc-api/const [_ v] v)

;; (derive :ogc-api/some-const :ogc-api/const) for constants

(derive :ogc-api/query-endpoint :ogc-api/const)
(derive :ogc-api/base-uri :ogc-api/const)

(defn env [[env-var default]]
  (let [e (System/getenv env-var)]
    (if e
      (if (int? default)
        (Integer/parseInt e)
        e)
      default)))

