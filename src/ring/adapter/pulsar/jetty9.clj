(ns ring.adapter.pulsar.jetty9
  "Adapter for the Jetty webserver using pulsar
  fibers as concurency primitives."
  (:import (org.eclipse.jetty.server Server Request ServerConnector HttpConfiguration
                                     HttpConnectionFactory SslConnectionFactory ConnectionFactory)
           (org.eclipse.jetty.server.handler AbstractHandler)
           (org.eclipse.jetty.util.thread QueuedThreadPool)
           (org.eclipse.jetty.util.ssl SslContextFactory))
  (:require [ring.util.servlet :as servlet]
            [co.paralleluniverse.pulsar.core :refer [spawn-fiber]]))


(defn- process-request
  [handler brequest req res]
  (let [request-map  (servlet/build-request-map req)
        response-map (handler request-map)]
    (when response-map
      (servlet/update-servlet-response res response-map)
      (.setHandled brequest true))))

(defn- proxy-handler
  "Returns an Jetty Handler implementation for the given Ring handler."
  [handler]
  (proxy [AbstractHandler] []
    (handle [_ ^Request base-request request response]
      (spawn-fiber process-request handler base-request request response))))

(defn- make-ssl-context-factory
  "Creates a new SslContextFactory instance from a map of options."
  [options]
  (let [context (SslContextFactory. "http/1.1")]
    (if (string? (options :keystore))
      (.setKeyStorePath context (options :keystore))
      (.setKeyStore context ^java.security.KeyStore (options :keystore)))
    (.setKeyStorePassword context (options :key-password))
    (when (options :truststore)
      (.setTrustStore context ^java.security.KeyStore (options :truststore)))
    (when (options :trust-password)
      (.setTrustStorePassword context (options :trust-password)))
    (case (options :client-auth)
      :need (.setNeedClientAuth context true)
      :want (.setWantClientAuth context true)
      nil)
    context))

(defn- make-https-configuration
  [options]
  (doto (HttpConfiguration.)
    (.setSecureScheme "https")
    (.setSecurePort (options :ssl-port 443))
    (.setOutputBufferSize 32768)))

(defn- make-thread-pool
  [options]
  (let [pool (QueuedThreadPool. (options :max-threads 50))]
    (when (:daemon? options false)
      (.setDaemon pool true))
    pool))

(defn- make-server
  [options]
  (Server. (make-thread-pool options)))

(defn- make-http-connector
  [server options]
  (doto (ServerConnector. server)
    (.setPort (options :port 80))
    (.setHost (options :host))
    (.setIdleTimeout (options :max-idle-time 200000))))

(defn- make-https-connector
  [server options]
  (let [sslctx-factory  (make-ssl-context-factory options)
        confactory      (HttpConnectionFactory. (make-https-configuration options))
        confactories    (into-array ConnectionFactory [confactory])]
    (doto (ServerConnector. server sslctx-factory confactories)
      (.setPort (options :ssl-port 443))
      (.setIdleTimeout (options :max-idle-time 200000)))))

(defn ^Server run-jetty
  "Start a Jetty webserver to serve the given handler according to the
  supplied options:

  :configurator - a function called with the Jetty Server instance
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
  :max-idle-time  - the maximum idle time in milliseconds for a connection (default 200000)
  :client-auth  - SSL client certificate authenticate, may be set to :need,
                  :want or :none (defaults to :none)"
  [handler options]
  (let [server (make-server options)]
    (.addConnector server (make-http-connector server options))
    (when (or (options :ssl?) (options :ssl-port))
      (.addConnector server (make-https-connector server options)))
    (doto server
      (.setHandler (proxy-handler handler))
      (.start))
    (when (:join? options true)
      (.join server))
    server))
