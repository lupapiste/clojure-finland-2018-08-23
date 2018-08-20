(defproject clojure-finland-2018-08-23 "0.0.0-SNAPSHOT"
  :description "Demonstrate how to extract I/O from commands"
  :url "https://github.com/lupapiste/clojure-finland-2018-08-23"
  :license {:name "Eclipse Public License", :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]

                 ; Components:
                 [integrant "0.6.3"]

                 ; Web:
                 [org.immutant/web "2.1.10"]
                 [metosin/ring-http-response "0.9.0"]
                 [metosin/muuntaja "0.6.0-alpha1"]
                 [metosin/reitit "0.2.0-SNAPSHOT"]

                 ; DB:
                 [evolta/mingler "3.8.0-SNAPSHOT"]

                 ; Schema:
                 [prismatic/schema "1.1.9"]

                 ; UUID:
                 [danlentz/clj-uuid "0.1.7"]

                 ; Logging:
                 [org.clojure/tools.logging "0.4.1"]
                 [org.slf4j/slf4j-api "1.7.25"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]]
  :source-paths ["src"]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[integrant/repl "0.3.1"]
                                  [midje "1.9.2"]
                                  [eftest "0.5.2"]
                                  [metosin/testit "0.4.0-SNAPSHOT"]]}}
  :plugins [[lein-eftest "0.5.2"]])

