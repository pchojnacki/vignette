(ns vignette.http.routes
  (:require [cheshire.core :refer :all]
            [clout.core :refer [route-compile route-matches]]
            [compojure.core :refer [context routes GET ANY HEAD]]
            [compojure.route :refer [files]]

            [environ.core :refer [env]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response status charset header]]
            [slingshot.slingshot :refer [try+ throw+]]
            [vignette.http.middleware :refer :all]))

(defn create-routes
  [image-serving-routes]
  (-> (apply routes (concat
                      image-serving-routes
                      (list
                        (GET "/ping" [] "pong")
                        (files "/static/")
                        (bad-request-path))))
      (log-image-request)
      (wrap-params)
      (exception-catcher)
      (multiple-slash->single-slash)
      (request-timer)
      (add-headers)))
