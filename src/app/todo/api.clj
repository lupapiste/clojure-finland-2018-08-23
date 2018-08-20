(ns app.todo.api
  (:require [schema.core :as s]
            [ring.util.http-response :as resp]
            [mingler.op :refer :all]
            [app.db :as db])
  (:import (java.util UUID)))

(def commands
  [{:name    :find-by-email
    :summary "Find ToDos by user email"
    :request {:email s/Str
              :done? s/Bool}
    :input   [[:user [:find-one :users {:email ^:q [:body :email]}]]
              [:todos [:find-all :todos {:user  ^:q [:input :user :_id]
                                         :done? ^:q [:body :done?]}]]]
    :handler (fn [request]
               (when-not (-> request :input :user)
                 (resp/not-found!))
               (resp/ok {:todos (-> request :input :todos)}))}

   {:name    :create
    :summary "Create new ToDo"
    :request {:email s/Str
              :todo  s/Str}
    :input   [[:user [:find-one :users {:email ^:q [:body :email]}]]]
    :handler (fn [request]
               (let [user (-> request :input :user)
                     todo (-> request :parameters :body :todo)
                     id   (db/create-id)]
                 (-> {:message (str "New ToDo created, id=" id)}
                     (resp/ok)
                     (assoc :output [[:insert :todos
                                      {:_id   id
                                       :user  (-> user :_id)
                                       :todo  todo
                                       :done? false}]
                                     [:update :users
                                      {:email (-> user :email)}
                                      {$inc {:todos 1}}]]))))}

   {:name    :mark-done
    :summary "Mark ToDo as done"
    :request {:_id UUID}
    :input   [[:todo [:find-one :todos {:_id ^:q [:body :_id]}]]]
    :handler (fn [request]
               (when-not (-> request :input :todo)
                 (resp/not-found!))
               (when (-> request :input :todo :done?)
                 (resp/bad-request! {:message "ToDo is already done"}))
               (let [_id (-> request :parameters :body :_id)]
                 (-> {:message "ToDo marked as done"}
                     (resp/ok)
                     (assoc :output [[:update :todos
                                      {:_id _id}
                                      {$set {:done? true}}]]))))}])
