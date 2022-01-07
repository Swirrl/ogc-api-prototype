(ns build
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]))

(def lib 'ogc-api/ogc-api)
(def main 'ogc-api.main)

(defn uber [opts]
  (-> opts
      (assoc
        :main main
        :lib lib)
      bb/uber))

