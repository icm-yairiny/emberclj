(ns emberclj.route-gen
  (:require
   [compojure.core :refer :all]
   [emberclj.utils :as u]
   [ring.util.response :as resp]
   [korma.core :as k]))

(defn- extract-ids [models result kw]
  (let [children (map #(u/pluralise models %) (get-in models [kw :children]))]
    (reduce (fn [res child-kw] (update-in res [child-kw] (partial map :id))) result children)))

(defn- gen-parts-from-name [models name]
  (let [name-plural (u/pluralise models name)
        sym-plural (symbol name-plural)]
    {:name-plural name-plural
     :sym-plural sym-plural
     :kw (keyword name)
     :kw-plural (keyword name-plural)
     :url-id (str "/" name-plural "/:id")
     :url-all (str "/" name-plural)
     :entity-def @(resolve sym-plural)}))

(defn- build-select [models entity-def kw]
  (let [children (get-in models [kw :children])
        child-syms (map (comp u/symbolise #(u/pluralise models %)) children)]
    (reduce (fn [sel child-sym]
              (-> sel (k/with @(resolve child-sym) (k/fields :id)))) (k/select* entity-def) child-syms)))

(defn- gen-get-all [models name]
  (let [{:keys [kw-plural kw entity-def url-all]} (gen-parts-from-name models name)
        select (build-select models entity-def kw)]
    (GET url-all {{:keys [ids]} :params}
         (let [where (if ids #(k/where % {:id [in (map u/s->int ids)]}) identity)]
           (resp/response
            {kw-plural
             (map #(extract-ids models % kw) (-> select where k/select))})))))

(defn- gen-get-single [models name]
  (let [{:keys [kw kw-plural entity-def url-id]} (gen-parts-from-name models name)]
    (GET url-id [id]
         (resp/response {kw (extract-ids
                             models
                             (first (-> (build-select models entity-def kw)
                                        (k/where {:id (u/s->int id)})
                                        k/select))
                             kw)}))))

(defn- gen-update [models name]
  (let [{:keys [kw entity-def url-id]} (gen-parts-from-name models name)]
    (PUT url-id {{:keys [id] :as params} :params}
         (resp/response {kw (k/update entity-def
                                      (k/where {:id (u/s->int id)})
                                      (k/set-fields (dissoc params :id)))}))))

(defn- gen-create [models name]
  (let [{:keys [kw entity-def url-all]} (gen-parts-from-name models name)]
    (POST url-all {params :params}
          (resp/response {kw (k/insert entity-def (k/values params))}))))

(defn- gen-delete [models name]
  (let [{:keys [kw entity-def url-id]} (gen-parts-from-name models name)]
    (DELETE url-id [id]
            (resp/response {kw (k/delete entity-def
                                         (k/where {:id (u/s->int id)}))}))))
(defn generate-all-routes [models name]
  (routes
   (gen-get-all models name)
   (gen-get-single models name)
   (gen-update models name)
   (gen-create models name)
   (gen-delete models name)))
