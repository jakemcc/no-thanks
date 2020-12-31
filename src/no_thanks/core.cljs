(ns no-thanks.core
  (:require [com.degel.re-frame-firebase :as firebase]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [no-thanks.events]
            [no-thanks.subs]
            [no-thanks.firebase]
            [no-thanks.views :as views]
            [no-thanks.config :as config]))

(enable-console-print!)

(defonce firebase-app-info
  {:projectId "no-thanks"
   :apiKey "AIzaSyAkUjSD5ogB2Lw-YofUxSmAANlERRtsg4I"
   :authDomain "no-thanks.firebaseapp.com"
   :databaseURL "https://no-thanks.firebaseio.com"
   :storageBucket "no-thanks.appspot.com"})

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [:initialize-db])
  (firebase/init :firebase-app-info firebase-app-info
                 :get-user-sub           [:user]
                 :set-user-event         [:set-user]
                 :default-error-handler  [:firebase-error])
  (dev-setup)
  (mount-root))
