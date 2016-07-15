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
(import '[org.apache.commons.lang3 RandomStringUtils])

(declare cmd-default http200 post-one bust summary-badge-numbers mk-image-url)

(defn msg-leader-entry [person-name badge-title url] (format "%s is a %s\n%s" person-name badge-title (bust url)))
(defn msg-leader-entry-title [person-name num] (format "%s has %s awards!" person-name num))
(defn msg-award [person-name badge-name] (format "@%s receives the badge '%s'" person-name badge-name))
(defn msg-award-err [] "you need to enter a recipient and an award for this to work")

; mapping to badge #
(def summary-badge-numbers {
  "star" 0
  "crown" 1
  "cupp" 13
  "codekiller" 3
  "grace" 4
  "mentor" 5
  "email" 6
  "onboarded" 7
  "anniversary" 8
  "crawl" 9
  "catchall" 2
  "hack" 10
  })
; "cueen" badge-cc
; "codekiller" badge-codekiller
; "grace" badge-grace
; "mentor" badge-mentor
; "email" badge-email
; "onboarded" badge-onboarded
; "anniversary" badge-anniversary
; "crawl" badge-crawl
; "catchall" badge-catchall
; "hack" badge-hd

(def badge-cc { :title "Coverage Cueen"
                :desc "covered so much code, the code could sleep well"
                :ico-url "badge_crown.png"
                })
(def badge-codekiller { :title "Code Killer"
                :desc "Cleaned up the most code this year. unused code sitting around not only causes clutter, but makes the codebase very error prone"
                :ico-url "badge-codekiller.png"
                })
(def badge-grace { :title "Amazing Grace"
                :desc "For extraordinary contributions in testing"
                :ico-url "badge_grace.png"
              })
(def badge-mentor { :title "MEME Mentor"
                :desc "Taught a class on testing"
                :ico-url "badge_mentor.png"
                })
(def badge-email { :title "Email Destroyer"
                :desc "Answered 100 support emails in a single day. Able to leap tall buildings in a single bound."
                :ico-url "badge_email.png"
                })
; (def badge- { :title "FAFF Attendee"
;                 :desc "RSVP and attend a FAFF"
;                 :ico-url "badge_.png"
;                 })
(def badge-onboarded { :title "On your way!"
                :desc "Complete your on-boarding training"
                :ico-url "badge_onboarded.png"
                })
(def badge-anniversary { :title "Happy Anniversary"
                :desc "Receive this badge on the day of your work anniversary"
                :ico-url "badge_anniversary.png"
                })
(def badge-crawl { :title "Meetup Crawler"
                :desc "Went on a Meetup Crawl"
                :ico-url "badge_crawl.png"
                })
; (def badge- { :title "Propper"
;                 :desc "Received/Given a Prop at Mupdate"
;                 :ico-url "badge_.png"
;                 })
(def badge-catchall { :title "Gotta Catch Em All"
                :desc "Gain at least 5 badges"
                :ico-url "badge_catchemall.png"
                })
(def badge-hd { :title "Hackday Winner"
                :desc "no one can ever take that from ya!"
                :ico-url "badge_meetupswarm.png"
                })

; lookup of badges by key
(def badges {"cueen" badge-cc
  "codekiller" badge-codekiller
  "grace" badge-grace
  "mentor" badge-mentor
  "email" badge-email
  "onboarded" badge-onboarded
  "anniversary" badge-anniversary
  "crawl" badge-crawl
  "catchall" badge-catchall
  "hack" badge-hd
  })

; users and badges
(def users {
  "kristen" [badge-cc badge-mentor badge-hd]
  "ken" [badge-cc]
  "bryanvelzy" [badge-cc badge-mentor]
  "sak" [badge-mentor badge-hd]
  })

; users and badges
(def profile-img-by-user {
  "kristen" "kristen.png"
  "ken" "ken.png"
  "bryanvelzy" "bryanvelzy.png"
  "sak" "sak.png"
  })

; hard-coded fake ordering of leaderboard!
(def top-users ["kristen" "sak" "bryanvelzy" "ken"])



(defn attachJson [title footer icon-url server-name]
  (let [  template (str   "{\"fallback\": \"%s\","
            "\"color\": \"#36a64f\", "
            "\"title\": \"%s\","
            "\"footer\": \"%s\","
            "\"footer_icon\": \"%s\"}")
          ]
          (format template title title footer (mk-image-url icon-url server-name))
  ))

; dtod dyn
(defn listJson[server-name]
  (defn mk-tmpl-with-more [m]
    (str
      "{\"response_type\": \"in_channel\","
      "\"text\": \"Here is the list of possible badges\","
      "\"attachments\": [" m "]"
      "}"))
    (mk-tmpl-with-more
     (join ", " (map #(attachJson (:title %) (:desc %) (:ico-url %) "lala") (vals badges)))
     )
  )

;
; Routing and such follows
;
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


(defn cmd-list [server-name] (http200 (listJson server-name)))

(defn channel-name [channel] (str "#" channel))

(defn cmd-top [channel server-name]
  (doseq [u top-users]
    (Thread/sleep 200)
    (post-one (channel-name channel) (msg-leader-entry-title u (count (users u))))
    (doseq [b (users u)]
      (Thread/sleep 200) ;hack to get ordering in ui right
      (post-one (channel-name channel)
        (msg-leader-entry u (:title b) (mk-image-url (:ico-url b) server-name))
        )))
  {:status 200})


(defn post-one [channel text]
  (http/post "https://hooks.slack.com/services/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"
   {:form-params {:payload (json/write-str {:channel channel "text" text})}})
   )

; http utils
(defn http200 [body]
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body (str body)
  })
; cache buster
(defn bust [url]
  (str url "?" (RandomStringUtils/randomAlphanumeric 2)))

; format imge url
(defn mk-image-url [file-name server-name]
  (format "http://%s:3000/%s" server-name file-name)
  )
;
; cmd(s) are expected to
; return a ring "response" object
;
(defn cmd-award [channel text]
  (let [
        terms (split text #" ")
        should-be-3 (count terms)
        channel-name (str "#" channel)
        ]
    (if (= 3 should-be-3)
      (let [name (terms 1) badge (terms 2)]
        (post-one channel-name (msg-award name (:title (badges badge)))))
      (post-one channel-name (msg-award-err)))
    {:status 200})) ; suppress feedback in favor of dynamic feedback

(defn cmd-default []
  {:status 200
    :headers {"Content-Type" "application/json"}
    :body default-response
  }
  )

; profile image creation
(defn cmd-create-profile [text server-name]
  (defn msg [base-url user-name summary-badge]
    (http200 (str base-url (add-badge (profile-img-by-user user-name) (summary-badge-numbers summary-badge))))
    )
    (let [
      terms (split text #" ")
      u (terms 1)
      badge (terms 2)
      ]
      (msg (format "http://%s:3000/" server-name) u badge))
    )

(defn handler [request]
  (let [
    params (:params request)
    server-name (:server-name request)
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
    "list" (cmd-list server-name)
    "leaderboard" (cmd-top channel_name server-name)
    "award" (cmd-award channel_name text)
    "response-url" (http200 (str "response-url: " response_url "\n"))
    "create-profile" (cmd-create-profile text server-name)
    (cmd-default)
    )))

(defn router [request]
  (let [ uri (:uri request)
      ]
      (handler request)
    ))

(def app
    (-> router (wrap-resource "public") (wrap-content-type) wrap-params))

(defn -main
  "starts web application"
  [& args]
  (println "starting app")
  (run-jetty app {:port 3000})
  )
