(ns clojure-application-project.events
  (:require [clojure-application-project.helpers :as helpers]))

(defn update-shot
  [state is-goal]
  (let [curr-team (:possession state)
        ball-holder-id (:id (:ball-holder state))]
    (if (= is-goal 1)
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:shots :shots-on-goal :goals]))
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:shots])))))

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
            (assoc :zone :attack)
            (assoc :phase :resume)
            (assoc :ball-holder updated-ball-holder)
            (assoc :possession new-possession)))
      (let [updated-ball-holder (first (get-in state [new-possession :team :players :goalkeeper]))]
          (-> state
            (update-shot 0)
            (update-in [:log team] conj :miss)
            (update-in [:log new-possession] conj :ball-won)
            (assoc :zone :goalkeeper)
            (assoc :phase :goal-out)
            (assoc :ball-holder updated-ball-holder)
            (assoc :possession new-possession))))))

(defn goal
  [state]
  ;(finish-shot state :goal :conceded-goal :midfield 1 :resume))
  (finish-shot state :goal))

(defn miss
  [state]
  (finish-shot state :miss))

(defn shot-saved
  "Goalkeeper can save shot"
  [state]
  (let [curr-team (:possession state)
        opp-team (helpers/opposite-team curr-team)
        ball-holder-id (:id (:ball-holder state))
        goalkeeper-id (:id (first (get-in state [opp-team :team :players :goalkeeper])))]
    (if (= (rand-int 3) 1) ; Da li ce lopta nakon parade ostati kod golmana ili otici kod nekog u odbrani
    ;(if (= 1 1) ;PROMENITI
      (-> state
          (assoc :possession opp-team)
          (assoc :ball-holder (first (get-in state [opp-team :team :players :goalkeeper])))
          (helpers/inc-events curr-team ball-holder-id [:shots-on-goal :shots])
          (helpers/inc-events opp-team goalkeeper-id [:saves])
          (assoc :zone :goalkeeper)
          (assoc :phase :goalkeeper)
          (update-in [:log opp-team] conj :shot-saved)
          (update-in [:log curr-team] conj :miss))
      (-> state
          (assoc :possession opp-team)
          (assoc :ball-holder (helpers/new-ball-holder state opp-team :defense))
          (helpers/inc-events curr-team ball-holder-id [:shots-on-goal :shots])
          (helpers/inc-events opp-team goalkeeper-id [:saves])
          (assoc :zone :defense)
          (assoc :phase :defense)
          (update-in [:log opp-team] conj :shot-saved)
          (update-in [:log curr-team] conj :miss)))))

(declare get-shot-duration)
(defn shot
  "Shot can be on or off the goal"
  [state]
  (let [ball-holder (:ball-holder state)
        opp-team (helpers/opposite-team (:possession state))
        goalkeeper (first (get-in state [opp-team :team :players :goalkeeper]))
        shot-duration (+ (rand) (get-shot-duration (:zone state)))
        new-state
        (if (helpers/closer-value-to-first? (:skill ball-holder) (:skill goalkeeper))
        ;(if (true? false) ;PROMENITI
          (if (helpers/goal? (:ball-holder state))
            (goal state)
            (miss state))
          (shot-saved state))]
    (helpers/wrap-return new-state shot-duration)))

(defn update-pass
  [state is-good-pass]
  (let [curr-team (:possession state)
        ball-holder-id (:id (:ball-holder state))]
    (if (= is-good-pass 1)
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:passes :good-passes]))
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:passes])))))

