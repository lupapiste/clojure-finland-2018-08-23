(ns app.web
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [immutant.web :as immutant]
            [ring.middleware.params :as params]
            [ring.util.http-response :as resp]
            [muuntaja.core :as muuntaja]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as ring-coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja-middleware]
            [reitit.ring.middleware.exception :as exception-middleware]
            [reitit.coercion.schema :as schema-coercion]
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

(def exception-middleware (exception-middleware/create-exception-middleware
                            (merge exception-middleware/default-handlers
                                   {::exception-middleware/default (fn [e _]
                                                                     (log/error e "unexpected error")
                                                                     (resp/internal-server-error "Unexpected internal error"))
                                    ::resp/response                (fn [e _] (-> e ex-data :response))})))

(def default-middleware [params/wrap-params
                         muuntaja-middleware/format-negotiate-middleware
                         muuntaja-middleware/format-response-middleware
                         muuntaja-middleware/format-request-middleware
                         ring-coercion/coerce-response-middleware
                         ring-coercion/coerce-request-middleware
                         exception-middleware])

(defmethod ig/init-key ::handler [_ {:keys [commands middleware]}]
  (ring/ring-handler
    (ring/router
      [""
       ["/api/" (map command->route commands)]
       ["/swagger.json" {:get {:no-doc  true
                              :swagger {:info {:title "API"}}
                              :handler (swagger/create-swagger-handler)}}]]
      {:data {:coercion   schema-coercion/coercion
              :muuntaja   muuntaja/instance
              :middleware (concat default-middleware middleware)}})
    (some-fn (swagger-ui/create-swagger-ui-handler {:path "/swagger", :url "/swagger.json"})
             (constantly (resp/not-found "Say what?")))))

(def defaults {:host "localhost"
               :port 3000
               :path "/"})

(defmethod ig/init-key ::server [_ {:keys [handler opts]}]
  (immutant/run handler (merge defaults opts)))

(defmethod ig/halt-key! ::server [_ server]
  (immutant/stop server))
