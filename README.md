# ring-jetty-adapter

A Ring adapter that uses the Jetty webserver.

This is completely based on jetty adapter from https://github.com/ring-clojure/ring but
uses the latest stable jetty version: 9.1.1.

## How to use?

```clojure
(ns myns.core
  (:require [ring.adapter.jetty9 :as jetty])
  (:gen-class))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(defn- main
  [& args]
  (jetty/run-jetty #'app {:port 8080}))
```

**Options:**

```clojure
:port         - the port to listen on (defaults to 80)
:host         - the hostname to listen on
:join?        - blocks the thread until server ends (defaults to true)
:daemon?      - use daemon threads (defaults to false)
:ssl?         - allow connections over HTTPS
:ssl-port     - the SSL port to listen on (defaults to 443, implies :ssl?)
:keystore     - the keystore to use for SSL connections
:key-password - the password to the keystore
:truststore   - a truststore to use for SSL connections
:trust-password - the password to the truststore
:max-idle-time  - the maximum idle time in milliseconds
                  for a connection (default 200000)
:client-auth  - SSL client certificate authenticate, may be
                set to :need, :want or :none (defaults to :none)"
```


## License

Copyright © 2014 Andrey Antukh and released under an MIT license

Copyright © 2009-2013 Mark McGranaghan and released under an MIT license.
