(ns ogc-api.util.validations
  (:require
   [ogc-api.util.params :as params]))

(defn error-result [message]
  {:valid? false :message message})

(defn- validate [initial-val & validations]
  (reduce (fn [{:keys [valid?] :as result} validation-fn]
            (if valid?
              (validation-fn result)
              result ;; if the result is already invalid, short circuit and skip remaining validations
              ))
          (assoc initial-val :valid? true)
          validations))

(defn- lines [{:keys [bbox] :as result}]
  (let [[min-lat min-long max-lat max-long] bbox]
    (if (or (= max-lat min-lat) (= max-long min-long))
      (error-result "Points must form a box")
      result)))

(defn- latitudes [{:keys [bbox] :as result}]
  (let [[min-lat _ max-lat _] bbox]
    (if (> max-lat min-lat)
      result
      (error-result "Max latitude must be larger than min latitude"))))

(defn- longitudes [{:keys [bbox] :as result}]
  (let [[_ min-long _ max-long] bbox]
    (if (> max-long min-long)
      result
      (error-result "Max longitude must be larger than min longitude"))))

(defn bbox [request]
  (validate {:bbox (params/bbox request)}
            lines
            latitudes
            longitudes))
