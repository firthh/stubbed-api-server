(ns fake-api.core
  (:require [cheshire.core :refer [parse-string]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.java.io :as io]
            [schema.core :as s]))

;; loading data

(defn read-schema [file-name]
  (-> file-name
      io/resource
      slurp
      (parse-string true)))

(def DoubleS
  (s/pred double?))
(def DoubleStr
  (s/pred #(re-matches #"^[0-9]+(\.[0-9]+)?$" %)))

(def IntStr
  (s/pred #(re-matches #"^[0-9]+" %)))

(def type-mapping
  ;; Should this support `format` as well as type?
  ;; QueryString params are strings, let's leave them that way
  {
   "integer" IntStr
   "number"  DoubleStr
   "string"  s/Str
   })

(defn schema-key [type]
  (if (:required type)
    (keyword (:name type))
    (s/optional-key (keyword (:name type)))))

(defn swagger-types->schema [types & [allow-extra-keys]]
  (merge (->> types
              (map (fn [t] [(schema-key t)
                           (type-mapping (:type t))]))
              (into {}))
         (if allow-extra-keys {s/Any s/Any} {})))

(defn swagger-params->schemas [swagger]
  {})

(defn parse-path [base-path [path requests]]
  (map
   (fn [[k v]]
     {:uri (str base-path (name path))
      :request-method k
      :parameters (:parameters v)
      :query-schema (swagger-types->schema (filter (fn [t] (= "query" (:in t))) (:parameters v)) true)})
   requests))

(defn parse-paths [schema]
  (let [base-path (:basePath schema)]
    (mapcat (partial parse-path base-path) (:paths schema))))

(defn paths [file-name]
  (parse-paths (read-schema file-name)))

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
        ;; TODO: This no longer needs to be here any more
        QueryParams (swagger-types->schema (filter (fn [t] (= "query" (:in t))) (:parameters path)) true)]
    (if-let [r (s/check QueryParams query-params)]
      (do
        ;; (prn r) ;; make it easier to debug. Should we return this in the http response?
        false)
      true)))

(defn matches-path? [request path]
  (when (and
         (matches-uri? request path)
         (matches-request-method? request path))
    path))

;; handling requests

(defn handler [paths]
  (fn [request]
    (if-let [match (first (filter (partial matches-path? request) paths))]
      (if (matches-query-params? request match)
        {:status 200
         :body "wooo"}
        {:status 400})
      {:status 404
       :body "Not found"})))

(defn keywordize-keys [m]
  (into {} (map (fn [[k v]] [(keyword k) v]) m)))

(defn wrap-keywordize-params [handler]
  (fn [request]
    (handler (update request :query-params keywordize-keys))))

(def app
  (-> (handler (paths "swagger.json"))
      wrap-keywordize-params
      wrap-params))
