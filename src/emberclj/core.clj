(ns emberclj.core
  (:require [korma.core :as k]
            [compojure.core :as c]
            [emberclj.utils :as u]
            [emberclj.route-gen :as rg]
            [ring.util.response :as resp])
  (:use [clojure.pprint :only [pprint]]))

(defn clean [] (map #(ns-unmap *ns* %) (keys (ns-interns *ns*))))
(clean)

(def models-definition
  {:user
   {:attributes [:name :password]
    :children [:account]}
   :account
   {:attributes [:name]
    :parents [:user]}})

(defmacro declare-entities
  "takes a collection of symbols and generates declare clauses"
  [entity-syms]
  `(do ~@(map (fn [sym#] `(declare ~sym#)) entity-syms)))

(defn- generate-has-many
  "takes the kw names of a parent and child entity and generates a has-many clause"
  [models kw-parent kw-child]
  `(k/has-many ~(u/symbolise (u/pluralise models kw-child)) {:fk ~(u/kw+ kw-parent "-id")}))

(defn- generate-belongs-to
  "takes the kw name of a parent and generates a  belongs-to clause"
  [models kw-parent]
  `(k/belongs-to ~(u/symbolise (u/pluralise models kw-parent)) {:fk ~(u/kw+ kw-parent "-id")}))

(defmacro define-entity [sym models]
  "generates a korma entity definition"
  (let [models (if-not (symbol? models) models (deref (resolve models)))
        pluralise (fn [n] (u/pluralise models n))
        definition `[k/defentity ~(pluralise sym)]
        kw (keyword sym)
        model-def (models kw)
        children (or (:children model-def) [])
        parents (or (:parents model-def) [])
        has-manys (map #(generate-has-many models kw %) children)
        belong-tos (map #(generate-belongs-to models %) parents)]
    (apply list (reduce conj definition (concat has-manys belong-tos)))))

(defmacro define-entities [models]
  "declares and then defines the enities in thr models definition"
  (let [models (if-not (symbol? models) models (deref (resolve models)))
        keys# (keys models)
        syms# (map (comp symbol name) keys#)
        syms-plural# (map (comp symbol name #(u/pluralise models %)) keys#)]
    `(do     
       (declare-entities ~syms-plural#)
       (do ~@(map (fn [sym#] `(define-entity ~sym# ~models)) syms#)))))

(defn generate-routes [models]
  (let [nms (map name (keys models))]
    (apply c/routes (map #(rg/generate-all-routes models %) nms))))
