(ns no-thanks.views
  (:require [cljs.pprint :as pprint]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [no-thanks.game :as game]
            [no-thanks.config :as config]))

(defn listen [query]
  @(rf/subscribe [query]))

(defn game []
  (condp = (listen :game-state)
    :over [:div "GAME OVER!"
           (doall
            (for [[idx player] (map-indexed vector (listen :players))
                  :let [rounds-score (game/score-player player)]]
              [:div {:key idx}
               (str "player " idx "'s round score: " rounds-score " total score: " (+ rounds-score (:total-score player)))]))
           [:button {:on-click #(rf/dispatch [:play-another-round])} "Play another round"]
           [:button {:on-click #(rf/dispatch [:start-over])} "Start over"]]
    :playing [:div
              [:div
               "Top card: " (listen :top-card)
               [:br]
               "Tokens on card: " (listen :token-pot)]
              (let [current-user (listen :user)]
                (doall (for [[idx player] (map-indexed vector (listen :players))
                             :let [is-current-user? (= (:name player) (:email current-user))]]
                         [:div {:key idx
                                :class "player"}
                          [:div (str "----- " (:name player) " ------")]
                          [:div "Cards: " (string/join ", " (sort (:cards player)))]
                          (when is-current-user?
                            [:div "Tokens: " (:tokens player)])
                          (when (and (= idx (listen :current-player))
                                     is-current-user?)
                            [:div
                             [:button {:class "action-button" :on-click #(rf/dispatch [:take-card])}
                              [:span "Take card"]]
                             [:button {:class "action-button" :on-click #(rf/dispatch [:no-thanks!]) :disabled (zero? (:tokens player))}
                              [:span "No thanks!"]]])])))]
    :not-started (let [players (listen :players)]
                   [:div
                    [:div "---- Players ----"
                     [:div
                      (doall (for [[idx player] (map-indexed vector players)]
                               [:div {:key idx}
                                (:name player)]))]]
                    [:button
                     {:on-click #(rf/dispatch [:start-game])}
                     "Start Game"]])))

(defn no-game []
  [:div
   [:form {:on-submit (fn [e]
                        (.preventDefault e)
                        (println (.. e -target -elements -game -value))
                        (rf/dispatch [:join-game (string/upper-case (.. e -target -elements -game -value))]))}
    [:input {:style {:text-transform :uppercase}
             :type "text"
             :name "game"
             :placeholder "Enter Game Code"
             :required true}]
    [:button {:type "submit"} "Join Game"]]
   [:br]
   [:button {:on-click #(rf/dispatch [:create-game])} "Create new game"]])

(defn header []
  [:div {:class "header"}
   (when-let [code (listen :game-code)]
     [:span {:style {:padding-right 10}} "Game code: " code])
   (if-let [user (listen :user)]
     [:span (:email user) [:button {:class "button"
                                    :on-click #(rf/dispatch [:sign-out])}
                           "Sign Out"]]
     [:button {:on-click #(rf/dispatch [:sign-in])
               :class "button"}
      "Sign in"])])

(defn not-signed-in []
  [:div
   [:p "Welcome to No Thanks!"]
   [:p "Sign in with a Google account by clicking above to join the game."]
   [:p "If you click Sign In and nothing happens, check your pop-up blocker!"]
   [:p "Read the "
    [:a {:href "https://boardgamegeek.com/boardgame/12942/no-thanks"} "description"] " for the rules."]])

(defn main-panel []
  [:div
   [header]
   [:div {:class "board"}
    (if (= :not-signed-in (listen :game-state))
      [not-signed-in]
      (let [view (listen :view)]
        (if (= :no-game view)
          [no-game]
          [game])))]
   #_   (when config/debug?
          [:pre {:class "database"}
           (with-out-str (pprint/pprint @(rf/subscribe [:db])))])])
