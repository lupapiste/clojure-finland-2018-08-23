(ns app.io.output
  (:require [schema.core :as s]
            [mingler.core :as m]
            [app.io.schema :as io-schema]))

(def validate-output (s/validator io-schema/Output))

(defn handle-output [response db]
  (doseq [[op coll & args] (some-> response :output (validate-output))]
    (let [f (case op
              :insert m/insert
              :update m/update)
          c (m/collection db coll)]
      (apply f c args)))
  (dissoc response :output))
