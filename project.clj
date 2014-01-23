(defproject be.niwi/ring-jetty-adapter "1.0.0"
  :description "Ring Jetty-9.1.x adapter."
  :url "https://github.com/niwibe/ring-jetty-adapter"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[ring/ring-core "1.2.1"]
                 [ring/ring-servlet "1.2.1"]
                 [org.eclipse.jetty/jetty-server "9.1.1.v20140108"]]
  :profiles {:dev {:dependencies [[clj-http "0.7.8"]
                                  [http-kit "2.1.16"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}})
