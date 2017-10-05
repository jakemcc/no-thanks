(ns no-thanks.game)

(defn partition-by-pairs [f xs]
  (loop [res []
         curr []
         [pair & pairs] (partition-all 2 1 xs)]
    (println "res:" res "curr:" curr)
    (case (count pair)
      0 res

      1 (conj res curr)
      
      2 (if (apply f pair)
          (recur (conj res (conj curr (first pair)))
                 []
                 pairs)
          (recur res
                 (conj curr (first pair))
                 pairs))))
  )

(defn score-player [player]
  (reduce + (:cards player)))
