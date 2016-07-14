(defproject badger "0.1.0-SNAPSHOT"
  :description "Badger: A Meetup Badge System (BS)"
  :url "http://github.com/meetup/badger"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                [org.clojure/clojure "1.8.0"]
                [ring/ring-core "1.4.0"]
                [ring/ring-jetty-adapter "1.4.0"]
                [quil "2.4.0"]
                ]
  :main ^:skip-aot badger.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  )
