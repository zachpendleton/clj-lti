(ns clj-lti.oauth
  (:import oauth.signpost.OAuth
           javax.crypto.Mac
           javax.crypto.spec.SecretKeySpec)
  (:require [clojure.walk :as walk]
            [clojure.string :as string]
            [clojure.data.codec.base64 :as base64]))

(declare percent-encode)

(def join-params (partial string/join "&"))
(def sort-params (partial sort-by first))
(def percent-encode-params (partial map #(map percent-encode %1)))
(def stringify-params (partial map (fn [[k v]] (str k "=" v))))

(defn percent-encode
  "Percent-encode a given string."
  [param]
  (OAuth/percentEncode param))

(defn encode-params
  [params]
  (-> params
    walk/stringify-keys
    percent-encode-params
    sort-params
    stringify-params
    join-params))

(defn base-string [method url params]
  (join-params [(.toUpperCase method)
                (percent-encode url)
                (percent-encode (encode-params params))]))

(defn sign
  [^String secret ^String subject]
  (let [signing-key (SecretKeySpec. (.getBytes secret) "HmacSHA1")
        mac (doto (Mac/getInstance "HmacSHA1") (.init signing-key))]
    (String. (base64/encode (.doFinal mac (.getBytes subject))) "UTF-8")))
