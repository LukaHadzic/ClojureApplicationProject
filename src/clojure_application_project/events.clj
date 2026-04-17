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
      (let [updated-ball-holder (helpers/new-ball-holder-resume-game state new-possession :attack)]
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
            (update-in [:log new-possession] conj :shot-ball-won)
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
        ball-holder (:ball-holder state)
        goalkeeper (first (get-in state [opp-team :team :players :goalkeeper]))
        ball-holder-id (:id ball-holder)
        goalkeeper-id (:id goalkeeper)]
    (if (helpers/corner? ball-holder goalkeeper)
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:shots-on-goal :shots])
          (helpers/inc-events opp-team goalkeeper-id [:saves])
          (assoc :zone :attack)
          (assoc :phase :corner)
          (update-in [:log opp-team] conj :shot-saved-corner)
          (update-in [:log curr-team] conj :corner))
      (if (helpers/catch? ball-holder goalkeeper) ; Da li ce lopta nakon parade ostati kod golmana ili otici kod nekog u odbrani
        (-> state
            (assoc :possession opp-team)
            (helpers/inc-events curr-team ball-holder-id [:shots-on-goal :shots])
            (assoc :ball-holder (first (get-in state [opp-team :team :players :goalkeeper])))
            (helpers/inc-events opp-team goalkeeper-id [:saves])
            (assoc :zone :goalkeeper)
            (assoc :phase :goalkeeper)
            (update-in [:log opp-team] conj :shot-saved)
            (update-in [:log curr-team] conj :miss))
        (let [{team :team zone :zone player :player opposite :opposite?} (helpers/new-ball-holder-safe state opp-team :defense)]
        (-> state
            (assoc :possession team)
            (helpers/inc-events curr-team ball-holder-id [:shots-on-goal :shots]) ;PROMENJENO umesto team i player curr-team i ball-holder
            (assoc :ball-holder player)
            (helpers/inc-events opp-team goalkeeper-id [:saves])
            (assoc :zone zone)
            (assoc :phase zone)
            (update-in [:log opp-team] conj :shot-saved)
            (update-in [:log curr-team] conj :miss)))))))


(declare get-shot-duration)
(defn shot
  "Shot can be on or off the goal"
  [state]
  (let [ball-holder (:ball-holder state)
        opp-team (helpers/opposite-team (:possession state))
        goalkeeper (first (get-in state [opp-team :team :players :goalkeeper]))
        shot-duration (+ (rand) (get-shot-duration (:zone state)))
        new-state
        (if (helpers/shot-on-goal? ball-holder)
          (if (helpers/shot-saved? ball-holder goalkeeper)
            (shot-saved state)
            (goal state))
          (miss state))]
        ;(if (helpers/shot-saved? ball-holder goalkeeper)
        ;  ;PROMENITI Desava se exception prilikom penala u shot-saved? jer se ne prosledi dobro
        ;  ; ball-holder ili goalkeeper? Ide duel->penalty->NPE Exception
        ;;(if (true? false)
        ;  (shot-saved state) ;PROMENJENO SA OVIM ISPOD SAMO REDOSLED JER AKO JE TRUE U SHOT-SAVED? ONDA SE POZIVA OVA FJA
        ;  (if (helpers/shot-on-goal? ball-holder)
        ;    (goal state)
        ;    (miss state)))]
    (helpers/wrap-return new-state shot-duration)))

;(defn shot1
;  "Shot can be on or off the goal"
;  [state]
;  (prn "\n=== SHOT START ===")
;
;  (let [ball-holder (:ball-holder state)
;        opp-team (helpers/opposite-team (:possession state))
;        goalkeeper (first (get-in state [opp-team :team :players :goalkeeper]))
;        shot-duration (+ (rand) (get-shot-duration (:zone state)))
;
;        _ (prn "Ball-holder:" (:name ball-holder) "ID:" (:id ball-holder))
;        _ (prn "Goalkeeper:" (:name goalkeeper) "ID:" (:id goalkeeper))
;        _ (prn "Zone:" (:zone state))
;        _ (prn "Possession:" (:possession state))
;
;        on-goal? (helpers/shot-on-goal? ball-holder)
;        _ (prn "shot-on-goal? ->" on-goal?)
;
;        new-state
;        (if on-goal?
;          (let [saved? (helpers/shot-saved? ball-holder goalkeeper)
;                _ (prn "shot-saved? ->" saved?)]
;            (if saved?
;              (do
;                (prn "[CALL] shot-saved")
;                (shot-saved state))
;              (do
;                (prn "[CALL] goal")
;                (goal state))))
;          (do
;            (prn "[CALL] miss")
;            (miss state)))
;
;        _ (prn "Shot duration:" shot-duration)
;        _ (prn "=== SHOT END ===\n")]
;
;    (helpers/wrap-return new-state shot-duration)))

