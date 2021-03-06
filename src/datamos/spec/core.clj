(ns datamos.spec.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(s/def :dms-rdf/value string?)
(s/def :dms-rdf/lang string?)
(s/def :dms-rdf/type keyword?)

(s/def ::literal-type
  (s/keys :req [:dms-rdf/value]
          :opt [:dms-rdf/type :dms-rdf/lang]))

(s/def ::node-blank (s/map-of ::property ::object :min-count 0))
(s/def ::coll (s/coll-of ::object :kind vector? :min-count 1))

(s/def ::subject-types (s/or :uri keyword?
                             :blank-node ::node-blank
                             :collection ::coll))

(s/def ::object-types (s/or :uri keyword?
                            :number number?
                            :literal string?
                            :typed-literal ::literal-type
                            :boolean-literal boolean?
                            :blank-node ::node-blank
                            :collection ::coll))

(s/def ::subject ::subject-types)
(s/def ::property keyword?)                                 ;; Using property instead of predicate. Because Clojure already uses predicate in functions like s/explain
(s/def ::object ::object-types)

(s/def ::property-object (s/map-of ::property ::object :min-count 1))
(s/def ::statement (s/map-of ::subject ::property-object :min-count 1))
(s/def ::named-graphs (s/map-of keyword? ::statement :min-count 1))

(s/def ::prefix (s/map-of keyword? string? :min-count 1))

(s/def ::rdf-content
  (s/or :prefixes (s/map-of #{:datamos/prefix} ::prefix :min-count 1)
        :triples (s/map-of #{:datamos/triples} ::statement :min-count 1)
        :quads (s/map-of #{:datamos/quads} ::named-graphs :min-count 1)))

(s/def ::logis-props
  (s/map-of #{:dms-def/rcpt-fn} keyword?))

(s/def ::message
  (s/or :rdf (s/map-of #{:datamos/rdf-content} ::rdf-content)
        :logis (s/map-of #{:datamos/logistic} ::logis-props)))
