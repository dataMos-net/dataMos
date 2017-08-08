(ns datamos.base
  (:require [mount.core :as mnt :refer [defstate]]))

(defonce ^:private component-config (atom {}))               ; Stores the component specific configuration for first mount state.

(defn component-function
  "Give the type of component, by providing type keyword and function keyword"
  [settings]
  (reset! component-config settings))

(defn generate-qualified-uri
  "Return unique uri, based on type-kw."
  [type-kw]
  (keyword (str (namespace type-kw) "/" (name type-kw) "+dms-fn+" (java.util.UUID/randomUUID))))

(defn set-component
  "Returns component settings. With component-type, component-fn and component-uri as a submap of :datamos-cfg/component."
  [settings]
  (let [{:keys [:datamos-cfg/component-fn
                :datamos-cfg/component-type
                :dms-def/provides
                :dms-def/function
                :datamos-cfg/local-register]
         :or {component-type :datamos-fn/enrichment
              component-fn :datamos-fn/function}} settings]
    {(generate-qualified-uri component-fn) {:datamos-cfg/component-type component-type
                                            :datamos-cfg/component-fn   component-fn
                                            :dms-def/provides           provides
                                            :datamos-cfg/local-register local-register
                                            :rdf/type                   :dms-def/component}}))

(defstate ^{:on-reload :noop} component :start (set-component @component-config))