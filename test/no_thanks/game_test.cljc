(ns no-thanks.game-test
  (:require #?(:clj [clojure.test :as t :refer [deftest is]]
               :cljs [cljs.test :as t :include-macros true])
            [no-thanks.game :as game]))

(deftest test-partition-by-pairs
  (is (= [[1 1]]
         (game/partition-by-pairs not= [1 1]))))

;; (deftest test-score-player
;;   (is (= 1 (game/score-player {:cards [1]})))
;;   (is (= 4 (game/score-player {:cards [1 3]})))
;;   (is (= 1 (game/score-player {:cards [1 2 3]}))))
