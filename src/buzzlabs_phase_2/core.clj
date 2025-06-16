(ns buzzlabs-phase-2.core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.cors :refer [wrap-cors]]
            [datomic.client.api :as d]
            ))

(def client (d/client {:server-type :datomic-local
                       :system "datomic-samples"}))
(def db-name "counter-db")

(def conn (d/connect client {:db-name db-name}))

(defn transact-incremented-value
  "transacts the number into datomic database.
   the number is expected to be an integer."
  [number]
  (println "Transacting value:" number)
  (d/transact conn {:tx-data [{:counter/id :counter
                               :counter/value number}]})
  )

(defn get-counter-value
  "get the current value from the counter database.
   returns the value as a string."
  []
  (let [db (d/db conn)
        current-value (ffirst (d/q '[:find ?value
                                     :where
                                     [?e :counter/value ?value]
                                     [?e :counter/id :counter]] db))]
    
    (println "Current value from datomic:" current-value)
    (str current-value)
    ))

(defroutes app-routes
  (GET "/" [] (get-counter-value))
  (GET "/:number" [number] (transact-incremented-value (Integer/parseInt number))))

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
