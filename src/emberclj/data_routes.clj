(ns emberclj.data-routes
  (:require [compojure.core :refer [defroutes]]
            [emberclj.db :as d]
            [emberclj.core :as em]))

(def models-definition
   {:user
    {:attributes [:name :password]
     :children [:account]}
    :account
    {:attributes [:name]
     :parents [:user]
     :children [:transaction]}
    :transaction
    {:attributes [:description :amount]
     :parents [:account]}})

(em/define-entities models-definition)

(defroutes data-routes
  (em/generate-routes models-definition))

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
