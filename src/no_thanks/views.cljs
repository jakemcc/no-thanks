(ns no-thanks.views
  (:require [cljs.pprint :as pprint]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [no-thanks.game :as game]
            [no-thanks.config :as config]))

(defn listen [query]
  @(rf/subscribe [query]))

(defn game-middle []
  [:div
   "Top card: " (listen :top-card)
   [:br]
   "Tokens on card: " (listen :token-pot)])

(defn playing-players []
  (let [user (listen :user)
        players (listen :players)
        indexed-players (->> (map-indexed vector players)
                             cycle
                             (drop-while (fn [[_ player]] (not= (:name player) (:email user))))
                             (take (count players)))]
    [:div
     (doall (for [[idx player] indexed-players
                  :let [is-viewing-user? (= (:name player) (:email user))
                        players-turn? (= idx (listen :current-player))]]
              [:div {:key idx
                     :class (cond-> "player"
                              players-turn? (str " playing"))}
               [:h2 {:class "player-title"} (:name player)]
               [:div "Cards: " (string/join ", " (sort (:cards player)))]
               (when is-viewing-user?
                 [:div "Tokens: " (:tokens player)])
               (when (and is-viewing-user? players-turn?)
                 [:div
                  [:button {:class "action-button"
                            :style {:width "35%"}
                            :on-click #(rf/dispatch [:take-card])}
                   [:span "Take card"]]
                  [:span {:style {:display "inline-block"
                                  :width "10%"}}]
                  [:button {:class "action-button"
                            :style {:width "35%"}
                            :on-click #(rf/dispatch [:no-thanks!]) :disabled (zero? (:tokens player))}
                   [:span "No thanks!"]]])]))]))

(defn winner-box [label player-name score]
  [:div {:class "winner"}
   [:span {:class "winner-label"} label]
   (str player-name " with " score)])

(defn game []
  (condp = (listen :game-state)
    :over [:div
           [:h2 "GAME OVER!"]
           (let [players (listen :players)
                 players (mapv (fn [player] (let [round-score (game/score-player player)]
                                              (assoc player
                                                     :round-score round-score
                                                     :total-score (+ round-score (:total-score player)))))
                               players)
                 round-winner (first (sort-by :round-score players))
                 overall-leader (first (sort-by :total-score players))]
             [:div
              [winner-box
               "ROUND WINNER"
               (:name round-winner)
               (:round-score round-winner)]
              [winner-box
               "OVERALL LEADER"
               (:name overall-leader)
               (:total-score overall-leader)]
              [:div {:class "winner"}
               [:span {:class "winner-label"} "SCOREBOARD"]
               [:table
                [:thead
                 [:tr [:th "Player"] [:th "Round"] [:th "Overall"]]]
                (into [:tbody]
                      (for [[idx player] (->> players
                                              (sort-by :round-score)
                                              (map-indexed vector))
                            :let [{:keys [name round-score total-score]} player]]
                        [:tr {:key idx}
                         [:td name]
                         [:td round-score]
                         [:td total-score]]))]]])
           [:button {:on-click #(rf/dispatch [:play-another-round])
                     :class "action-button"}
            [:span "Play another round"]]
           [:button {:on-click #(rf/dispatch [:start-over])
                     :class "action-button"}
            [:span "Start over"]]]
    :playing [:div
              [game-middle]
              [playing-players]]
    :not-started (let [players (listen :players)]
                   [:div
                    [:button
                     {:on-click #(rf/dispatch [:start-game])
                      :class "button"}
                     "Start Game"]
                    [:div "---- Players ----"
                     [:div
                      (doall (for [[idx player] (map-indexed vector players)]
                               [:div {:key idx}
                                (:name player)]))]]])))

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
    [:button {:type "submit" :class "button ml3 mt3"} "Join Game"]]
   [:br]
   [:button {:on-click #(rf/dispatch [:create-game])
             :class "button"}
    "Create new game"]])

(defn header []
  [:div {:class "header"}
   (when-let [code (listen :game-code)]
     [:span {:style {:padding-right 10}} "Game code: " code])
   (if (listen :logged-in?)
     [:span [:button {:class "button"
                      :on-click #(rf/dispatch [:sign-out])}
       "Sign Out"]]
     [:span [:button {:on-click #(rf/dispatch [:sign-in])
                      :class "button"}
             "Sign in"]
      [:span {:style {:margin-left "1em"}}
       "If you click Sign In and nothing happens, check your pop-up blocker!"]])])

(defn not-signed-in []
  [:div
   [:p "Welcome to No Thanks!"]
   [:p "Sign in with a Google account by clicking above to join the game."]
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
   #_(when config/debug?
       [:pre {:class "database"}
        (with-out-str (pprint/pprint @(rf/subscribe [:db])))])])
