(ns fake-api.core-test
  (:require [clojure.test :refer :all]
            [fake-api.core :refer :all]))

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
         {:query-params {"x" 1 "y" 2}}
         path))
    (is (matches-query-params?
         {:query-params {"x" 1 "y" 2 "z" 3}}
         path)))
  (testing "extra params are allowed"
    (is (matches-query-params?
         {:query-params {"x" 1 "y" 2 "a" 5}}
         path))
    (is (matches-query-params?
         {:query-params {"x" 1 "y" 2 "b" 5}}
         path)))
  (testing "missing one required param"
    (is (not (matches-query-params?
              {:query-params {"x" 1}}
              path)))
    (is (not (matches-query-params?
              {:query-params {"x" 1 "z" 3}}
              path))))
  (testing "params of the wrong time"
    (is (not (matches-query-params?
              {:query-params {"x" 1 "y" "a"}}
              path)))))
