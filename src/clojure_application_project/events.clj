(ns clojure-application-project.events
  (:require [clojure-application-project.helpers :as helpers]))


;(defn update-player-stats
;  [state event is-good]
;  (let [curr-team (:possession state)
;        ball-holder-id (:id (:ball-holder state))
;        zone ((:zone state) helpers/zone-equals)]
;    (if (= is-good 1)
;      (-> state
;          (update-in [curr-team :team :players zone]
;                     (fn [players]
;                       (map (fn [p]
;                              (if (= (:id p) ball-holder-id)
;                                (case event
;                                  "shot" (-> p
;                                             (update :shots inc)
;                                             (update :goals inc))
;                                  "pass" (-> p
;                                             (update :passes inc)
;                                             (update :good-passes inc))
;                                  "duel" (-> p
;                                             (update :duels inc)
;                                             (update :duels-won inc)))
;                                p))
;                            players))))
;      (-> state
;          (update-in [curr-team :team :players zone]
;                     (fn [players]
;                       (map (fn [p]
;                              (if (= (:id p) ball-holder-id)
;                                (case event
;                                  "shot" (update p :shots inc)
;                                  "pass" (update p :passes inc)
;                                  "duel" (update p :duels inc))
;                                p))
;                            players)))))))

(defn update-duel
  [state is-duel-won opp-player]
  (let [curr-team (:possession state)
        opp-team (helpers/opposite-team curr-team)
        ball-holder-id (:id (:ball-holder state))
        opp-player-id (:id opp-player)
        zone (:zone state)]
    (if (= is-duel-won 1)
      (-> state
          (update-in [curr-team :team :players zone]
                     (fn [players]
                       (map (fn [p]
                              (if (= (:id p) ball-holder-id)
                                (-> p
                                    (update :duels inc)
                                    (update :duels-won inc))
                                p))
                            players)))
          (update-in [opp-team :team :players (helpers/opposite-zone state)]
                     (fn [players]
                       (map (fn [p]
                              (if (= (:id p) opp-player-id)
                                (-> p
                                    (update :duels inc))
                                p))
                            players))))
      (-> state
          (update-in [opp-team :team :players (helpers/opposite-zone state)]
                     (fn [players]
                       (map (fn [p]
                              (if (= (:id p) opp-player-id)
                                (-> p
                                    (update :duels inc)
                                    (update :duels-won inc))
                                p))
                            players)))
          (update-in [curr-team :team :players zone]
                     (fn [players]
                       (map (fn [p]
                              (if (= (:id p) ball-holder-id)
                                (-> p
                                    (update :duels inc))
                                p))
                            players)))))))

(defn update-duel-2
  [state is-duel-won opp-player]
  (let [curr-team (:possession state)
        opp-team (helpers/opposite-team curr-team)
        ball-holder-id (:id (:ball-holder state))
        opp-player-id (:id opp-player)
        zone (:zone state)]
    (if (= is-duel-won 1)
      (-> state
          (update-in [curr-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) ball-holder-id)
                                               (-> p
                                                   (update :duels inc)
                                                   (update :duels-won inc))
                                               p))
                                           players)))))
          (update-in [opp-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) opp-player-id)
                                               (-> p
                                                   (update :duels inc))
                                               p))
                                           players))))))
      (-> state
          (update-in [curr-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) opp-player-id)
                                               (-> p
                                                   (update :duels inc)
                                                   (update :duels-won inc))
                                               p))
                                           players)))))
          (update-in [opp-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) ball-holder-id)
                                               (-> p
                                                   (update :duels inc))
                                               p))
                                           players)))))))))

;Resiti kada se prolazi u novu zonu nakon dobrog duela
;   Problem npr. duel posle prelaska u novu zonu
;   kako gadjati igraca iz zone i updatovati mu stats i sl...???
;
;   POTENCIJALNO RESENJE: update-vals
;   (update-in curr-team [:team :players]
;              (fn [players-map]
;                (update-vals players-map
;                             (fn [players]
;                               (mapv (fn [p]
;                                       (if (= (:id p) ball-holder-id)
;                                         (-> p
;                                             (update :duels inc)
;                                             (update :duels-won inc))
;                                         p))
;                                     players)))))

