(ns badger.core
  (:gen-class))
(use 'ring.adapter.jetty)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn handler [request]
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body "{\"response_type\": \"ephemeral\",\"text\": \"Here are the currently open tickets:\" }"
  })

(defn -main
  "starts web application"
  [& args]
  (println "starting app")
  (run-jetty handler {:port 80})
  )
