(ns fake-api.core)

(defn handler [request]
  {:status 200
   :body "hello world"})
