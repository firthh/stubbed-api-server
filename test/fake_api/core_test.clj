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
