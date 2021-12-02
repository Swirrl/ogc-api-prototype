(ns ogc-api.data.util.gridref
  (:require
   [ogc-api.data.util.conversions :as conv]
   [ogc-api.data.util.vocabs :as vocabs]))

(def os-gridref-prefixes [[ "SV" "SW" "SX" "SY" "SZ" "TV" "TW" ]
                          [ "SQ" "SR" "SS" "ST" "SU" "TQ" "TR" ]
                          [ "SL" "SM" "SN" "SO" "SP" "TL" "TM" ]
                          [ "SF" "SG" "SH" "SJ" "SK" "TF" "TG" ]
                          [ "SA" "SB" "SC" "SD" "SE" "TA" "TB" ]
                          [ "NV" "NW" "NX" "NY" "NZ" "OV" "OW" ]
                          [ "NQ" "NR" "NS" "NT" "NU" "OQ" "OR" ]
                          [ "NL" "NM" "NN" "NO" "NP" "OL" "OM" ]
                          [ "NF" "NG" "NH" "NJ" "NK" "OF" "OG" ]
                          [ "NA" "NB" "NC" "ND" "NE" "OA" "OB" ]
                          [ "HV" "HW" "HX" "HY" "HZ" "JV" "JW" ]
                          [ "HQ" "HR" "HS" "HT" "HU" "JQ" "JR" ]
                          [ "HL" "HM" "HN" "HO" "HP" "JL" "JM" ]])

(defn- grid-square-code
  "Derive the two letter OS grid reference code (100km square) from the first
  digits of the easting and northing"
  [easting northing]
  (let [x (int (Math/floor (/ easting 100000)))
        y (int (Math/floor (/ northing 100000)))]
    (when (and x y)
      (get-in os-gridref-prefixes [y x]))))

(defn- os-gridref-4
  "Return the OS 4 figure grid reference (1km square) that a point is located in.
  Returns nil for points not in the UK"
  ([easting northing]
   (let [code (grid-square-code easting northing)]
     (os-gridref-4 easting northing code)))
  ([easting northing code]
   (when code
     (str code (subs (str easting) 1 3) (subs (str northing) 1 3)))))

(defn- os-gridref-6
  "Return the OS 6 figure grid reference (100m square) that a point is located in.
  Returns nil for points not in the UK."
  ([easting northing]
   (let [code (grid-square-code easting northing)]
     (os-gridref-4 easting northing code)))
  ([easting northing code]
   (when code
     (str code (subs (str easting) 1 4) (subs (str northing) 1 4)))))

(defn- neighbouring-grid-dimension
  "Given an easting or northing, if close to the edge of its own grid, return
  the easting/northing of"
  [g]
  (let [within-grid (int (mod g 100000))]
    (cond
      (>= within-grid 99000) (+ g 1000)
      (<= within-grid 1000) (- g 1000)
      :else nil)))

(defn- grid-squares-close-to-point
  "Given a lat-long point, return the 4 figure OS grid references for that point,
  plus any neighbouring squares if the point is close to an edge"
  [point]
  (let [[easting northing] (conv/lat-long->easting-northing point)
        side-easting (neighbouring-grid-dimension easting)
        side-northing (neighbouring-grid-dimension northing)
        focus (os-gridref-4 easting northing)
        horizontal (when side-easting
                     (os-gridref-4 side-easting northing))
        vertical (when side-northing
                   (os-gridref-4 easting side-northing))
        diagonal (when (and side-easting side-northing)
                   (os-gridref-4 side-easting side-northing))]
    (cond-> nil
      focus (conj focus)
      horizontal (conj horizontal)
      vertical (conj horizontal)
      diagonal (conj diagonal))))

(defn gridref-uris
  "Return a collection of URI strings for the grid refs on/near a lat-long point"
  [point]
  (let [grid-squares (grid-squares-close-to-point point)]
    (when (seq grid-squares)
      (map vocabs/os-gridref-id grid-squares))))
