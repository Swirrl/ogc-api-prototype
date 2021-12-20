(ns ogc-api.util.params
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [integrant.core :as ig]
   [java-time :as jt]
   [ogc-api.data.collections :as collections]
   [ogc-api.data.util.vocabs :as vocabs]))

(defn collection-id [request]
  (-> request :path-params :collection-id))

(defn feature-id [request]
  (-> request :path-params :feature-id))

(defn collection-uri [request] (collections/name->uri (collection-id request)))

(defn feature-uri [request]
  (vocabs/wnid (str (str/lower-case (collection-id request))
                    "/"
                    (feature-id request))))

(defn point [request]
  (-> request :parameters :query :point))

(defn bbox [request]
  (-> request :parameters :query :bbox))

(defn datetime [request]
  (-> request :parameters :query :datetime))

(defn limit [request]
  (-> request :parameters :query :limit))

(defn offset [request]
  (-> request :parameters :query :offset))

(defn split-string-of-numbers-param [param]
  (->> (str/split param #",")
       (map edn/read-string)
       (map double)
       (into [])))

(defn- parse-date [s]
  (try
    (.atStartOfDay (java.time.LocalDate/parse s))
    (catch Exception e nil)))

(defn- parse-datetime [s]
  (try
    (java.time.LocalDateTime/parse s)
    (catch Exception e
      (parse-date s))))

(defn- end-of-day [t]
  (-> t
      (jt/plus (jt/days 1))
      (jt/minus (jt/seconds 1))))

(defn datetime-range-param [param]
  (let [[fr to] (str/split param #"/" -1)]
    (if (nil? to)
      ; no range specified
      (if-let [t (parse-date fr)]
        ; date -> match entire day
        {:from t :to (end-of-day t)}
        (if-let [t (parse-datetime fr)]
          ; datetime -> match the exact instant
          {:from t :to t}))
      ; datetime range
      (let [dfr (or (some? (#{"" ".."} fr)) (parse-datetime fr))
            dto (or (some? (#{"" ".."} to)) (parse-datetime to))]
        (cond
          (or (nil? dfr) (nil? dto)) nil
          (true? dfr) {:to dto}
          (true? dto) {:from dfr}
          :else {:from dfr :to dto})))))

(defmethod ig/init-key :ogc-api.util.params/point [_ _]
  split-string-of-numbers-param)

(defmethod ig/init-key :ogc-api.util.params/bbox [_ _]
  split-string-of-numbers-param)

(defmethod ig/init-key :ogc-api.util.params/datetime [_ _]
  datetime-range-param)

(defmethod ig/init-key :ogc-api.util.params/datetime? [_ _]
  #(some? (datetime-range-param %)))

(defmethod ig/init-key :ogc-api.util.params/pos-int? [_ _]
  nat-int?)

(defmethod ig/init-key :ogc-api.util.params/feature-id? [_ _]
  string?)