(defn finish-pass
  [state zone team log-text]
      (if (= log-text :pass)
        (let [updated-ball-holder (helpers/new-ball-holder state team zone)]
          (-> state
              (update-pass 1)
              (assoc :possession team)
              (assoc :zone zone)
              (assoc :phase zone)
              (assoc :ball-holder updated-ball-holder)
              (update-in [:log team] conj :pass)))
        ;(let [new-zone (helpers/opposite-zone state)
        (let [new-zone (helpers/opposite-zone zone)
              updated-ball-holder (helpers/new-ball-holder-2 state team new-zone)]
          (-> state
              (update-pass 0)
              (assoc :possession team)
              (assoc :zone new-zone)
              (assoc :phase new-zone)
              (assoc :ball-holder updated-ball-holder)
              (update-in [:log team] conj :ball-won)
              (update-in [:log (helpers/opposite-team team)] conj :ball-lost)))))

(declare get-pass-duration)
(defn resume-good-pass
  [state zone-end]
  (let [team (:possession state)
        pass-duration (+ (rand) (get-pass-duration :attack zone-end))]
    (helpers/wrap-return (finish-pass state zone-end team :pass) pass-duration)))

(defn good-pass
  ;"Pass is good, choose player from pass-end zone for new ball holder"
  [state zone-end]
  (let [team (:possession state)]
    (finish-pass state zone-end team :pass)))

(defn bad-pass
  [state zone-end]
  (let [team (helpers/opposite-team (:possession state))]
    (finish-pass state zone-end team :ball-won)))

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
        (helpers/inc-events old-possession offside-holder-id [:offsides])
        (assoc :possession new-possession)
        (assoc :ball-holder new-holder)
        (assoc :zone :defense)
        (assoc :phase :offside)
        (update-in [:log old-possession] conj :offside))))

(defn out ;Upada se u petlju, mora da se ustanovi da je out pa tek onda da se izvede
  [state zone-end] ;Kada se izvede ustanovi se da je izveden
  (let [ball-holder-id (get-in state [:ball-holder :id])
        old-possession (:possession state)
        new-possession (helpers/opposite-team old-possession)
        new-zone (helpers/opposite-zone zone-end)
        new-ball-holder (helpers/new-ball-holder state new-possession new-zone)]
    (-> state
        (helpers/inc-events old-possession ball-holder-id [:passes])
        (assoc :possession new-possession)
        (assoc :ball-holder new-ball-holder)
        (assoc :zone new-zone)
        (assoc :phase :out)
        (update-in [:log old-possession] conj :out-ball-lost)
        (update-in [:log new-possession] conj :out-ball-won))))

(declare get-pass-duration)
(defn pass ;Za izvodjenje out-a napraviti pass-no-offside koji je veoma slicna fja
  [state] ;Bolje napraviti i resume-out koja ce zvati pass-no-offside
  (if (helpers/offside?)
    (let [event-duration (+ (rand) (get-pass-duration (:zone state) :attack))
          new-state (offside state)]
      (helpers/wrap-return new-state event-duration))
    ;(offside state)
    (let [zone-begin (:zone state)
          zone-end (helpers/choose-pass-end-zone zone-begin)
          pass-duration (+ (rand) (get-pass-duration zone-begin zone-end))]
      (if (helpers/out? (:ball-holder state))
        (helpers/wrap-return (out state zone-end) pass-duration)
        ;(out state zone-end)
        (if (helpers/pass? zone-begin zone-end)
          (helpers/wrap-return (good-pass state zone-end) pass-duration)
          (helpers/wrap-return (bad-pass state zone-end) pass-duration))))))

(defn pass-no-offside ;Za izvodjenje out-a napraviti pass-no-offside koji je veoma slicna fja
  [state] ;Bolje napraviti i resume-out koja ce zvati pass-no-offside
  (let [zone-begin (:zone state)
        zone-end (helpers/choose-pass-end-zone zone-begin)
        pass-duration (+ (rand) (get-pass-duration zone-begin zone-end))]
    (if (and (= (:phase state) :out)
         (helpers/out? (:ball-holder state)))
      (helpers/wrap-return (out state zone-end) pass-duration)
      (let [new-state
            (if (helpers/pass? zone-begin zone-end)
              (-> state
                  (good-pass zone-end)
                  (assoc :phase zone-end))
              (-> state
                  (bad-pass zone-end)
                  (assoc :phase zone-end)))]
        (helpers/wrap-return new-state pass-duration)))))

(defn update-duel
  [state is-duel-won opp-player]
  (let [curr-team (:possession state)
        opp-team (helpers/opposite-team curr-team)
        ball-holder-id (:id (:ball-holder state))
        opp-player-id (:id opp-player)]
    (if (= is-duel-won 1)
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:duels :duels-won])
          (helpers/inc-events opp-team opp-player-id [:duels]))
      (-> state
          (helpers/inc-events opp-team opp-player-id [:duels :duels-won])
          (helpers/inc-events curr-team ball-holder-id [:duels])))))

