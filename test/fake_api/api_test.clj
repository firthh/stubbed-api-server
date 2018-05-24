(ns fake-api.api-test
  (:require [clojure.test :refer :all]
            [fake-api.core :refer :all]
            [schema.core :as s]
            [ring.mock.request :as mock]))

(deftest test-handler
  ;; TODO: write integration tests around the handler
  (is (= (:status (app (mock/request :get "/api/plus?x=13&y=24")))
         200))
  (is (= (:status (app (mock/request :get "/api/plus?x=1&y=abc")))
         400)))
