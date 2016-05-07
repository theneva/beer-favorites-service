(ns favorites-service.handler
  (:require
    [clojure.string :refer [join]]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.util.response :refer [response status]]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
    [ring.middleware.cors :refer [wrap-cors]]
    [clj-http.client :as http-client]))

(def beer-service-uri (or (System/getenv "BEER_SERVICE_URI")
                          "http://localhost:8080"))

(def beers-uri (str beer-service-uri "/beers"))

(def favorites
  (atom [{:username "theneva"
          :beers    [1, 2]}
         {:username "reservedeler"
          :beers    [2, 4]}]))

(defn hello-world []
  (response {:message "Hello, world!"}))

(defn get-favorites [] @favorites)

(defn get-favorites-by-username [username]
  (let [user-favorite-ids (:beers (first (filter #(= (:username %) username) @favorites)))]
    (:body (http-client/get beers-uri {:as :json :query-params {"id" (join "," user-favorite-ids)}}))))

(defroutes app-routes
           (GET "/" [] (hello-world))
           (GET "/favorites" [] (response (get-favorites)))
           (GET "/favorites/:username" [username]
             (response (get-favorites-by-username username)))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin "*" :access-control-allow-methods "*")
      (wrap-json-response)
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-defaults (assoc site-defaults :security false))))