(defn update-pass
  [state is-pass-good?]
  (let [curr-team (:possession state)
        ball-holder-id (:id (:ball-holder state))]
    (if is-pass-good?
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:passes :good-passes]))
      (-> state
          (helpers/inc-events curr-team ball-holder-id [:passes])))))

(defn finish-pass
  [state pass-end-team pass-end-zone pass-end-player is-pass-good?]
  (let [ball-holder-id (:id (:ball-holder state))
        curr-team (:possession state)
        new-state (if is-pass-good?
                    (-> state
                        (helpers/inc-events curr-team ball-holder-id [:passes :good-passes])
                        (update-in [:log pass-end-team] conj :pass))
                    (-> state
                        (helpers/inc-events curr-team ball-holder-id [:passes])
                        (update-in [:log pass-end-team] conj :pass-ball-won)
                        (update-in [:log (helpers/opposite-team pass-end-team)] conj :pass-ball-lost)))]
    (-> new-state
        (assoc :possession pass-end-team)
        (assoc :zone pass-end-zone)
        (assoc :phase pass-end-zone)
        (assoc :ball-holder pass-end-player))))

      ;(if (= log-text :pass)
      ;  (let [updated-ball-holder end-player]
      ;    (-> state
      ;        (update-pass 1)
      ;        (assoc :possession team)
      ;        (assoc :zone zone)
      ;        (assoc :phase zone)
      ;        (assoc :ball-holder updated-ball-holder)
      ;        (update-in [:log team] conj :pass)))
      ;  ;(let [new-zone (helpers/opposite-zone state)
      ;  (let [new-zone (helpers/resolve-player-zone (rand-nth (helpers/opposite-zones zone)))
      ;        updated-ball-holder end-player]
      ;    (-> state
      ;        (update-pass 0)
      ;        (assoc :possession team)
      ;        (assoc :zone new-zone)
      ;        (assoc :phase new-zone)
      ;        (assoc :ball-holder updated-ball-holder)
      ;        (update-in [:log team] conj :ball-won)
      ;        (update-in [:log (helpers/opposite-team team)] conj :ball-lost)))))

(declare get-pass-duration)
(defn resume-good-pass
  [state zone-end]
  (let [team (:possession state)
        new-end-zone (if (= :penalty-box zone-end) :attack zone-end)
        end-player (helpers/new-ball-holder-resume-game state team new-end-zone)
        pass-duration (+ (rand) (get-pass-duration :midfield new-end-zone))]
    (helpers/wrap-return (finish-pass state team new-end-zone end-player :pass) pass-duration))) ;PROMENJENO - umesto zone-end stavljeno new-end-zone

(declare get-pass-duration)
(defn handle-good-pass
  ;"Pass is good, choose player from pass-end zone for new ball holder"
  [state zone-end]
  (let [team (:possession state)
        zone-begin (:zone state)
        {pass-end-team :team pass-end-zone :zone
         pass-end-player :player opposite? :opposite?} (helpers/new-ball-holder-safe state team zone-end)]
    (do
      (prn "Pozvao se handle-good-pass, new-holder: " (:name pass-end-player))
      (prn "PASS-END-TEAM: " pass-end-team)
      (prn "PASS-END-ZONE: " pass-end-zone)
      (prn "PASS-END-PLAYER: " (:name pass-end-player))
      (if opposite?
        (helpers/wrap-return (finish-pass state pass-end-team pass-end-zone pass-end-player false) (get-pass-duration zone-begin (helpers/opposite-zone pass-end-zone)))
        (helpers/wrap-return (finish-pass state pass-end-team pass-end-zone pass-end-player true) (get-pass-duration zone-begin pass-end-zone))))))

(declare get-pass-duration)
(defn handle-bad-pass
  [state zone-end]
  (let [opp-team (helpers/opposite-team (:possession state))
        zone-begin (:zone state)
        opp-zone-end (helpers/opposite-zone zone-end)
        {pass-end-team :team pass-end-zone :zone
         pass-end-player :player opposite? :opposite?} (helpers/new-ball-holder-safe state opp-team opp-zone-end)]
    (do
      (prn "Pozvao se handle-bad-pass, new-holder:" (:name pass-end-player))
      (prn "PASS-END-TEAM: " pass-end-team)
      (prn "PASS-END-ZONE: " pass-end-zone)
      (prn "PASS-END-PLAYER: " (:name pass-end-player))
      (if opposite?
        (helpers/wrap-return (finish-pass state pass-end-team pass-end-zone pass-end-player true) (get-pass-duration zone-begin pass-end-zone))
        (helpers/wrap-return (finish-pass state pass-end-team pass-end-zone pass-end-player false) (get-pass-duration zone-begin (helpers/opposite-zone pass-end-zone)))))))

(defn offside
  [state]
  (let [new-possession (helpers/opposite-team (:possession state))
        old-possession (:possession state)
        new-zone :defense
        old-zone :attack
        new-holder (helpers/new-ball-holder-resume-game state new-possession new-zone)
        offside-holder-id (:id (helpers/new-ball-holder-resume-game state old-possession old-zone))]
    (-> state
        (helpers/inc-events old-possession (:id (:ball-holder state)) [:passes])
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
        new-ball-holder (helpers/new-ball-holder-resume-game state new-possession new-zone)]
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
  (do
    (prn "Ulazi se u pass event")
    (prn "PASS BALL-HOLDER: " (:name (:ball-holder state)))
    (prn "PASS ZONE: " (:zone state))
    (prn "PASS PHASE: " (:phase state))
    (if (helpers/offside? (:zone state))
      (do
        (prn "Ulazi se u OFFSIDE u pass event")
        (let [event-duration (+ (rand) (get-pass-duration (:zone state) :attack))
              new-state (offside state)]
          (helpers/wrap-return new-state event-duration)))
      ;(offside state)
      (let [zone-begin (:zone state)
            zone-end (helpers/choose-pass-end-zone zone-begin)
            ball-holder (:ball-holder state)]
        (if (helpers/out? ball-holder)
          (do
            (let [
                  pass-duration (+ (rand) (get-pass-duration zone-begin zone-end))]
              (helpers/wrap-return (out state zone-end) pass-duration)))
          ;(out state zone-end)
          (if (helpers/pass? zone-begin zone-end)
              (handle-good-pass state zone-end)
              (handle-bad-pass state zone-end)))))))

(defn pass-no-offside
  [state]
  ;Zamrsena logika, prvo se bira krajnji igrac pa onda da li
  ; je pass uspesan nezavisno od toga, mora biti povezano.
  ; Desava se da zona i faza postavi nezavisno od ball-holder
  ; jer se ball-holder i zone-end bira na jedan nacin, a sa good/bad-pass
  ; postavlja phase i zone na drugi nacin, drugom logikom -> Srediti
  ; Za pocetak promeniti logiku good/bad-pass, argumente koje salju
  ; funkciji finish-pass, i srediti funkciju finish-pass sa
  ; novim argumentima
  ;(prn "PASS BALL HOLDER:" (:ball-holder state))
  (let [zone-begin (:zone state)
        curr-team (:possession state)
        zone-end (helpers/choose-pass-end-zone zone-begin)
        ball-holder (:ball-holder state)]
    (if (and (= (:phase state) :out)
         (helpers/out? ball-holder))
      (let [pass-duration (+ (rand) (get-pass-duration zone-begin zone-end))]
        (helpers/wrap-return (out state zone-end) pass-duration))
      (if (helpers/pass? zone-begin zone-end)
        (handle-good-pass state zone-end)
        (handle-bad-pass state zone-end)))))
      ;(let [new-state
      ;      (if (helpers/pass? zone-begin zone-end)
      ;        (-> state
      ;            (good-pass zone-end end-player))
      ;            ;(assoc :phase zone-end))
      ;        (-> state
      ;            (bad-pass zone-end end-player)))]
      ;            ;(assoc :phase (helpers/opposite-zone zone-end))))]
      ;  (helpers/wrap-return new-state pass-duration)))))

