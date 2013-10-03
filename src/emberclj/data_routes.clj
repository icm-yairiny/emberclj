(ns emberclj.data-routes
  (:require [compojure.core :refer :all]
            [ring.util.response :as resp]
            [korma.core :as k]
            [emberclj.db :as d]
            [emberclj.utils :as u])
  (:use [clojure.pprint :only [pprint]]))

(defn- pluralise [name]
  (str name "s"))

(defn- gen-parts-from-name [name]
  (let [name-plural (pluralise name)]
    {:name-plural name-plural
     :sym-plural (symbol (str "d/" name-plural))
     :kw (keyword name)
     :kw-plural (keyword name-plural)
     :url-id (str "/" name-plural "/:id")
     :url-all (str "/" name-plural)}))

(defn- gen-get-all [name]
  (let [{:keys [kw-plural sym-plural url-all]} (gen-parts-from-name name)]
    (GET url-all {{:keys [ids]} :params}
         (resp/response
          {kw-plural
           (if ids
             (k/select sym-plural (k/where {:id [in (map u/s->int ids)]}))
             (k/select sym-plural))}))))

(defn- gen-get-single [name]
  (let [{:keys [kw sym-plural url-id]} (gen-parts-from-name name)]
    (GET url-id [id]
         (resp/response {kw (first (k/select sym-plural (k/where {:id (u/s->int id)})))}))))

(defn- gen-update [name]
  (let [{:keys [kw name-plural url-id]} (gen-parts-from-name name)]
    (PUT url-id {{:keys [id] :as params} :params}
         (resp/response {kw (k/update name-plural
                                      (k/where {:id (u/s->int id)})
                                      (k/set-fields (dissoc params :id)))}))))

(defn- gen-create [name]
  (let [{:keys [kw name-plural url-all]} (gen-parts-from-name name)]
    (POST url-all {params :params}
          (clojure.pprint/pprint (map type (keys params)))
          (resp/response {kw (k/insert name-plural (k/values params))}))))

(defn- gen-delete [name]
  (let [{:keys [kw name-plural url-id]} (gen-parts-from-name name)]
    (DELETE url-id [id]
            (resp/response {kw (k/delete name-plural
                                         (k/where {:id (u/s->int id)}))}))))
(defn- generate-get-all [name]
  (routes
   (gen-get-all name)
   (gen-get-single name)
   (gen-update name)
   (gen-create name)
   (gen-delete name)))

(defmacro t [na]
  (name na))

(defroutes data-routes
  (routes (generate-get-all "user")
          (generate-get-all "client")))
