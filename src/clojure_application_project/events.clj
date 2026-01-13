(ns clojure-application-project.events
  (:require [clojure-application-project.helpers :as helpers]))

(defn pass
  [state]
  (-> state
      (assoc :zone (rand-nth [:midfield :attack :defense]))
      ;Dodati new ball holder ako pas prodje
      (update-in [:log (:possession state)] conj :pass)))

(defn duel
  [state]
  (if (< (:skill (:ball-holder state)) (rand-int 101))
    (-> state
        (update-in [:log (:possession state)] conj :duel-won)
        (update-in [:log (helpers/opposite-team (:possession state))] conj :duel-lost))
    (let [new-possession (helpers/opposite-team (:possession state))
          players (get-in state [new-possession :team :players (:zone state)])
          new-ball-holder (rand-nth players)]
      (-> state
          (update-in [:log (:possession state)] conj :duel-lost)
          (update-in [:log (helpers/opposite-team (:possession state))] conj :duel-won)
          (assoc :possession new-possession :ball-holder new-ball-holder)))))

(defn offside
  [state]
  (let [new-possession (helpers/opposite-team (:possession state))
        new-zone :defense
        new-holder (helpers/new-ball-holder-2 state new-possession new-zone)]
    (-> state
        (assoc :possession new-possession)
        (assoc :ball-holder new-holder)
        (assoc :zone :offside)
        (update-in [:log (:possession state)] conj :offside))
    )
  )

(defn finish-shot
  [state event zone is-goal]
  (let [team (:possession state)
        new-possession (helpers/opposite-team (:possession state))]
    (-> state
        (update-in [:log team] conj event)
        (update-in [team :goals] + is-goal)
        (assoc :zone zone)
        (assoc :ball-holder (helpers/new-ball-holder-2 state new-possession zone))
        (update :possession new-possession))))

(defn goal [state]
  (finish-shot state :goal :midfield 1))

(defn miss [state]
  (finish-shot state :miss :defense 0))

(defn shot
  [state]
  (if (helpers/goal? (:ball-holder state))
    (goal state)
    (miss state)))

(def zone-actions-controller
  {:defense [pass duel]
   :midfield [pass duel]
   :attack [pass duel shot offside]
   :offside [pass]})
