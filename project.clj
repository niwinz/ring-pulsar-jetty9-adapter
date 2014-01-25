(defproject be.niwi/ring-jetty-adapter "1.0.0"
  :description "Ring Jetty-9.1.x adapter."
  :url "https://github.com/niwibe/ring-jetty-adapter"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-servlet "1.2.1"]
                 [co.paralleluniverse/pulsar "0.4.0"]
                 [org.eclipse.jetty/jetty-server "9.1.1.v20140108"]]
  :java-agents [[co.paralleluniverse/quasar-core "0.4.0"]]
  :profiles {:dev {:dependencies [[clj-http "0.7.8"]]}})
