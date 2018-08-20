(ns app.web
  (:require [integrant.core :as ig]
            [immutant.web :as immutant]
            [schema.core :as s]
            [slingshot.slingshot :refer [try+]]
            [ring.middleware.params :as params]
            [ring.util.http-response :as resp]
            [muuntaja.middleware :as muuntaja]
            [reitit.ring :as ring]
            [reitit.coercion.schema :as schema-coercion]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.exception :as exception]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [app.command :as c]))

(defn command->route
  "Accepts a command and returns a reitit route for command."
  [command]
  [(-> command :name name)
   {:name       (->> command :name)
    :parameters {:body (-> command :request)}
    :responses  {200 {:body (-> command :response)}}
    :post       {:summary (-> command :summary)
                 :handler (-> command :handler)}
    ::c/command command}])

(def exception-middleware
  (exception/create-exception-middleware
    (merge exception/default-handlers {::resp/response (fn [e _] (-> e ex-data :response))})))

(defmethod ig/init-key ::handler [_ {:keys [commands middleware]}]
  (ring/ring-handler
    (ring/router
      ["/"
       ["api/" {:swagger {:id ::api}}
        (map command->route commands)]
       ["swagger.json" {:get {:no-doc  true
                              :swagger {:info {:title "API"}}
                              :handler (swagger/create-swagger-handler)}}]]
      {:data {:coercion   schema-coercion/coercion
              :middleware (concat [params/wrap-params
                                   muuntaja/wrap-format
                                   muuntaja/wrap-exception
                                   swagger/swagger-feature
                                   rrc/coerce-exceptions-middleware
                                   rrc/coerce-request-middleware
                                   rrc/coerce-response-middleware
                                   exception-middleware]
                                  middleware)
              :swagger    {:produces #{"application/json" "application/edn" "application/transit+json"}
                           :consumes #{"application/json" "application/edn" "application/transit+json"}}}})
    (some-fn (swagger-ui/create-swagger-ui-handler {:path "/swagger", :url "/swagger.json"})
             (constantly (resp/not-found "Say what?")))))

(def defaults {:host "localhost"
               :port 3000
               :path "/"})

(defmethod ig/init-key ::server [_ {:keys [handler opts]}]
  (immutant/run handler (merge defaults opts)))

