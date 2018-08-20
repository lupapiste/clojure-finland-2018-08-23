(ns app.io.input
  (:require [clojure.walk :as walk]
            [mingler.core :as m]))

(defn resolve-params [ctx filter-document]
  (walk/prewalk
    (fn [v]
      (if (-> v meta :q)
        (get-in ctx v)
        v))
    filter-document))

(defn handle-query [db [op collection filter-document] input body]
  (let [f     (case op
                :find-one m/find-one
                :find-all m/find-all)
        coll  (m/collection db collection)
        ctx   {:body  body
               :input input}
        query (resolve-params ctx filter-document)]
    (f coll query)))

(defn handle-input [request db command-input]
  (let [body  (-> request :parameters :body)
        input (reduce (fn [acc [id query]]
                        (assoc acc id (handle-query db query acc body)))
                      {}
                      command-input)]
    (assoc request :input input)))
