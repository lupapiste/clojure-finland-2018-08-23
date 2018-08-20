(ns app.db
  (:require [integrant.core :as ig]
            [mingler.core :as m]
            [clj-uuid :as uuid]))

;;
;; Use UUID ID's:
;;

(defn create-id []
  (uuid/v1))

;;
;; Client:
;;

(defmethod ig/init-key ::client [_ config]
  (m/open-client config))

(defmethod ig/halt-key! :core.db/client [_ client]
  (m/close-client client))

;;
;; DB:
;;

(defmethod ig/init-key ::db [_ {:keys [client database]}]
  (m/database client database))
