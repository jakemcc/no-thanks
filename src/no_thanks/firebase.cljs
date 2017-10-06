(ns no-thanks.firebase
  (:require clojure.walk
            com.degel.re-frame-firebase.core
            [re-frame.core :as rf]
            [sodium.re-utils :as re-utils]))

(def fb-ref #'com.degel.re-frame-firebase.core/fb-ref)

(defn success-failure-wrapper [on-success on-failure]
  (let [on-success (and on-success (re-utils/event->fn on-success))
        on-failure (and on-failure (re-utils/event->fn on-failure))]
    (fn [err]
      (cond (nil? err) (when on-success (on-success))
            on-failure (on-failure err)
            :else      ;; [TODO] This should use default error handler
                       (js/console.error "Firebase error:" err)))))


(defn- js->clj-tree [x]
  (-> x
      js->clj
      clojure.walk/keywordize-keys))

(defn firebase-transaction-effect [{:keys [path function on-success on-failure]}]
  (.transaction (fb-ref path)
                (fn [data] (-> data
                               js->clj-tree
                               function
                               clj->js))
                (success-failure-wrapper on-success on-failure)))

(rf/reg-fx :firebase/swap! firebase-transaction-effect)
