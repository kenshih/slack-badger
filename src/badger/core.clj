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
    :body (str "{\"response_type\": \"in_channel\",\"text\": \"Here are the top Badgers!\" "
    ",\"attachments\": [{\"fallback\": \"Coverage Cueen\", \"color\": \"#36a64f\", \"title\": \"Coverage Cueen\",\"footer\": \"covered so much code, the code could sleep well\",\"footer_icon\": \"http://www.gummyworm.net/wp-content/uploads/2015/02/Pinguino-png-129x129.png\"},{\"fallback\": \"Required plain-text summary of the attachment.\", \"color\": \"#36a64f\", \"title\": \"Hackathon winner\",\"footer\": \"no one can ever take that from ya!\",\"footer_icon\": \"https://platform.slack-edge.com/img/default_application_icon.png\"}]"
    "}"
    )
  })

; \"attachments\": [{\"fallback\": \"Coverage Cueen\", \"color\": \"#36a64f\", \"title\": \"Coverage Cueen\",\"footer\": \"covered so much code, the code could sleep well\",\"footer_icon\": \"http://www.gummyworm.net/wp-content/uploads/2015/02/Pinguino-png-129x129.png\"},{\"fallback\": \"Required plain-text summary of the attachment.\", \"color\": \"#36a64f\", \"title\": \"Hackathon winner\",\"footer\": \"no one can ever take that from ya!\",\"footer_icon\": \"https://platform.slack-edge.com/img/default_application_icon.png\"}]

(defn -main
  "starts web application"
  [& args]
  (println "starting app")
  (run-jetty handler {:port 3000})
  )
