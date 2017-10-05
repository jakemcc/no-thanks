(ns no-thanks.game-test
  (:require #?(:clj [clojure.test :as t :refer [deftest is]]
               :cljs [cljs.test :as t :include-macros true])
            [no-thanks.game :as game]))

(deftest test-score-player
  (is (= 1 (game/score-player {:cards [1]})))
  (is (= 4 (game/score-player {:cards [1 3]})))
  
  (is (= 4 (game/score-player {:cards [1 3 4]})))
  (is (= 1 (game/score-player {:cards [1 2 3]})))

  (is (= -3 (game/score-player {:cards [1 2 3]
                                :chips 4}))))

(deftest test-initial-game
  (let [game (game/initialize 5)]
    (is (= 5 (count (:players game))))
    (is (= 11 (-> game :players first :chips)))
    (is (= [] (-> game :players first :cards)))
    (is (= (inc (- 35 3 9)) (count (:draw-pile game)))))

  (let [game (game/initialize 6)]
    (is (= 6 (count (:players game))))
    (is (= 9 (-> game :players first :chips))))
  
  (let [game (game/initialize 7)]
    (is (= 7 (count (:players game))))
    (is (= 7 (-> game :players first :chips)))))
