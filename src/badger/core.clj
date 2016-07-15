(ns badger.core
  (:use ring.middleware.params
        ring.middleware.resource
        ring.middleware.content-type
        ring.util.response
        ring.adapter.jetty
        clojure.string
        badger.badgerer
        )
  (:gen-class))
(require '[org.httpkit.client :as http])
(require '[clojure.data.json :as json])

(declare cmd-default http200 post-one)

(def badge-cc { :title "Coverage Cueen"
                :desc "covered so much code, the code could sleep well"
                :ico-url "http://www.gummyworm.net/wp-content/uploads/2015/02/Pinguino-png-129x129.png"
                })

(def badge-hd { :title "Hackday Winner"
                :desc "no one can ever take that from ya!"
                :ico-url "https://platform.slack-edge.com/img/default_application_icon.png"
                })

; lookup of badges by key
(def badges {"cueen" badge-cc "hack" badge-hd})

; users and badges
(def users {
  "kristen" [badge-cc badge-hd]
  "ken" [badge-cc]
  "bryanvelzy" [badge-cc]
  "sak" [badge-hd]
  })

(def top-users ["kristen" "sak" "bryanvelzy" "ken"])

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

(defn channel-name [channel] (str "#" channel))

(defn cmd-top [channel]
  (doseq [u top-users]
    (post-one (channel-name channel) (format "%s has %s awards!" u (count (users u))))
    (doseq [b (users u)]
      (post-one (channel-name channel)
        (format "%s has earned %s (%s)" u (:title b) (:ico-url b))
        )))
  {:status 200})


(defn post-one [channel text]
  (http/post "https://hooks.slack.com/services/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"
   {:form-params {:payload (json/write-str {:channel channel "text" text})}})
   )

(defn http200 [body]
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body (str body)
  })

(defn cmd-award [channel text]
  (let [
        terms (split text #" ")
        should-be-3 (count terms)
        channel-name (str "#" channel)
        ]
    (if (= 3 should-be-3)
      (let [name (terms 1) badge (terms 2)]
        (post-one channel-name (format "@%s receives the badge '%s'" name (:title (badges badge)))))
      (post-one channel-name "you need to enter a recipient and an award for this to work")
    )
    {:status 200
      ;:headers {"Content-Type" "application/json"}
      ;:body (str "here is what the cmd looks like " text "\n")
    }))

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
    "leaderboard" (cmd-top channel_name)
    "award" (cmd-award channel_name text)
    "response-url" (http200 (str "response-url: " response_url "\n"))
    "create-badge" (http200 (str "http://localhost:3000/" (add-badge "CuppWat.jpg" 1)))
    (cmd-default)
    )))

(defn router [request]
  (let [ uri (:uri request)
      ]
      (handler request)
    ))

'(defn app [request]
  (router request))
'(def app
  (-> handler wrap-params wrap-spy)
  )
(def app
    (-> router (wrap-resource "public") (wrap-content-type) wrap-params))

(defn -main
  "starts web application"
  [& args]
  (println "starting app")
  (run-jetty app {:port 3000})
  )
