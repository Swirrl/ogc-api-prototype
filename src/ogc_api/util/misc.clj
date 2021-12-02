(ns ogc-api.util.misc
  (:require
    [clojure.math.numeric-tower :as math]))

(defn box [x]
  (if (coll? x) x [x]))

(defn round [decimal-places val]
  (let [decimal-multiplier (double (math/expt 10 decimal-places))]
    (-> val (* decimal-multiplier) math/round (/ decimal-multiplier))))
