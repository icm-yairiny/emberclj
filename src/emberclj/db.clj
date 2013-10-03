(ns emberclj.db
  (:use [korma core db]))

(defn- underscores->dashes [n]
  "takes a name with underscores and converts to dashes"
  (-> n clojure.string/lower-case (.replaceAll "_" "-") keyword))

(defn- dashes->underscores [n]
  "takes a name with dashes and converts to underscores"
  (-> n name (.replaceAll "-" "_")))

(def ^:private pg-naming
  "the naming convention we use for the postgres database"
  {:keys underscores->dashes :fields dashes->underscores})

(defdb dev
  (postgres {:db "bpolsp"
                :user "bpolsp"
                :password "bpolsp" :naming pg-naming :delimiters ""}))

;;defentities go here
(defentity users)
