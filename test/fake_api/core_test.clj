(ns fake-api.core-test
  (:require [clojure.test :refer :all]
            [fake-api.core :refer :all]
            [schema.core :as s]
            [ring.mock.request :as mock]))

(def request
  {
   :headers {}
   :content-type nil
   :uri "/test",
   :query-string nil
   :request-method :get
   })

(deftest matches-path-test
  (testing "matches a correct uri"
    (is (matches-path? request {:uri "/test" :request-method :get})))

  (testing "doesn't match an incorrect uri"
    (is (not (matches-path? request {:uri "/" :request-method :get}))))

  (testing "doesn't match an incorrect request-method"
    (is (not (matches-path? request {:uri "/test" :request-method :post})))))

(def path
  {
   :parameters [
                {:in "query",
                 :name "x",
                 :description "",
                 :required true,
                 :type "integer",
                 :format "int64"}
                {:in "query",
                 :name "y",
                 :description "",
                 :required true,
                 :type "integer",
                 :format "int64"}
                {:in "query",
                 :name "z",
                 :description "",
                 :required false,
                 :type "integer",
                 :format "int64"}]})

(deftest matches-query-params
  (testing "empty params and no required params"
    (is (matches-query-params? {:query-params {}} {:parameters []})))
  (testing "correct params when theay exist"
    (is (matches-query-params?
         {:query-params {:x "1" :y "2"}}
         path))
    (is (matches-query-params?
         {:query-params {:x "1" :y "2" :z "3"}}
         path)))
  (testing "extra params are allowed"
    (is (matches-query-params?
         {:query-params {:x "1" :y "2" :a "5"}}
         path))
    (is (matches-query-params?
         {:query-params {:x "1" :y "2" :b "5"}}
         path)))
  (testing "missing one required param"
    (is (not (matches-query-params?
              {:query-params {:x "1"}}
              path)))
    (is (not (matches-query-params?
              {:query-params {:x "1" :z "3"}}
              path))))
  (testing "params of the wrong time"
    (is (not (matches-query-params?
              {:query-params {:x "1" :y "a"}}
              path)))))

(deftest test-swagger-params->schemas
  (testing "empty params"
    (is (= {} (swagger-params->schemas (:parameters path))))))

(deftest test-swagger-types->schema
  (testing "empty types"
    (is (= {} (swagger-types->schema []))))
  (testing "a single integer type"
    (is (= {:a IntStr} (swagger-types->schema [{:name "a",
                                               :required true,
                                               :type "integer",
                                               :format "int64"}])))
    ;; (is (= {:a s/Int} (swagger-types->schema [{:name "a",
    ;;                                            :required true,
    ;;                                            :type "integer",
    ;;                                            :format "int32"}])))
    )
  ;; (testing "a single float"
  ;;   (is (= {:a FloatS} (swagger-types->schema [{:name "a",
  ;;                                              :required true,
  ;;                                              :type "number",
  ;;                                              :format "float"}]))))
  (testing "a single double"
    (is (= {:a DoubleStr} (swagger-types->schema [{:name "a",
                                                   :required true,
                                                   :type "number",
                                                   :format "double"}]))))
  (testing "a single string"
    (is (= {:a s/Str} (swagger-types->schema [{:name "a",
                                               :required true,
                                               :type "string"}]))))
  ;; TODO: string binary
  ;; TODO: string byte
  ;; TODO: string date
  ;; TODO: string date-time
  ;; TODO: boolean nil

  (testing "multiple strings"
    (is (= {:a s/Str
            :b s/Str}
           (swagger-types->schema [{:name "a",
                                    :required true,
                                    :type "string"}
                                   {:name "b",
                                    :required true,
                                    :type "string"}]))))

  (testing "an optional string"
    (is (= {(s/optional-key :a) s/Str}
           (swagger-types->schema [{:name "a",
                                    :required false,
                                    :type "string"}]))))
  )

(deftest test-parse-path
  (testing "integration test"
    (is (= (first (parse-paths (read-schema)))
           {:uri "/api/plus",
            :request-method :get,
            :parameters [{:in "query", :name "x", :description "", :required true, :type "integer", :format "int64"} {:in "query", :name "y", :description "", :required true, :type "integer", :format "int64"}],
            :query-schema {:x IntStr
                           :y IntStr
                           s/Any s/Any}}))
    (is (= (second (parse-paths (read-schema)))
           {:uri "/api/echo",
            :request-method :post,
            :parameters [{:in "body", :name "Pizza", :description "", :required true, :schema {:$ref "#/definitions/Pizza"}}],
            :query-schema {s/Any s/Any}}))))
