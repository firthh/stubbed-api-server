(ns fake-api.core
  (:require [cheshire.core :refer [parse-string]]
            [clojure.java.io :as io]))

;; loading data

(defn read-schema []
  (-> "swagger.json"
      io/resource
      slurp
      (parse-string true)))

(defn parse-path [base-path [path requests]]
  (map
   (fn [[k v]]
     {:uri (str base-path (name path))
      :request-method k
      :parameters (:parameters v)})
   requests))

(defn parse-paths [schema]
  (let [base-path (:basePath schema)]
    (mapcat (partial parse-path base-path) (:paths schema))))

(def paths (delay (parse-paths (read-schema))))

;; matching requests

(defn matches-uri? [request path]
  (= (:uri request) (:uri path)))

(defn matches-request-method? [request path]
  (= (:request-method request) (:request-method path)))

(defn matches-path? [request path]
  (and
   (matches-uri? request path)
   (matches-request-method? request path)))

;; handling requests

(defn handler [request]
  (if-let [match (first (filter (partial matches-path? request) @paths))]
    {:status 200
     :body "wooo"}
    {:status 404
     :body "Not found"}))
