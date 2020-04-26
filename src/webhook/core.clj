(ns webhook.core 
  (:require 
    [org.httpkit.server :as server]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [clojure.string :as str]
    [clojure.data.json :as json]
    [ring.middleware.defaults :refer :all]
  )
  (:gen-class)
)

(defn default-res-headers
  []
  { 
    "content-type" "application/json"
    "when" (str (java.util.Date.))
  }
)

(defn gh-release
  [body]
  (def commit (get body "after"))
  (def ref (get body "ref"))
  (def sender (get (get body "sender") "login"))
  
  (def responseBody (json/write-str (str "{\"function\": \"gh-release\", \"sender\": \"" sender "\", \"ref\": \"" ref "\" \"commit\": \"" commit "\"}")))
  (println responseBody)
  {
    :status 200
    :body responseBody
    :headers (default-res-headers)
  }
)

(defn gh-master
  [body]
  (def commit (get body "after"))
  (def sender (get (get body "sender") "login"))

  (def responseBody (json/write-str (str "{\"function\": \"gh-master\", \"sender\": \"" sender "\", \"commit\": \"" commit "\"}")))
  (println responseBody)
  {
    :status 200
    :body responseBody
    :headers (default-res-headers)
  }
)

(defn gh-pull-request
  [body]
  (def commit (get (get (get body "pull_request") "head") "sha"))
  (def prnumber (get body "number"))
  (def sender (get (get body "sender") "login"))
  (def action (get body "action"))

  (let [responseBody
        (json/write-str (str 
                          "{"
                            "\"function\": \"gh-pull-request\","
                            "\"sender\": \"" sender "\","
                            "\"action\": \"" action "\","
                            "\"prnumber\": \"" prnumber "\","
                            "\"commit\": \"" commit "\""
                          "}"
                        )
        )] 
        (println responseBody)
        {
          :status 200
          :headers (default-res-headers)
          :body responseBody
        }
  )
)

(defn webhook
  [req]
  (def body (json/read-str (slurp (:body req))))

  (def ref (get body "ref"))
  (def pull_request (get body "pull_request"))

  (cond
    (and ref (re-matches #"^refs/tags/v\d{1,2}.\d{1,2}.\d{1,2}$" ref)) (gh-release body)
    (and ref (re-matches #"^refs/heads/master$" ref)) (gh-master body)
    (some? pull_request) (gh-pull-request body)
    :else 
      {
        :status 204
        :headers (default-res-headers)
      }
  )
)

(defroutes app-routes
  (GET "/" [] {:status 200 :body "you better post" :headers (default-res-headers)})
  (POST "/webhook" [] webhook)
  (route/not-found "Error, page not found!"))

(def app
  (wrap-defaults app-routes api-defaults)
)

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server app {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))
  )
)