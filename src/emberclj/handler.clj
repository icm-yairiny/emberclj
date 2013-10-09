(ns emberclj.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]

            [ring.util.response :as resp]
            [ring.middleware.json :as json]
            ring.middleware.session.store

            [noir.util.middleware :as nm]
            [noir.session :as session]
            [noir.util.route :as nr]
            [noir.validation :as nv]

            [emberclj.data-routes :as dr]
            [emberclj.auth-routes :as ar]
            [emberclj.utils :as u]))

(def ^:private conversions
  "a map of parameter name and converter"
  {:user-id u/s->int})

(defn- convert-params
  "converts specific parameters to their proper types"
  [params]
  (reduce (fn [m [k v]]
            (if-let [converter-fn (conversions k)]
              (assoc m k (converter-fn v))
              m))
          params params))

(defn- convert-params-handler
  "a compojure handler to convert incoming parameters"
  [app]
  (fn [{params :params :as req}]
    (app (assoc req :params (convert-params params)))))

(defn- emberise-response-handler
  "a compojure handler to convert clojure objects with hyphenated keywords to underscored keywords"
  [app]
  (fn [req]
    (let [response (app req)
          response (update-in response [:body] u/make-emberable)]
      response)))

(defn- check-session-handler
  "a compojure handler to ensure the session is in place"
  [app]
  (fn [req]
    (if (session/get :user-id)
      (app req)
      (resp/status (resp/response "Not Authorised") 401))))

(defroutes app-routes

  (GET "/" [] (resp/redirect "/index.html"))
  
  (context "/data" []
           (-> dr/data-routes
               ;check-session-handler
               json/wrap-json-params convert-params-handler emberise-response-handler json/wrap-json-response))

  (context "/auth" []
           (-> ar/auth-routes convert-params-handler json/wrap-json-response))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (nm/app-handler
          [app-routes]))
