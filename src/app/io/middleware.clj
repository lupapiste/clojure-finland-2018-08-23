(ns app.io.middleware
  (:require [integrant.core :as ig]
            [app.io.input :as i]
            [app.io.output :as o]))

(defmethod ig/init-key ::in [_ {:keys [db]}]
  {:name    ::in
   :compile (fn [{:keys [app.command/command]} _]
              (when-let [input (-> command :input)]
                (fn [handler]
                  (fn [request]
                    (-> request
                        (i/handle-input db input)
                        (handler))))))})

;;
;; Process updates:
;;

(defmethod ig/init-key ::out [_ {:keys [db]}]
  {:name    ::out
   :compile (fn [{:keys [app.command/command]} _]
              (when (-> command :pure?)
                (fn [handler]
                  (fn [request]
                    (-> request
                        (handler)
                        (o/handle-output db))))))})

;;
;; Non-pure commands:
;;

(defmethod ig/init-key ::non-pure [_ {:keys [db]}]
  {:name    :non-pure
   :compile (fn [{:keys [app.command/command]} _]
              (when-not (-> command :pure?)
                (fn [handler]
                  (fn [request]
                    (-> request
                        (assoc :app.db/db db)
                        (handler))))))})
