(ns emberclj.modelsjs-gen
  (:require [clostache.parser :as clp]
            [camel-snake-kebab :as csk]
            [emberclj.utils :as u])
  (:use [clojure.pprint :only [pprint]]))

(def mdls
  {:user
   {:attributes [:name :password]
    :children [:account]}
   :account
   {:attributes [:name]
    :parents [:user]}})

(defn- gen-name-pair [models kw]
  {:single (name kw) :plural (name (u/pluralise models kw))})

(defn- convert-names [models model]
  (let [f (partial map (partial gen-name-pair models))]
    (-> model
        (update-in [:attributes] f)
        (update-in [:children] f)
        (update-in [:parents] f))))

(defn- convert-models-to-seq [models]
  (reduce (fn [acc [k v]]
            (conj acc (assoc
                          (convert-names models v)
                        :name (name (csk/->CamelCase k)))))
          [] models))

(defn generate-content [models]
  (clp/render-resource "emberclj/models-tmpl.js" {:models (convert-models-to-seq models)}))
