(ns ogc-api.util.queries
  (:require [selmer.parser :as selmer]
            [grafter-2.rdf4j.repository :as repo]))

(defn execute-selmer-query [repo filename selmer-context]
  (let [query-str (selmer/render-file filename selmer-context)]
    (with-open [conn (repo/->connection repo)]
      (into [] (repo/query conn query-str)))))
