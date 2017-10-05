(ns no-thanks.game)

(defn score-player [player]
  (loop [cards (sort (:cards player))
         current-run []
         total 0]
    (if (seq cards)
      (let [[card & remaining-cards] cards]
        (if (or (empty? current-run)
                (= 1 (- card (last current-run))))
          (recur remaining-cards (conj current-run card) total)
          (recur cards [] (+ total (first current-run)))))
      (+ (first current-run) total))))
