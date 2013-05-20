(ns clj-lti.middleware
  (:require [clj-lti.oauth :as oauth]))

(defn url
  "Construct the URL of the current request"
  [request]
  (let [scheme (name (:scheme request))
        host (get-in request [:headers "host"])
        uri (:uri request)]
    (str scheme "://" host uri)))

(defn valid-request?
  "Determine if the given request is properly signed."
  [consumer-secret request]
  (let [params (dissoc (:params request) :oauth_signature)
        method (-> request :request-method name .toUpperCase)
        uri (url request)
        given-signature (-> request :params :oauth_signature)]
    (= given-signature (oauth/sign consumer-secret (oauth/base-string method uri params)))))

(defn lti-middleware
  [handler]
  (fn [request]
    (handler request)))
