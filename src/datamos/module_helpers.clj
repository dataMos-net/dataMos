(ns datamos.module-helpers
  (:require [datamos
             [rdf-function :as rdf-fn]
             [communication :as dcom]
             [messaging :as dm]]
            [taoensso.timbre :as log]))

(defn retrieve-prefixes
  [rdf-map]
  (mapv keyword
       (set (keep namespace
                  (filter keyword?
                          (tree-seq coll? seq
                                    rdf-map))))))

(defn get-prefix-matches
  [speak-conn exchange-settings module-settings rdf-map]
  (dcom/speak speak-conn
              exchange-settings
              module-settings
              :datamos-fn/prefix
              :dms-def/module
              :datamos/match-prefix
              {:dms-def/message {:dms-def/namespaces (retrieve-prefixes rdf-map)}}))

(defn local-module-register
  [local-register]
  {:datamos/registry (fn register
                       [_ _ message]
                       (let [rdf-content (rdf-fn/message-content message)
                             r           @local-register
                             values      (rdf-fn/values-by-predicate :dms-def/function
                                                                     rdf-content
                                                                     r)]
                         (log/debug "@register" (log/get-env))
                         (when (apply = values)
                           (do
                             (log/trace "@register - duplicate module-fns" (log/get-env))
                             (swap! local-register (fn [m]
                                                        (dissoc m
                                                                (first (rdf-fn/subject-object-by-predicate m :dms-def/function)))))))
                         (swap! local-register conj rdf-content)))})