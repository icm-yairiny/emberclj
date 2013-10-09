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

(defn kw+
  "takes a base and suffix and returns a concatenated keyword.  base and suffix can be either kw or string"
  [kw suffix]
  (keyword (str (name kw) (name suffix))))

(defmulti pluralise (fn [_ o] (class o)))

(defmethod pluralise clojure.lang.Keyword [models kw] 
    (let [plural (get-in models [kw :plural])]
    (or plural (kw+ kw "s"))))

(defmethod pluralise String [models n] 
    (let [plural (get-in models [(keyword n) :plural])]
    (name (or plural (kw+ n "s")))))

(defmethod pluralise clojure.lang.Symbol [models sym] 
    (let [plural (get-in models [(keyword sym) :plural])]
    (symbol (name (or plural (kw+ (name sym) "s"))))))

(defn symbolise [n]
  "turns the given arg into a symbol"
  (if (symbol? n) n
      (symbol (if (keyword? n) (name n) n))))
