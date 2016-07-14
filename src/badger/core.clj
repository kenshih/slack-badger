(ns badger.core
  (:use ring.middleware.params
        ring.util.response
        ring.adapter.jetty
        clojure.string)
  (:gen-class))

(declare cmd-default)


(def hard-response
  (str "{\"response_type\": \"in_channel\",\"text\": \"Here are the top Badgers!\" "
  ",\"attachments\": [{\"fallback\": \"Coverage Cueen\", \"color\": \"#36a64f\", \"title\": \"Coverage Cueen\",\"footer\": \"covered so much code, the code could sleep well\",\"footer_icon\": \"http://www.gummyworm.net/wp-content/uploads/2015/02/Pinguino-png-129x129.png\"},{\"fallback\": \"Required plain-text summary of the attachment.\", \"color\": \"#36a64f\", \"title\": \"Hackathon winner\",\"footer\": \"no one can ever take that from ya!\",\"footer_icon\": \"https://platform.slack-edge.com/img/default_application_icon.png\"}]"
  "}"
  ))

(def default-response
  (str "{\"response_type\": \"in_channel\",\"text\": \"(default unconfigured response)\" }\n"
  ))

(comment
  "token=gIkuvaNzQIHg97ATvDxqgjtO
  team_id=T0001
  team_domain=example
  channel_id=C2147483705
  channel_name=test
  user_id=U2147483697
  user_name=Steve
  command=/weather
  text=94070
  response_url=https://hooks.slack.com/commands/1234/5678"
  )

(defn cmd-list [] (cmd-default))

(defn cmd-top [r]
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body (str "response_url: " r "\n")
  }
  )

(defn cmd-award [cmd]
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body (str "here is what the cmd looks like " cmd "\n")
  })

(defn cmd-default []
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body default-response
  }
  )

(defn handler [{params :params}]
  (let [
    channel_id (params "channel_id")
    channel_name (params "channel_name")
    user_id (params "user_id")
    user_name (params "user_name")
    command (params "command")
    text (params "text" "defaut")
    response_url (params "response_url")
    first-text ((split text #" ") 0)
    ]
  (case first-text
    "list" (cmd-list)
    "top" (cmd-top response_url)
    "award" (cmd-award text)
    (cmd-default)
    )))

; \"attachments\": [{\"fallback\": \"Coverage Cueen\", \"color\": \"#36a64f\", \"title\": \"Coverage Cueen\",\"footer\": \"covered so much code, the code could sleep well\",\"footer_icon\": \"http://www.gummyworm.net/wp-content/uploads/2015/02/Pinguino-png-129x129.png\"},{\"fallback\": \"Required plain-text summary of the attachment.\", \"color\": \"#36a64f\", \"title\": \"Hackathon winner\",\"footer\": \"no one can ever take that from ya!\",\"footer_icon\": \"https://platform.slack-edge.com/img/default_application_icon.png\"}]

(def app
  (-> handler wrap-params)
  )

(defn -main
  "starts web application"
  [& args]
  (println "starting app")
  (run-jetty app {:port 3000})
  )
