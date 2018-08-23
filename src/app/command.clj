(ns app.command
  (:require [integrant.core :as ig]
            [schema.core :as s :refer [defschema]]
            [app.io.schema :as io.schema])
  (:import (clojure.lang Fn)))

(defschema Command
  {:name     s/Keyword
   :summary  s/Str
   :request  s/Any
   :response s/Any
   :handler  Fn
   :pure?    s/Bool
   :input    (s/maybe io.schema/CommandInput)})

(def command-defaults {:summary  ""
                       :request  s/Any
                       :response s/Any
                       :pure?    true
                       :input    nil})

(defn ->command [command]
  (->> (merge command-defaults command)
       (s/validate Command)))

(defn- load-sym [sym]
  (-> sym
      (namespace)
      (symbol)
      (require))
  (-> sym
      (resolve)
      (deref)))

(defmethod ig/init-key ::commands [_ {:keys [commands]}]
  (->> commands
       (mapcat load-sym)
       (mapv ->command)))
