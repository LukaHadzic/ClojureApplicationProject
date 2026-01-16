(ns clojure-application-project.events
  (:require [clojure-application-project.helpers :as helpers]))

(defn duel
  [state]
  (if (< (:skill (:ball-holder state)) (rand-int 101))
    (let [current-team (:possession state)
          diff-team (helpers/opposite-team current-team)]
      (-> state
          (update-in [:log current-team] conj :duel-won)
          (update-in [:log diff-team] conj :duel-lost)))
    (let [old-possession (:possession state)
          new-possession (helpers/opposite-team old-possession)
          new-zone (helpers/opposite-zone state)
          updated-ball-holder (helpers/new-ball-holder-2
                                state new-possession
                                new-zone)]
      (-> state
          (update-in [:log old-possession] conj :duel-lost)
          (update-in [:log new-possession] conj :duel-won)
          (assoc :zone new-zone)
          (assoc :possession new-possession
                 :ball-holder updated-ball-holder)))))

(defn offside
  [state]
  (let [new-possession (helpers/opposite-team (:possession state))
        old-possession (:possession state)
        new-zone :defense
        new-holder (helpers/new-ball-holder-2 state new-possession new-zone)]
    (-> state
        (assoc :possession new-possession)
        (assoc :ball-holder new-holder)
        (assoc :zone :offside)
        (update-in [:log old-possession] conj :offside))))

;(defn finish-shot
;  [state event-1 event-2 zone is-goal new-zone]
;  (let [team (:possession state)
;        new-possession (helpers/opposite-team team)
;        updated-ball-holder (helpers/new-ball-holder-2 state new-possession zone)]
;    (-> state
;        (update-in [:log team] conj event-1)
;        (update-in [:log new-possession] conj event-2)
;        (update-in [team :goals] + is-goal)
;        (assoc :zone new-zone)
;        (assoc :ball-holder updated-ball-holder)
;        (assoc :possession new-possession))))

(defn finish-shot
  [state event]
  (let [team (:possession state)
        new-possession (helpers/opposite-team team)]
    (if (= event :goal)
      (let [updated-ball-holder (helpers/new-ball-holder-2 state new-possession :attack)]
        (-> state
            (update-in [:log team] conj event)
            (update-in [:log new-possession] conj :conceded-goal)
            (update-in [team :goals] + 1)
            (assoc :zone :resume)
            (assoc :ball-holder updated-ball-holder)
            (assoc :possession new-possession)))
      (let [updated-ball-holder (helpers/new-ball-holder-2 state new-possession :defense)]
        (-> state
            (update-in [:log team] conj :miss)
            (update-in [:log new-possession] conj :ball-won)
            (assoc :zone :defense)
            (assoc :ball-holder updated-ball-holder)
            (assoc :possession new-possession))))))

(defn goal
  [state]
  ;(finish-shot state :goal :conceded-goal :midfield 1 :resume))
  (finish-shot state :goal))


(defn miss
  [state]
  ;(finish-shot state :miss :ball-won :defense 0 :defense))
  (finish-shot state :miss))

(defn shot
  [state]
  (if (helpers/goal? (:ball-holder state))
    (goal state)
    (miss state)))

(defn finish-pass
  [state zone team log-text]
      (if (= log-text :pass)
        (let [updated-ball-holder (helpers/new-ball-holder state team zone)]
          (-> state
              (assoc :possession team)
              (assoc :zone zone)
              (assoc :ball-holder updated-ball-holder)
              (update-in [:log team] conj :pass)))
        (let [updated-ball-holder (helpers/new-ball-holder-2 state team zone)]
          (-> state
              (assoc :possession team)
              (assoc :zone zone)
              (assoc :ball-holder updated-ball-holder)
              (update-in [:log team] conj :ball-won)
              (update-in [:log (helpers/opposite-team team)] conj :ball-lost)))))

(defn good-pass
  [state]
  (let [new-zone (helpers/rand-zone)
        team (:possession state)]
    (finish-pass state new-zone team :pass)))

(defn bad-pass
  [state]
  (let [new-zone (:zone state)
        team (helpers/opposite-team (:possession state))]
    (finish-pass state new-zone team :ball-won)))

(defn pass
  [state]
  (if (helpers/pass? (:ball-holder state))
    (good-pass state)
    (bad-pass state)))

(defn resume-game
  [state]
  (good-pass state))

(def zone-actions-controller
  {:defense [pass duel]
   :midfield [pass duel]
   :attack [pass duel shot offside]
   :offside [pass]
   :resume [resume-game]})

