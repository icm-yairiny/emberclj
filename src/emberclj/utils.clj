(ns emberclj.utils
  (:require [camel-snake-kebab :as csk]))

(defn s->int
  "converts a given string into an integer"
  [s]
  (Integer/valueOf s))

(defn s->double
  "converts a given string into a double"
  [s]
  (Double/valueOf s))

(defn make-emberable [o]
  (if (map? o)
    (reduce (fn [new-m [kw val]] (assoc new-m (csk/->camelCase kw) (make-emberable val))) {} o)
    (if (coll? o)
      (map make-emberable o)
      o)))