(defn duel
  [state]
  (let [ball-holder (:ball-holder state)
        opp-player (helpers/rand-opposite-player state)]
  (if (> (:skill ball-holder) (:skill opp-player))
  ;(if (> (:skill ball-holder) 1)
    (let [current-team (:possession state)
          diff-team (helpers/opposite-team current-team)]
      (if (<= (rand-int 3) 1)
      ;(if (<= (rand-int 3) -1)
        (-> state
            (update-duel-2 1 opp-player)
            (update-in [:log current-team] conj :duel-won)
            (update-in [:log diff-team] conj :duel-lost))
        (-> state
            (update-duel-2 1 opp-player)
            (update-in [:log current-team] conj :duel-won)
            (update-in [:log diff-team] conj :duel-lost)
            (update :zone helpers/next-zone))))
    (let [old-possession (:possession state)
          new-possession (helpers/opposite-team old-possession)
          new-zone (helpers/opposite-zone state)]
      (-> state
          (update-duel-2 0 opp-player)
          (update-in [:log old-possession] conj :duel-lost)
          (update-in [:log new-possession] conj :duel-won)
          (assoc :zone new-zone)
          (assoc :possession new-possession
                 :ball-holder opp-player))))))

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

(defn update-shot
  [state is-goal]
  (let [curr-team (:possession state)
        ball-holder-id (:id (:ball-holder state))
        zone (:zone state)]
    (if (= is-goal 1)
      (-> state
          (update-in [curr-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) ball-holder-id)
                                               (-> p
                                                   (update :shots inc)
                                                   (update :goals inc))
                                               p))
                                           players))))))
      (-> state
          (update-in [curr-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) ball-holder-id)
                                               (-> p
                                                   (update :duels inc))
                                               p))
                                           players)))))))))

(defn finish-shot
  [state event]
  (let [team (:possession state)
        new-possession (helpers/opposite-team team)]
    (if (= event :goal)
      (let [updated-ball-holder (helpers/new-ball-holder-2 state new-possession :attack)]
        (-> state
            (update-shot 1)
            (update-in [:log team] conj event)
            (update-in [:log new-possession] conj :conceded-goal)
            (update-in [team :goals] + 1)
            (assoc :zone :resume)
            (assoc :ball-holder updated-ball-holder)
            (assoc :possession new-possession)))
      (let [updated-ball-holder (helpers/new-ball-holder-2 state new-possession :defense)]
          (-> state
            (update-shot 0)
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

(defn update-pass
  [state is-good-pass]
  (let [curr-team (:possession state)
        ball-holder-id (:id (:ball-holder state))
        zone (:zone state)]
    (if (= is-good-pass 1)
      (-> state
          (update-in [curr-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) ball-holder-id)
                                               (-> p
                                                   (update :passes inc)
                                                   (update :good-passes inc))
                                               p))
                                           players))))))
      (-> state
          (update-in [curr-team :team :players]
                     (fn [pl-pos-map]
                       (update-vals pl-pos-map
                                    (fn [players]
                                      (map (fn [p]
                                             (if (= (:id p) ball-holder-id)
                                               (-> p
                                                   (update :passes inc))
                                               p))
                                           players)))))))))

(defn finish-pass
  [state zone team log-text]
      (if (= log-text :pass)
        (let [updated-ball-holder (helpers/new-ball-holder state team zone)]
          (-> state
              (update-pass 1)
              (assoc :possession team)
              (assoc :zone zone)
              (assoc :ball-holder updated-ball-holder)
              (update-in [:log team] conj :pass)))
        (let [updated-ball-holder (helpers/new-ball-holder-2 state team zone)]
          (-> state
              (update-pass 0)
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

(defn offside
  [state]
  (let [new-possession (helpers/opposite-team (:possession state))
        old-possession (:possession state)
        new-zone :defense
        old-zone :attack
        new-holder (helpers/new-ball-holder-2 state new-possession new-zone)
        offside-holder-id (:id (helpers/new-ball-holder state old-possession old-zone))]
    (-> state
        (update-pass 0)
        (update-in [old-possession :team :players]
                   (fn [player-pos-map]
                     (update-vals player-pos-map
                                  (fn [players]
                                    (map (fn [p]
                                           (if (= (:id p) offside-holder-id)
                                             (-> p
                                                 (update :offsides inc))
                                             p))
                                         players)))))
        (assoc :possession new-possession)
        (assoc :ball-holder new-holder)
        (assoc :zone :offside)
        (update-in [:log old-possession] conj :offside))))

(defn pass
  [state]
  (if (true? helpers/offside?)
  ;(if (true? true)
    (offside state)
    (if (helpers/pass? (:ball-holder state))
      (good-pass state)
      (bad-pass state))))

(defn resume-game
  [state]
  (good-pass state))

(def zone-actions-controller
  {:defense [pass duel]
   :midfield [pass duel]
   :attack [pass duel shot]
   :offside [pass]
   :resume [resume-game]})

