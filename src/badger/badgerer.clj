(ns badger.badgerer
  "hands out the badge onto the profile pic"
  (:require [quil.core :as q]))

(defn add-badge
  "receives profile pic filename and badge number, and saves the badged pic and return the filename"
  [profile-filename badge-num]
  (let [result-filename (clojure.string/replace profile-filename #"\." (str "_" badge-num "."))
        profile-size 600 badge-size 200]
    (q/defsketch badged-profile
      :size [profile-size profile-size]
      :setup (fn []
               (let [profile-pic (q/load-image profile-filename)
                     badge-pic (q/load-image (str "badge_" badge-num ".png"))
                     badge-pos (- profile-size badge-size)]
                 (q/image profile-pic 0 0 profile-size profile-size)
                 (q/image badge-pic badge-pos badge-pos badge-size badge-size)
                 (q/save result-filename)))
      :title "saving to file"
      :draw (fn [] nil))
    (.exit badged-profile)
    result-filename))
