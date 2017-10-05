(ns no-thanks.events
  (:require [re-frame.core :as rf]
            [no-thanks.db :as db]
            [no-thanks.game :as game]))

(rf/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-db
 :set-number-players
 (fn [db [_ number-players]]
   (assoc db
          :game (game/initialize (js/parseInt number-players))
          :view :game)))

(rf/reg-event-db
 :take-card
 (fn [db [_]]
   (update db :game game/turn :take-card)))

(rf/reg-event-db
 :no-thanks!
 (fn [db [_]]
   (update db :game game/turn :no-thanks!)))
