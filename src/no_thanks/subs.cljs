(ns no-thanks.subs
  (:require [re-frame.core :as re-frame]
            [no-thanks.game :as game])
  (:require-macros [reagent.ratom :refer [reaction]]))

(re-frame/reg-sub
 :db
 (fn [db]
   db))

(re-frame/reg-sub
 :view
 (fn [db]
   (:view db)))

(re-frame/reg-sub
 :top-card
 (fn [db]
   (first (get-in db [:game :draw-pile]))))

(re-frame/reg-sub
 :current-player
 (fn [db]
   (get-in db [:game :current-player])))

(re-frame/reg-sub
 :token-pot
 (fn [db]
   (get-in db [:game :token-pot] 0)))

(re-frame/reg-sub
 :players
 (fn [db]
   (get-in db [:game :players])))

(re-frame/reg-sub
 :game-over
 (fn [db]
   (game/game-over? (:game db))))
