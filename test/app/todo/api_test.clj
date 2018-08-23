(ns app.todo.api-test
  (:require [clojure.test :refer :all]
            [testit.core :refer :all]
            [app.todo.test-util :refer [find-handler]]
            [app.todo.api :as todo])
  (:import (java.util UUID)))

(def find-by-email (find-handler todo/commands :todo/find-by-email))

(deftest find-by-email-test
  (fact
    (find-by-email {:input {:user nil}})
    => (throws-ex-info any {:response {:status 404}}))
  (fact
    (find-by-email {:input {:user  ::user
                            :todos ::todos}})
    => {:status 200
        :body   {:todos ::todos}}))

(def create (find-handler todo/commands :todo/create))

(deftest create-test
  (fact
    (create {:input      {:user {:_id   "user-id"
                                 :email "user-email"}}
             :parameters {:body {:todo "todo text"}}})
    => {:status 200
        :body   {:message string?}
        :output [[:insert :todos
                  {:_id   UUID
                   :user  "user-id",
                   :todo  "todo text",
                   :done? false}]
                 [:update :users
                  {:email "user-email"}
                  {"$inc" {:todos 1}}]]}))

