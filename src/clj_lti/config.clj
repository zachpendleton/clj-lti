(ns clj-lti.config
 (:require [clojure.data.xml :as xml]))

(def blti-params
  { "xmlns"               "http://www.imsglobal.org/xsd/imslticc_v1p0",
    "xmlns:blti"          "http://www.imsglobal.org/xsd/imsbasiclti_v1p0",
    "xmlns:lticm"         "http://www.imsglobal.org/xsd/imslticm_v1p0",
    "xmlns:lticp"         "http://www.imsglobal.org/xsd/imslticp_v1p0",
    "xmlns:xsi"           "http://www.w3.org/2001/XMLSchema-instance",
    "xsi:schemaLocation"  "http://www.imsglobal.org/xsd/imslticc_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticc_v1p0.xsd http://www.imsglobal.org/xsd/imsbasiclti_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imsbasiclti_v1p0p1.xsd http://www.imsglobal.org/xsd/imslticm_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticm_v1p0.xsd http://www.imsglobal.org/xsd/imslticp_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticp_v1p0.xsd"})

(defn lticm-property [k v]
  ["lticm:property" {:name (name k)} v])

(defn lticm-options [k v]
  ["lticm:options" {:name (name k)}
    (map #(apply lticm-property %) v)])

(defn lticm-element [k v]
  (if (map? v)
    (lticm-options k v)
    (lticm-property k v)))


(defn blti-extension [platform elements]
  ["blti:extensions" {:platform platform}
  (map #(apply lticm-element %) elements)])

(defn blti-extensions [extensions]
  (map #(apply blti-extension %) extensions))

(defn blti-elements [k v]
  (if (= k :extensions)
    (blti-extensions v)
    [(str "blti:" (name k)) v]))

(defn blti-xml [options]
  [:cartridge_basiclti_link blti-params
    (map #(apply blti-elements %) options)])

(defn generate-xml [options]
  (xml/emit-str (xml/sexp-as-element
    (blti-xml options))))
