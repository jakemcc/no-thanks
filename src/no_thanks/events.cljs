(ns no-thanks.events
  (:require [com.degel.re-frame-firebase :as firebase]
            [re-frame.core :as rf]
            [no-thanks.db :as db]
            [no-thanks.game :as game]))

(rf/reg-event-fx
 :sign-in
 (fn [_ _] {:firebase/google-sign-in {:sign-in-method :popup}}))

(rf/reg-event-fx
 :sign-out
 (fn [_ _] {:firebase/sign-out nil}))

(rf/reg-event-db
 :set-user
 (fn [db [_ user]]
   (assoc db :user user)))

(rf/reg-event-db
 :firebase-error
 (fn [db [_ error]]
   (assoc db :firebase-error (pr-str error))))

;; ^^^ re-frame-firebase stuff


(rf/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-fx
 :create-game
 (fn [{:keys [db]} [_]]
   (let [code (apply str (repeatedly 4 #(rand-nth ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "N" "O" "P" "Q" "R" "S" "T" "U" "V" "W" "X" "Y" "Z"])))]
     {:db (-> db
              (assoc :game-code code :view :pregame))
      :firebase/write {:path [(keyword code)]
                       :value {:players [{:name (get-in db [:user :email])}]}
                       :on-success #(js/console.log "wrote success")
                       :on-failure [:firebase-error]}})))

(rf/reg-event-fx
 :join-game
 (fn [{:keys [db]} [_ code]]
   {:db (assoc db
               :game-code code
               :view :pregame)
    :firebase/swap! {:path [(keyword code) :players]
                     :function (fn [players]
                                 (let [user-id (get-in db [:user :email])]
                                   (if (some (comp (partial = user-id) :name) players)
                                     players
                                     (conj (vec players) {:name user-id}))))
                     :on-success #(println "join game success")
                     :on-failure [:firebase-error]}}))

(defn game-event! [event f & args]
  (let [enabled? (atom true)]
    (rf/reg-event-fx
     event
     (fn [{:keys [db]} _]
       (js/setTimeout (fn [_] (compare-and-set! enabled? false true)) 300)
       (if (compare-and-set! enabled? true false)
         {:firebase/swap! {:path [(keyword (:game-code db))]
                           :function #(apply f % args)
                           :on-success #(println (str event " success"))
                           :on-failure [:firebase-error]}}
         {})))))

(game-event! :start-over game/reset-and-keep-players)

(game-event! :start-game game/new-round)

(game-event! :play-another-round game/new-round)

(game-event! :take-card game/turn :take-card)

(game-event! :no-thanks! game/turn :no-thanks!)


