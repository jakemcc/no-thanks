(ns no-thanks.game-test
  (:require #?(:clj [clojure.test :as t :refer [deftest is testing]]
               :cljs [cljs.test :as t :include-macros true])
            [no-thanks.game :as game]))

(deftest test-score-player
  (is (= 1 (game/score-player {:cards [1]})))
  (is (= 4 (game/score-player {:cards [1 3]})))

  (is (= 4 (game/score-player {:cards [1 3 4]})))
  (is (= 1 (game/score-player {:cards [1 2 3]})))

  (is (= -3 (game/score-player {:cards [1 2 3]
                                :tokens 4}))))

(deftest test-initial-game
  (let [game (game/initialize 5)]
    (is (= 5 (count (:players game))))
    (is (= 11 (-> game :players first :tokens)))
    (is (= [] (-> game :players first :cards)))
    (is (= (inc (- 35 3 9)) (count (:draw-pile game))))
    (is (= 0 (:current-player game))))

  (let [game (game/initialize 6)]
    (is (= 6 (count (:players game))))
    (is (= 9 (-> game :players first :tokens))))

  (let [game (game/initialize 7)]
    (is (= 7 (count (:players game))))
    (is (= 7 (-> game :players first :tokens)))))

(deftest test-turn
  (testing "player takes a card"
    (let [game (game/turn {:draw-pile [8 1 10]
                           :token-pot 2
                           :current-player 0
                           :players [{:tokens 10 :cards []}
                                     {:tokens 9 :cards []}]}
                          :take-card)]
      (is (= [8] (get-in game [:players 0 :cards])))
      (is (= 12 (get-in game [:players 0 :tokens])))
      (is (= 0 (:current-player game)))
      (is (= [1 10] (:draw-pile game)))
      (is (zero? (:token-pot game)))))

  (testing "player passes"
    (let [game (game/turn {:draw-pile [8 1 10]
                           :token-pot 2
                           :current-player 0
                           :players [{:tokens 10 :cards []}
                                     {:tokens 9 :cards []}]}
                          :pass)]
      (is (= [] (get-in game [:players 0 :cards])))
      (is (= 9 (get-in game [:players 0 :tokens])))
      (is (= 1 (:current-player game)))
      (is (= [8 1 10] (:draw-pile game)))
      (is (= 3 (:token-pot game)))))

  (testing "play goes from last player to first"
    (let [game (game/turn {:draw-pile [8 1 10]
                           :token-pot 2
                           :current-player 1
                           :players [{:tokens 10 :cards []}
                                     {:tokens 9 :cards []}]}
                          :pass)]
      (is (= 0 (:current-player game)))))

  (testing "Player takes card with zero tokens on it"
    (let [game (game/turn {:draw-pile [8 1 10]
                           :token-pot 0
                           :current-player 0
                           :players [{:tokens 0 :cards []}
                                     {:tokens 0 :cards []}]}
                          :take-card)]
      (is (= [8] (get-in game [:players 0 :cards])))
      (is (= 0 (get-in game [:players 0 :tokens]))))))
