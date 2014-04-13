(ns getinvolved.test.handler
  (:use clojure.test
        ring.mock.request
        getinvolved.handler))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (re-matches #"(?is).*getinvolved.*" (:body response)))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))
