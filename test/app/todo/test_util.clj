(ns app.todo.test-util)

(defn find-handler [commands name]
  (->> commands
       (filter (comp (partial = name) :name))
       (first)
       :handler))
