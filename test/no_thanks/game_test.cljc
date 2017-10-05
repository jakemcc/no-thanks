(ns no-thanks.game-test
  (:require #?(:clj [clojure.test :as t :refer [deftest is]]
               :cljs [cljs.test :as t :include-macros true])
            [no-thanks.game :as game]))

(comment
  [1 3 4] -> [[1] [3 4]]
  [1 3 4 5] -> [[1] [3 4 5]]
  )

(deftest test-score-player
  (is (= 1 (game/score-player {:cards [1]})))
  (is (= 4 (game/score-player {:cards [1 3]})))
  
  (is (= 4 (game/score-player {:cards [1 3 4]})))
  (is (= 1 (game/score-player {:cards [1 2 3]}))))
