(defproject emberclj "0.1.0-SNAPSHOT"
  :description "A REST backend generator for ember.js projects"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.7.0"]
                 [postgresql "9.1-901-1.jdbc4"]
                 [korma "0.3.0-RC5"]
                 [com.draines/postal "1.11.0"]
                 [ring/ring-json "0.2.0"]
                 [camel-snake-kebab "0.1.2"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [expectations "1.4.56"]]
  :plugins [[lein-ring "0.8.6"]
            [lein-marginalia "0.7.1"]]
  :ring {:handler emberclj.handler/app})
