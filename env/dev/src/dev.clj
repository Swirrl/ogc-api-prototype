(ns dev
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]
   [integrant.repl :as igr :refer [go reset]]
   [integrant.repl.state :refer [system config]]
   [reitit.dev.pretty :as pretty]
   [reitit.ring.middleware.dev :as rdev]
   [ogc-api.main :as main]))

;; require scope capture as a side effect

(require 'sc.api)

(def profiles (concat main/core-profiles
                      [(io/resource "dev.edn")
                       (io/resource "local.edn")]))

(igr/set-prep!
 #(do
    (main/prep-config profiles)))

(defmethod ig/init-key :dev/http-pretty-exception [_ opts]
  pretty/exception)

(defmethod ig/init-key :dev/http-print-request-diffs [_ opts]
  rdev/print-request-diffs)
