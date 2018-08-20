(ns app.dev
  (:require [ring.util.http-response :as resp]
            [mingler.core :as m]
            [app.db :as db]))

(defn apply-fixture [db]
  (println "Applying DB fixture!")
  (let [users (m/collection db :users)
        todos (m/collection db :todos)
        data  {"pena@example.com"  ["Remember the milk"]
               "sonja@example.com" ["Fix example app"
                                    "Write README"]}]
    (m/delete-many users {})
    (m/delete-many todos {})
    (doseq [[email user-todos] data]
      (let [user-id (db/create-id)]
        (m/insert users {:_id   user-id
                         :email email
                         :todos (count user-todos)})
        (m/insert-many todos (for [todo user-todos]
                               {:_id (db/create-id), :user user-id, :todo todo, :done? false}))))
    (println "Fixture ready")))

(def commands
  [{:name    :apply-fixture
    :pure?   false
    :handler (fn [request]
               (apply-fixture (-> request :app.db/db))
               (resp/ok {:message "Fixture applied successfully"}))}])

(comment

  (def db (-> (user/system) :app.db/db))
  (def users (m/collection db :users))
  (def todos (m/collection db :todos))

  (apply-fixture db)

  (m/find-all users {})
  (m/find-all todos {})

  )


