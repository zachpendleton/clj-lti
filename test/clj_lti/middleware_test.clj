(ns clj-lti.middleware-test
  (:require [clojure.test :refer :all]
            [clj-lti.middleware :refer :all]))

(def request {:params {:status "Hello Ladies + Gentlemen, a signed OAuth request!"
                       :include_entities "true"
                       :oauth_consumer_key "xvz1evFS4wEEPTGEFPHBog"
                       :oauth_signature "tnnArxj06cWHq44gCs1OSKk/jLY="
                       :oauth_nonce "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg"
                       :oauth_signature_method "HMAC-SHA1"
                       :oauth_timestamp "1318622958"
                       :oauth_token "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb"
                       :oauth_version "1.0"}
               :request-method :post
               :scheme :https
               :headers { "host" "api.twitter.com" }
               :uri "/1/statuses/update.json"})
(def signing-key "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE")
(defn handler [request] {:status 200 :headers {} :body "it works"})

(deftest url-test
  (testing "Can create a URL"
    (is (= (url request) "https://api.twitter.com/1/statuses/update.json"))))

(deftest valid-request?-test
  (testing "Validates properly signed requests"
    (is (= (valid-request? signing-key request) true)))
  (testing "Does not validate improperly signed requests"
    (let [req (assoc-in request [:params :oauth_timestamp] "12345")]
      (is (= (valid-request? signing-key req) false)))))