(defn set-new-goalkeeper
  [state team]
  (let [new-goalkeeper (helpers/choose-pl-max-attr
                 (helpers/players-in-zones
                   state team [:defense :midfield :attack]) :goal-keeping)
        new-goalkeeper-id (:id new-goalkeeper)]
    (-> state
        (update-in [team :team :players :goalkeeper] conj new-goalkeeper)
        (helpers/remove-from-zone team [:defense :midfield :attack] new-goalkeeper-id))))

(defn kick-player
  [state team player-id]
  (let [positions [:goalkeeper :defense :midfield :attack]
        players-positions (get-in state [team :team :players])
        player (some (fn [pos]
                       (some #(when (= (:id %) player-id) %) (pos players-positions)))
                positions)
        goalkeeper? (= player-id (:id (first (get-in state [team :team :players :goalkeeper]))))]
    (-> state
        (update-in [team :team :kicked-players] conj player)
        ;(reduce (fn [st pos]
        ;          (update-in st [team :team :players pos]
        ;                     #(vec (remove (fn [p] (= (:id p) player-id)) %))))
        ;        s
        ;        positions))
        (helpers/remove-from-zone team positions player-id)
        (cond->
          goalkeeper? (set-new-goalkeeper team)))))

(defn get-card
  [state team player card]
  (let [player-id (get player :id)]
    ;(if (< (rand) 0.01)
    (if (= card :red)
      (if (= (get player :yellow-cards) 1)
        (-> state
            (helpers/inc-events team player-id [:yellow-cards])
            (helpers/inc-events team player-id [:red-card])
            (update-in [:log team] conj :yellow-card)
            (update-in [:log team] conj :red-card)
            (kick-player team player-id))
        (-> state
            (helpers/inc-events team player-id [:red-card])
            (update-in [:log team] conj :red-card)
            (kick-player team player-id)))
      ;(if (and (> (rand) 0.01) (< (rand) 0.15))
      (if (= card :yellow)
        (if (= (get player :yellow-cards) 1)
          (-> state
              (helpers/inc-events team player-id [:yellow-cards])
              (helpers/inc-events team player-id [:red-card])
              (update-in [:log team] conj :yellow-card)
              (update-in [:log team] conj :red-card)
              (kick-player team player-id))
          (-> state
              (helpers/inc-events team player-id [:yellow-cards])
              (update-in [:log team] conj :yellow-card)))
        state))))

(defn update-duel
  [state is-duel-won opp-player]
  (let [curr-team (:possession state)
        opp-team (helpers/opposite-team curr-team)
        ball-holder-id (:id (:ball-holder state))
        opp-player-id (:id opp-player)]
    (if is-duel-won
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
  [state is-duel-won opp-player cross-next-zone?]
  (if is-duel-won
    (let [current-team (:possession state)
          diff-team (helpers/opposite-team current-team)]
      (if cross-next-zone?
        ;(if (<= (rand-int 3) -1)
        ;(if (> (helpers/closer-value-to-first? (:speed ball-holder) (:speed opp-player)))
        (-> state
            ;(update-duel true opp-player)
            (helpers/inc-events current-team (:id (:ball-holder state)) [:duels :duels-won :crosses])
            (helpers/inc-events diff-team (:id opp-player) [:duels])
            (update-in [:log current-team] conj :duel-won :cross) ;PROMENJENO da se upise i :cross
            (update-in [:log diff-team] conj :duel-lost)
            (assoc :zone (helpers/next-zone (:zone state)))
            (assoc :phase (helpers/next-zone (:zone state))))
        (-> state
            ;(update-duel true opp-player)
            (helpers/inc-events current-team (:id (:ball-holder state)) [:duels :duels-won])
            (helpers/inc-events diff-team (:id opp-player) [:duels])
            (update-in [:log current-team] conj :duel-won)
            (update-in [:log diff-team] conj :duel-lost))))
    (let [old-possession (:possession state)
          new-possession (helpers/opposite-team old-possession)
          prev-ball-holder-id (:id (:ball-holder state))
          ;new-zone (helpers/opposite-zone state)]
          new-zone (helpers/opposite-zone (:zone state))]
      (-> state
          (assoc :possession new-possession
                 :ball-holder opp-player)
          ;(update-duel false opp-player)
          (helpers/inc-events new-possession (:id opp-player) [:duels :duels-won])
          (helpers/inc-events old-possession prev-ball-holder-id [:duels])
          (update-in [:log old-possession] conj :duel-lost)
          (update-in [:log new-possession] conj :duel-won)
          (assoc :zone new-zone)
          (assoc :phase new-zone)))))

(defn duel-won
  [state opp-player cross-next-zone?]
  (finish-duel state true opp-player cross-next-zone?))

(defn duel-lost
  [state opp-player]
  (finish-duel state false opp-player false))

(def event-mapper
  {:shot shot
   :pass pass})

(defn resume-penalty
  [state]
  (let [zone (:zone state)
        event (event-mapper (helpers/choose-resume-event zone))]
    (-> state
        (assoc :ball-holder (helpers/choose-pl-for-event state event))
        (event))))

(defn resume-corner
  [state]
  (let [event (event-mapper (helpers/choose-resume-event :corner))]
    (-> state
        (assoc :ball-holder (helpers/choose-pl-for-event state event))
        (event))))

(defn resume-foul
  [state]
  (let [zone (:zone state)
        event (event-mapper (helpers/choose-resume-event zone))]
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
      (let [{should-get-card :get-card? card :card}
            (helpers/should-get-card? opp-player ball-holder)
            new-state (if should-get-card
                        (get-card state curr-team ball-holder card)
                        state)]
        (-> new-state
            (assoc :ball-holder opp-player)
            (helpers/inc-events curr-team (:id ball-holder) [:fouls :duels])
            (helpers/inc-events opp-team (:id opp-player) [:duels :duels-won])
            (assoc :possession opp-team)
            (assoc :zone new-zone)
            (assoc :phase :foul)
            (update-in [:log curr-team] conj :foul)
            (update-in [:log opp-team] conj :fouled)))
      (let [{should-get-card :get-card? card :card}
            (helpers/should-get-card? ball-holder opp-player)
            new-state (if should-get-card
                        (get-card state opp-team opp-player card)
                        state)]
        (if (helpers/penalty? new-state)
            (-> new-state
                (helpers/inc-events curr-team (:id ball-holder) [:duels :duels-won])
                (helpers/inc-events opp-team (:id opp-player) [:duels :fouls])
                (assoc :phase :penalty)
                (update-in [:log curr-team] conj :fouled-penalty)
                (update-in [:log opp-team] conj :foul-penalty))
            (-> new-state
                (helpers/inc-events curr-team (:id ball-holder) [:duels :duels-won])
                (helpers/inc-events opp-team (:id opp-player) [:duels :fouls])
                (assoc :phase :foul)
                (update-in [:log curr-team] conj :fouled)
                (update-in [:log opp-team] conj :foul)))))))

(declare get-duel-duration)
(declare get-cross-duration)
(defn duel
  [state]
  (let [ball-holder (:ball-holder state)
        current-team (:possession state)
        opp-team (helpers/opposite-team (:possession state))
        opp-zone (helpers/opposite-zone (:zone state))
        opp-player (helpers/rand-player state opp-team opp-zone)]
    ;(if (helpers/foul? ball-holder opp-player)
    (if opp-player
      (let [duel-duration (+ (rand 0.5) (get-duel-duration ball-holder opp-player))
            cross-next-zone? (helpers/cross-next-zone? ball-holder opp-player)]
        (if (helpers/foul? ball-holder opp-player)
          (helpers/wrap-return (foul state opp-player) duel-duration)
          (if (helpers/duel-won? ball-holder opp-player)
            (let [cross-duration (get-cross-duration ball-holder)
                  total-duel-duration (if cross-next-zone?
                                        (+ duel-duration cross-duration)
                                        duel-duration)]
              (helpers/wrap-return (duel-won state opp-player cross-next-zone?) total-duel-duration))
            (helpers/wrap-return (duel-lost state opp-player) duel-duration))))
      (let [duel-duration (get-cross-duration ball-holder)
            next-zone (helpers/next-zone (:zone state))]
        (helpers/wrap-return
          (-> state
              (update-in [:log current-team] conj :cross)
              (assoc :zone next-zone)
              (assoc :phase next-zone)
              (helpers/inc-events current-team (:id ball-holder) [:crosses]))
          duel-duration)))))

(defn resume-game
  [state]
  (resume-good-pass state (helpers/choose-pass-end-zone :midfield)))

(defn resume-goal-out
  [state]
  (pass (assoc state :phase :goalkeeper)))

(defn resume-offside
  [state]
  (pass (assoc state :phase :defense)))

(defn resume-out
  [state]
  (pass-no-offside state))

(def pass-duration-map
  {:goalkeeper {:defense 1
                :midfield 3.5
                :attack 5
                :penalty-box 5.5
                :goalkeeper 5.7} ; For opposite :goalkeeper zone
   :defense {:goalkeeper 1.5
             :defense 1
             :midfield 1.5
             :attack 3
             :penalty-box 3.5}
   :midfield {:goalkeeper 3.5
              :defense 1.5
              :midfield 1
              :attack 1.5
              :penalty-box 1.5}
   :attack {:goalkeeper 5
            :defense 3
            :midfield 1
            :attack 0.5
            :penalty-box 0.5}
   :penalty-box {:goalkeeper 5.5
                 :defense 3.5
                 :midfield 1.5
                 :attack 0.5
                 :penalty-box 0.5}})

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

(defn get-cross-duration
  [ball-holder]
  (let [speed (:speed ball-holder)
        speed-factor (- 1.0 (/ speed 200))
        base 3
        r (rand)]
    (+ (* speed-factor base) r)))

(def shot-duration-map
  {:defense 3
   :midfield 1.8
   :attack 1
   :penalty-box 0.5})

(def event-mapper-2
  {:shot shot
   :pass pass
   :duel duel
   :resume-offside resume-offside
   :resume-game resume-game
   :resume-goal-out resume-goal-out
   :resume-out resume-out
   :resume-foul resume-foul
   :resume-corner resume-corner
   :resume-penalty resume-penalty})

(defn get-pass-duration
  [zone-begin zone-end]
  (get-in pass-duration-map [zone-begin zone-end]))

(defn get-shot-duration
  [zone-begin]
  (get-in shot-duration-map [zone-begin]))

(def phase-actions-controller
  {
   ;:goalkeeper {:pass 0.96 :duel 0.04}
   ;:defense {:pass 0.7 :duel 0.25 :shot 0.05}
   ;:midfield {:pass 0.4 :duel 0.4 :shot 0.2}
   ;:attack {:pass 0.35 :duel 0.35 :shot 0.3}
   ;:penalty-box {:pass 0.1 :duel 0.5 :shot 0.4}
   :goalkeeper  {:pass 0.98 :duel 0.02}
   :defense     {:pass 0.85 :duel 0.14 :shot 0.01}
   :midfield    {:pass 0.65 :duel 0.33 :shot 0.02}
   :attack      {:pass 0.45 :duel 0.45 :shot 0.10}
   :penalty-box {:pass 0.20 :duel 0.45 :shot 0.35}
   :offside {:resume-offside 1.0}
   :resume {:resume-game 1.0}
   :goal-out {:resume-goal-out 1.0}
   :out {:resume-out 1.0}
   :foul {:resume-foul 1.0}
   :corner {:resume-corner 1.0}
   :penalty {:resume-penalty 1.0}})

(def zone-lambda-map
  {:goalkeeper 0.1
   :defense 0.2
   :midfield 0.25
   :attack 0.35
   :penalty-box 0.5})

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
