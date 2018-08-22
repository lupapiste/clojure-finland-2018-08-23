(ns app.components
  (:require [integrant.core :as ig]))

(defn dev-mode? []
  (or (-> (System/getProperty "app.mode") (= "dev"))
      (-> (System/getenv "APP_MODE") (= "dev"))))

(def components
  {:app.command/commands       {:commands (concat '[app.todo.api/commands]
                                                  (when (dev-mode?)
                                                    '[app.dev/commands]))}

   :app.io.middleware/in       {:db (ig/ref :app.db/db)}
   :app.io.middleware/out      {:db (ig/ref :app.db/db)}
   :app.io.middleware/non-pure {:db (ig/ref :app.db/db)}

   :app.db/client              {:servers [{:host "localhost"}]}
   :app.db/db                  {:client   (ig/ref :app.db/client)
                                :database :sample-app-db}

   :app.web/handler            {:commands   (ig/ref :app.command/commands)
                                :middleware [(ig/ref :app.io.middleware/in)
                                             (ig/ref :app.io.middleware/out)
                                             (ig/ref :app.io.middleware/non-pure)]}

   :app.web/server             {:opts    {:host "localhost"
                                          :port 3300}
                                :handler (ig/ref :app.web/handler)}})