;(defn duel
;  [state]
;  (let [ball-holder (:ball-holder state)
;        opp-player (helpers/rand-opposite-player state)]
;  (if (> (:skill ball-holder) (:skill opp-player))
;  ;(if (> (:skill ball-holder) 1)
;  ;(if (helpers/closer-value-to-first?
;  ; (helpers/calc-avg (:strength ball-holder) (:speed ball-holder))
;  ; (helpers/calc-avg (:strength opp-player) (:speed opp-player)))
;    (let [current-team (:possession state)
;          diff-team (helpers/opposite-team current-team)]
;      (if (<= (rand-int 3) 1)
;      ;(if (<= (rand-int 3) -1)
;      ;(if (> (helpers/closer-value-to-first? (:speed ball-holder) (:speed opp-player)))
;      (-> state
;            (update-duel 1 opp-player)
;            (update-in [:log current-team] conj :duel-won)
;            (update-in [:log diff-team] conj :duel-lost))
;        (-> state
;            (update-duel 1 opp-player)
;            (update-in [:log current-team] conj :duel-won)
;            (update-in [:log diff-team] conj :duel-lost)
;            (assoc :zone (helpers/next-zone (:zone state)))
;            (assoc :phase (helpers/next-zone (:zone state))))))
;    (let [old-possession (:possession state)
;          new-possession (helpers/opposite-team old-possession)
;          ;new-zone (helpers/opposite-zone state)]
;          new-zone (helpers/opposite-zone (:zone state))]
;      (-> state
;          (update-duel 0 opp-player)
;          (update-in [:log old-possession] conj :duel-lost)
;          (update-in [:log new-possession] conj :duel-won)
;          (assoc :zone new-zone)
;          (assoc :phase new-zone)
;          (assoc :possession new-possession
;                 :ball-holder opp-player))))))

(defn finish-duel
  [state is-duel-won opp-player]
  (if (= is-duel-won 1)
    (let [current-team (:possession state)
          diff-team (helpers/opposite-team current-team)]
      (if (<= (rand-int 3) 1)
        ;(if (<= (rand-int 3) -1)
        ;(if (> (helpers/closer-value-to-first? (:speed ball-holder) (:speed opp-player)))
        (-> state
            (update-duel 1 opp-player)
            (update-in [:log current-team] conj :duel-won)
            (update-in [:log diff-team] conj :duel-lost))
        (-> state
            (update-duel 1 opp-player)
            (update-in [:log current-team] conj :duel-won)
            (update-in [:log diff-team] conj :duel-lost)
            (assoc :zone (helpers/next-zone (:zone state)))
            (assoc :phase (helpers/next-zone (:zone state))))))
    (let [old-possession (:possession state)
          new-possession (helpers/opposite-team old-possession)
          ;new-zone (helpers/opposite-zone state)]
          new-zone (helpers/opposite-zone (:zone state))]
      (-> state
          (update-duel 0 opp-player)
          (update-in [:log old-possession] conj :duel-lost)
          (update-in [:log new-possession] conj :duel-won)
          (assoc :zone new-zone)
          (assoc :phase new-zone)
          (assoc :possession new-possession
                 :ball-holder opp-player)))))

(defn duel-won
  [state opp-player]
  (finish-duel state 1 opp-player))

(defn duel-lost
  [state opp-player]
  (finish-duel state 0 opp-player))

(def event-mapper
  {:shot shot
   :pass pass})

(defn resume-foul
  [state]
  (let [zone (:zone state)
        event (event-mapper (helpers/choose-foul-event zone))]
    (-> state
        (assoc :ball-holder (helpers/choose-pl-for-event state event))
        (event))))

(defn foul
  [state opp-player]
  (let [ball-holder (:ball-holder state)
        curr-team (:possession state)
        opp-team (helpers/opposite-team curr-team)
        new-zone (helpers/opposite-zone (:zone state))]
    (if (helpers/foul-attack? ball-holder opp-player)
      (-> state
          (helpers/inc-events curr-team (:id ball-holder) [:fouls :duels])
          (helpers/inc-events opp-team (:id opp-player) [:duels :duels-won])
          (assoc :ball-holder opp-player)
          (assoc :possession opp-team)
          (assoc :zone new-zone)
          (assoc :phase :foul)
          (update-in [:log curr-team] conj :foul)
          (update-in [:log opp-team] conj :fouled))
      (-> state
          (helpers/inc-events curr-team (:id ball-holder) [:duels :duels-won])
          (helpers/inc-events opp-team (:id opp-player) [:duels :fouls])
          (assoc :phase :foul)
          (update-in [:log curr-team] conj :fouled)
          (update-in [:log opp-team] conj :foul)))))

(declare get-duel-duration)
(defn duel
  [state]
  (let [ball-holder (:ball-holder state)
        opp-player (helpers/rand-opposite-player state)
        duel-duration (+ (rand 0.5) (get-duel-duration ball-holder opp-player))]
    (if (helpers/foul? ball-holder opp-player)
      (helpers/wrap-return (foul state opp-player) duel-duration)
      (if (helpers/duel-won? ball-holder opp-player)
        (helpers/wrap-return (duel-won state opp-player) duel-duration)
        (helpers/wrap-return (duel-lost state opp-player) duel-duration)))))

(defn resume-game
  [state]
  (resume-good-pass state (helpers/choose-pass-end-zone :attack)))

(defn resume-goal-out
  [state]
  (pass (assoc state :phase :goalkeeper)))

(defn resume-offside
  [state] ;Nema ofsajda nakon ofsajda i ova fja zove pass-no-offside
  (pass (assoc state :phase :defense)))

(defn resume-out
  [state]
  (pass-no-offside state))

(def pass-duration-map
  {:goalkeeper {:defense 1
                :midfield 3.5
                :attack 5}
   :defense {:goalkeeper 1.5
             :defense 1
             :midfield 1.5
             :attack 3}
   :midfield {:goalkeeper 3.5
              :defense 1.5
              :midfield 1
              :attack 1.5}
   :attack {:goalkeeper 5
            :defense 3
            :midfield 1
            :attack 0.5}})

(defn get-duel-duration
  [ball-holder opp-player]
  (let [holder-str (:strength ball-holder)
        opp-str (:strength opp-player)
        holder-sp (:speed ball-holder)
        opp-sp (:speed opp-player)
        str-diff (- holder-str opp-str)
        sp-diff (- holder-sp opp-sp)
        diff (Math/sqrt (+ (* str-diff str-diff) (* sp-diff sp-diff)))]
    (* 3 (Math/pow 0.96656 diff)))) ; exp regression (100:0.1s ; 0:3s)y = 3*0.9656^x -> diff:y 30:1.81s ; 25:1.28s ; 20:1.519s ; 15:1.80s...

(def shot-duration-map
  {:defense 3
   :midfield 1.8
   :attack 1})

(def event-mapper-2
  {:shot shot
   :pass pass
   :duel duel
   :resume-offside resume-offside
   :resume-game resume-game
   :resume-goal-out resume-goal-out
   :resume-out resume-out
   :resume-foul resume-foul})

(defn get-pass-duration
  [zone-begin zone-end]
  (get-in pass-duration-map [zone-begin zone-end]))

(defn get-shot-duration
  [zone-begin]
  (get-in shot-duration-map [zone-begin]))

(def phase-actions-controller
  {:goalkeeper {:pass 0.96 :duel 0.04}
   :defense {:pass 0.7 :duel 0.25 :shot 0.05}
   :midfield {:pass 0.4 :duel 0.4 :shot 0.2}
   :attack {:pass 0.35 :duel 0.35 :shot 0.3}
   :offside {:resume-offside 1.0}
   :resume {:resume-game 1.0}
   :goal-out {:resume-goal-out 1.0}
   :out {:resume-out 1.0}
   :foul {:resume-foul 1.0}})

(def zone-lambda-map
  {:goalkeeper 0.1
   :defense 0.2
   :midfield 0.25
   :attack 0.35})

(defn exp-rand
  [zone]
  (let [zone-lambda (zone zone-lambda-map)]
    (/ (- (Math/log (- 1 (rand)))) zone-lambda)))

(defn choose-event
  [phase]
  (let [r (rand)
        actions-probs (phase phase-actions-controller)]
    (loop [acc 0
           [[action prob] & rest] actions-probs]
      (let [new-acc (+ acc prob)]
        (if (or (<= r new-acc) (nil? rest))
          (action event-mapper-2)
          (recur new-acc rest))))))

(def zone-actions-controller
  {:defense [pass duel]
   :midfield [pass duel]
   :attack [pass duel shot]
   :offside [resume-offside]
   :resume [resume-game]
   :goal-out [resume-goal-out]
   :goalkeeper [pass duel]
   :out [resume-out]})
