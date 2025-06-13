(ns buzzlabs-phase-2.core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.cors :refer [wrap-cors]]))

(def value (atom 0))

(defn set-value 
  "Sets the value of the atom to the provided number."
  [number]
  (reset! value number))

(defroutes app-routes
  (GET "/" [] (str @value))
  (GET "/:number" [number] (set-value number)))

(def app
  (wrap-cors
   app-routes
   :access-control-allow-origin [#".*"]
   :access-control-allow-methods [:get :post :put :delete :options]))

(defn -main 
  [] 
  (run-jetty
   app
   {:port 3000})
  )
