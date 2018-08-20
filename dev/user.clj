(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as igr]
            [integrant.repl.state :as state]))

(igr/set-prep!
  (fn []
    (require 'app.components)
    (-> 'app.components/components
        (resolve)
        (deref)
        (doto (ig/load-namespaces)))))

(def reset igr/reset)
(def start igr/init)
(def stop igr/halt)
(defn system [] state/system)
