(ns emberclj.auth-routes
  (:require [compojure.core :refer :all]
            [ring.util.response :as resp]))

(defn dummy-authenticator
  "this is a dummy authenticator, replace this with something useful"
  []
  true)

(defroutes auth-routes
  (POST "/login" {params :params}
        (if-let [auth-ctx (dummy-authenticator)]
          (resp/response {:success true
                          :context auth-ctx})
          (resp/response {:success false}))))
