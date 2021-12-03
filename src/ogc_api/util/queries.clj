(ns ogc-api.util.queries
  (:require [selmer.parser :as selmer]
            [grafter-2.rdf4j.repository :as repo]))

(defn execute-selmer-query [repo filename selmer-context opts]
  (let [query-str (selmer/render-file filename selmer-context)]
    (with-open [conn (repo/->connection repo)]
      (into [] (apply repo/query conn query-str opts)))))
