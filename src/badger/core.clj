(ns badger.core
  (:use ring.middleware.params
        ring.util.response
        ring.adapter.jetty
        clojure.string)
  (:gen-class))

(declare cmd-default http200)


(def badge-cc { :title "Coverage Cueen"
                :desc "covered so much code, the code could sleep well"
                :ico-url "http://www.gummyworm.net/wp-content/uploads/2015/02/Pinguino-png-129x129.png"
                })

(def badge-hd { :title "Hackday Winner"
                :desc "no one can ever take that from ya!"
                :ico-url "https://platform.slack-edge.com/img/default_application_icon.png"
                })

(defn attachJson [title footer icon-url]
  (let [  template (str   "{\"fallback\": \"%s\","
            "\"color\": \"#36a64f\", "
            "\"title\": \"%s\","
            "\"footer\": \"%s\","
            "\"footer_icon\": \"%s\"}")
          ]
          (format template title title footer icon-url)
  ))

(def listJson
  (let [  template (str
    "{\"response_type\": \"in_channel\","
    "\"text\": \"Here is the list of possible badges\","
    "\"attachments\": [%s, %s]"
    "}")
    ]
    (format template
      (attachJson (:title badge-cc) (:desc badge-cc) (:ico-url badge-cc))
      (attachJson (:title badge-hd) (:desc badge-hd) (:ico-url badge-hd))
    )
  ))

(def topJson
  (let [  template (str
    "{\"response_type\": \"in_channel\","
    "\"text\": \"Here are the top Badgers!\","
    "\"attachments\": [%s, %s]"
    "}")
    ]
    (format template
      (attachJson (:title badge-cc) (:desc badge-cc) (:ico-url badge-cc))
      (attachJson (:title badge-hd) (:desc badge-hd) (:ico-url badge-hd))
    )
  ))
;
; Routing and such follows
;

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


(defn cmd-list [] (http200 listJson))

(defn cmd-top [] (http200 topJson))

(defn http200 [body]
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body (str body)
  })

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
    "leaderboard" (cmd-top)
    "award" (cmd-award text)
    "response-url" (http200 (str "response-url: " response_url "\n"))
    (cmd-default)
    )))

(def app
  (-> handler wrap-params)
  )

(defn -main
  "starts web application"
  [& args]
  (println "starting app")
  (run-jetty app {:port 3000})
  )
