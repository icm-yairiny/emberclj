(ns emberclj.data-routes
  (:require [compojure.core :refer :all]
            [ring.util.response :as resp]
            [korma.core :as k]
            [emberclj.db :as d]
            [emberclj.utils :as u])
  (:use [clojure.pprint :only [pprint]]))

(def relations
  {:users {:children [:accounts]}
   :accounts {:children [:transactions]}})

(defn- pluralise [name]
  (str name "s"))

(defn- extract-ids [result kw-plural]
  (let [children (get-in relations [kw-plural :children])]
    (reduce (fn [res child-kw] (update-in res [child-kw] (partial map :id))) result children)))

(defn- gen-parts-from-name [name]
  (let [name-plural (pluralise name)
        sym-plural (symbol (str "d/" name-plural))]
    {:name-plural name-plural
     :sym-plural sym-plural
     :kw (keyword name)
     :kw-plural (keyword name-plural)
     :url-id (str "/" name-plural "/:id")
     :url-all (str "/" name-plural)
     :entity-def @(resolve sym-plural)}))

(defn- build-select [entity-def kw-plural]
  (let [children (get-in relations [kw-plural :children])
        child-syms (map (comp symbol #(str "d/" %) name) children)]
    (reduce (fn [sel child-sym]
              (-> sel (k/with @(resolve child-sym) (k/fields :id)))) (k/select* entity-def) child-syms)))

(defn- gen-get-all [name]
  (let [{:keys [kw-plural entity-def url-all]} (gen-parts-from-name name)
        select (build-select entity-def kw-plural)]
    (GET url-all {{:keys [ids]} :params}
         (let [where (if ids #(k/where % {:id [in (map u/s->int ids)]}) identity)]
           (resp/response
            {kw-plural
             (map #(extract-ids % kw-plural) (-> select where k/select))})))))

(defn- gen-get-single [name]
  (let [{:keys [kw kw-plural entity-def url-id]} (gen-parts-from-name name)]
    (GET url-id [id]
         (resp/response {kw (extract-ids (first (-> (build-select entity-def kw-plural)
                                        (k/where {:id (u/s->int id)})
                                        k/select)) kw-plural)}))))

(defn- gen-update [name]
  (let [{:keys [kw entity-def url-id]} (gen-parts-from-name name)]
    (PUT url-id {{:keys [id] :as params} :params}
         (resp/response {kw (k/update entity-def
                                      (k/where {:id (u/s->int id)})
                                      (k/set-fields (dissoc params :id)))}))))

(defn- gen-create [name]
  (let [{:keys [kw entity-def url-all]} (gen-parts-from-name name)]
    (POST url-all {params :params}
          (resp/response {kw (k/insert entity-def (k/values params))}))))

(defn- gen-delete [name]
  (let [{:keys [kw entity-def url-id]} (gen-parts-from-name name)]
    (DELETE url-id [id]
            (resp/response {kw (k/delete entity-def
                                         (k/where {:id (u/s->int id)}))}))))
(defn- generate-all-routes* [name]
  (routes
   (gen-get-all name)
   (gen-get-single name)
   (gen-update name)
   (gen-create name)
   (gen-delete name)))

(defmacro generate-model [model-name]
  (generate-all-routes* (name model-name)))

(defroutes data-routes
  (routes (generate-model user)
          (generate-model account)))

(defn- test-all []
  (data-routes {:uri "/users" :request-method :get}))

(defn- test-one [id]
  (data-routes {:uri (str "/users/" id) :request-method :get}))

(defn- test-update [id vals]
  (data-routes {:uri (str "/users/" id) :request-method :put :params vals}))

(defn- test-create [vals]
  (data-routes {:uri "/users" :request-method :post :params vals}))

(defn- test-delete [id]
  (data-routes {:uri (str "/users/" id) :request-method :delete}))
