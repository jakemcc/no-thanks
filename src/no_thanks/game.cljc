(ns no-thanks.game)

(defn score-player [player]
  (- (loop [cards (sort (:cards player))
            current-run []
            total 0]
       (if (seq cards)
         (let [[card & remaining-cards] cards]
           (if (or (empty? current-run)
                   (= 1 (- card (last current-run))))
             (recur remaining-cards (conj current-run card) total)
             (recur cards [] (+ total (first current-run)))))
         (+ (first current-run) total)))
     (:tokens player 0)))

(defn initialize [number-players]
  (let [starting-tokens (condp > number-players
                         6 11
                         7 9
                         8 7)]
    {:draw-pile (drop 9 (shuffle (range 3 36)))
     :current-player 0
     :players (vec (repeat number-players {:tokens starting-tokens :cards []}))}))

(defn new-round [game]
  (let [next-game (initialize (count (:players game)))]
    (update next-game :players
            (fn [new-players]
              (vec (for [[old-player new-player] (map vector (:players game) new-players)]
                     (update new-player :total-score (fnil + 0) (score-player old-player))))))))

(defn draw-card [game]
  (-> game
      (update :shown-card (first (:draw-pile game)))
      (update :draw-pile (rest (:draw-pile game)))))

(defn turn [game action]
  (if (= action :take-card)
    (-> game
        (update :draw-pile rest)
        (assoc :token-pot 0)
        (update-in [:players (:current-player game) :tokens] + (:token-pot game))
        (update-in [:players (:current-player game) :cards] conj (first (:draw-pile game))))
    (-> game
        (update :current-player (comp #(rem % (count (:players game))) inc))
        (update :token-pot inc)
        (update-in [:players (:current-player game) :tokens] dec))))

(def game-over? (comp empty? :draw-pile))

