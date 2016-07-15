(ns badger.badgerer
  "hands out the badge onto the profile pic"
  (:require [quil.core :as q]))

(defn prefix-path [filename] (str "resources/public/" filename))

(def profile-size 600)

(def badge-size 200)

(def orig-file (ref ""))

(def badg-file (ref ""))

(def dest-file (ref ""))

(def should-write (ref false))

(q/defsketch badged-profile
 :size [profile-size profile-size]
 :setup (fn []
          (q/frame-rate 10))
 :title "saving to file"
 :draw (fn []
         (when (deref should-write)
           (dosync
             (ref-set should-write false)
             (let [profile-pic (q/load-image (deref orig-file))
                   badge-pic (q/load-image (deref badg-file))
                   result-filename (deref dest-file)
                   badge-pos (- profile-size badge-size)]
               (q/image profile-pic 0 0 profile-size profile-size)
               (q/image badge-pic badge-pos badge-pos badge-size badge-size)
               (q/save result-filename))))))

(defn add-badge
  "receives profile pic filename and badge number, and saves the badged pic and return the filename"
  [profile-filename badge-num]
  (let [result-filename (clojure.string/replace profile-filename #"\." (str "_" badge-num "."))]
    (dosync
      (ref-set orig-file (prefix-path profile-filename))
      (ref-set badg-file (prefix-path (str "badge_" badge-num ".png")))
      (ref-set dest-file (prefix-path result-filename))
      (ref-set should-write true))
    (Thread/sleep 1000)
    (result-filename)))
