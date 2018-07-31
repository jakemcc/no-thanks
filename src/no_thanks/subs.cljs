(ns no-thanks.subs
  (:require [re-frame.core :as rf])
  (:require-macros [reagent.ratom :refer [reaction]]))

(rf/reg-sub
 :db
 (fn [db]
   db))

(rf/reg-sub
 :view
 (fn [db _]
   (:view db)))

(rf/reg-sub
 :user
 (fn [db _] (:user db)))

(rf/reg-sub
 :logged-in?
 (fn [_ _]
   (rf/subscribe [:user]))
 (fn [user _]
   (boolean user)))

(rf/reg-sub
 :game-code
 (fn [db]
   (:game-code db)))

(rf/reg-sub-raw
 :game
 (fn [app-db _]
   (let [previous-player (atom nil)]
     (reaction
      (let [game-code @(rf/subscribe [:game-code])]
        (if game-code
          (let [game @(rf/subscribe [:firebase/on-value {:path [game-code]}])
                player (get-in game [:players (:current-player game) :name])]
            (when (and (not= player @previous-player)
                       (= player (get-in @app-db [:user :email])))
              (rf/dispatch [:start-current-players-turn]))
            (reset! previous-player player)
            game)
          nil))))))

(rf/reg-sub
 :top-card
 (fn [_ _]
   (rf/subscribe [:game]))
 (fn [game _]
   (first (:draw-pile game))))

(rf/reg-sub
 :current-player
 (fn [_ _]
   (rf/subscribe [:game]))
 (fn [game _]
   (:current-player game)))

(rf/reg-sub
 :token-pot
 (fn [_ _]
   (rf/subscribe [:game]))
 (fn [game _]
   (:token-pot game 0)))


(rf/reg-sub
 :players
 (fn [_ _]
    (rf/subscribe [:game]))
 (fn [game _]
   (:players game)))

(rf/reg-sub
 :game-state
 (fn [_ _]
   [(rf/subscribe [:game])
    (rf/subscribe [:user])])
 (fn [[game user] _]
   (if (nil? user)
     :not-signed-in
     (condp = (:game-over? game)
       true :over
       false :playing
       nil :not-started))))

