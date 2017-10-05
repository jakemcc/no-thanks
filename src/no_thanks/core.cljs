(ns no-thanks.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [no-thanks.events]
            [no-thanks.subs]
            [no-thanks.views :as views]
            [no-thanks.config :as config]))

(enable-console-print!)

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
