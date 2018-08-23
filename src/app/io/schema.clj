(ns app.io.schema
  (:require [schema.core :as s :refer [defschema]]))

;;
;; Command input:
;;

(defschema Query
  [(s/one (s/enum :find-one :find-all) 'op)
   (s/one s/Keyword 'collection)
   (s/one {s/Any s/Any} 'filter-document)])

(defschema CommandInput
  [[(s/one s/Keyword 'key) (s/one Query 'query)]])

;;
;; Command output:
;;

(defschema Insert
  [(s/one s/Keyword 'op)
   (s/one s/Keyword 'collection)
   (s/one {s/Any s/Any} 'document)])

(defn insert? [update]
  (-> update first (= :insert)))

(defschema Update
  [(s/one s/Keyword 'op)
   (s/one s/Keyword 'collection)
   (s/one {s/Any s/Any} 'filter-document)
   (s/one {s/Any s/Any} 'update-document)])

(defn update? [update]
  (-> update first (= :update)))

(defschema Output
  [(s/conditional
     insert? Insert
     update? Update)])
