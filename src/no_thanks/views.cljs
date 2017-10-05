(ns no-thanks.views
  (:require [cljs.pprint :as pprint]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [no-thanks.game :as game]))

(defn listen [query]
  @(rf/subscribe [query]))

(defn main-panel []
  [:div
   (case (listen :view)
     :pregame [:div
               [:div "How many players? "
                [:form {:on-submit
                        (fn [e]
                          (.preventDefault e)
                          (rf/dispatch [:set-number-players (.. e -target -elements -players -value)]))}
                 [:input {:type "number" :name "players" :required true}]
                 [:input {:type "submit" :value "Submit"}]]]]
     :game
     (if (listen :game-over)
       [:div "GAME OVER!"
        (doall (for [[idx player] (map-indexed vector (listen :players))]
                 [:div (str "player " idx "'s score: " (game/score-player player))]))]
       [:div "No Thanks!"
        [:br] [:br]
        [:div
         "Top card: " (listen :top-card)
         [:br]
         "Tokens on card: " (listen :token-pot)]
        (doall (for [[idx player] (map-indexed vector (listen :players))]
                 [:div {:key idx}
                  [:div (str "----- player " idx " ------")]
                  [:div "Cards: " (string/join ", " (sort (:cards player)))]
                  [:div "Tokens: " (:tokens player)]
                  (when (= idx (listen :current-player))
                    [:div
                     [:button {:on-click #(rf/dispatch [:take-card])} "Take card"]
                     [:button {:on-click #(rf/dispatch [:no-thanks!]) :disabled (zero? (:tokens player))} "No thanks!"]])]))])
     [:div "Missing view clause"])
   [:pre {:class "database"}
    (with-out-str (pprint/pprint @(rf/subscribe [:db])))]])
