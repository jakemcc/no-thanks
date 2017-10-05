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
     (:chips player 0)))

(defn initialize [number-players]
  (let [starting-chips (condp > number-players
                         6 11
                         7 9
                         8 7)]
    {:draw-pile (drop 9 (shuffle (range 3 36)))
     :players (vec (repeat number-players {:chips starting-chips :cards []}))}))
