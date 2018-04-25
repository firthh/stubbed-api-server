(ns fake-api.core
  (:require [cheshire.core :refer [parse-string]]
            [ring.middleware.params :refer [wrap-params]]
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

(def type-matches
  {"integer" integer?})

(defn has-all-required-params? [required-params query-params]
  (every? (fn [p] (and (get query-params (:name p))
                      ((type-matches (:type p)) (get query-params (:name p)))))
          required-params))

(defn matches-query-params? [request path]
  (let [query-params (:query-params request)
        required-params (filter :required (:parameters path))]
    (and (has-all-required-params? required-params query-params))))

(defn matches-path? [request path]
  (and
   (matches-uri? request path)
   (matches-request-method? request path)
   (matches-query-params? request path)))

;; handling requests

(defn handler [request]
  (if-let [match (first (filter (partial matches-path? request) @paths))]
    {:status 200
     :body "wooo"}
    {:status 404
     :body "Not found"}))

(def app
  (-> handler
      wrap-params))
