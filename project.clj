(defproject fake-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire            "5.8.0"]
                 [ring                "1.6.3"]
                 [prismatic/schema    "1.1.9"]]
  :plugins [[lein-ring "0.12.3"]
            [lein-auto "0.1.3"]]
  :ring {:handler fake-api.core/app})
