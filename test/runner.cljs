(ns no-thanks.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [no-thanks.core-test]))

(doo-tests 'no-thanks.core-test)
