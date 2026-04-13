(ns clojure-application-project.helpers)

(defn make-player
  [id name skill
   goal-keeping defense passing attack
   handling reflexes positioning
   technique shot-power finishing
   strength speed]
  {:id id :name name :skill skill
   :goal-keeping goal-keeping :defense defense :passing passing :attack attack
   :handling handling :reflexes reflexes :positioning positioning
   :technique technique :shot-power shot-power :finishing finishing
   :strength strength :speed speed
   :saves 0 :passes 0 :good-passes 0
   :shots 0 :shots-on-goal 0 :goals 0
   :duels 0 :duels-won 0 :crosses 0
   :offsides 0
   :fouls 0 :yellow-cards 0 :red-card 0})

(defn count-players
  [zone players-and-positions]
  (count (zone players-and-positions)))

(defn make-team
  [name players-and-positions]
  {:name name :formation {:goalkeeper (count (get players-and-positions :goalkeeper))
                          :defense (count (get players-and-positions :defense))
                          :midfield (count (get players-and-positions :midfield))
                          :attack (count (get players-and-positions :attack))}
   :players players-and-positions :kicked-players {}})

(defn make-match
  [home away]
  (let [state {:home {:team home :goals 0}
               :away {:team away :goals 0}
               :minute 0
               :time 0
               :possession :home
               :zone :midfield ;PROMENJENO UMESTO :attack da bude :midfield
               :phase :resume
               :ball-holder {}
               :log {:home [] :away []}}
        state (assoc state :ball-holder (rand-nth (get-in state [(:possession state) :team :players :attack])))]
    state))

(defn make-match-debug
  [home away]
  (let [state {:home {:team home :goals 0}
               :away {:team away :goals 0}
               :minute 0
               :time 0
               :possession :home
               :zone :attack
               :ball-holder {}
               :log {:home [] :away []}}
        state (assoc state :ball-holder (rand-nth (get-in state [(:possession state) :team :players :attack])))]
    state))

(defn wrap-return
  [new-state event-duration]
  {:new-state new-state
   :event-duration event-duration})

(defn calc-avg
  [val-1 val-2]
  (/ (+ val-1 val-2) 2))

(defn closer-value-to-first?
  [value-1 value-2]
  ; Nije dobro, sta ako je val-1 vece od val-2
  (let [total (+ value-1 value-2)
        r (rand-int total)
        in-order? (< value-1 value-2)]
    (if in-order?
      (if (< r value-1)
        true
        (if (> r value-2)
          false
          (if (< (- r value-1) (- value-2 r))
            true
            false)))
      (if (< r value-2)
        false
        (if (> r value-1)
          true
          (if (< (- r value-2) (- value-1 r))
            false
            true))))))

(defn opposite-team
  [team]
  (if (= team :home) :away :home))

(def opposite-zones-map
  {:goalkeeper [:attack :penalty-box]
   :defense [:attack]
   :attack [:defense]
   :midfield [:midfield]
   :penalty-box [:defense :goalkeeper]}) ; :penalty-box <-> :goalkeeper

(def next-zone-map
  {:goalkeeper :defense
   :defense :midfield
   :midfield :attack
   :attack :penalty-box
   :penalty-box :penalty-box}) ; :attack <-> :penalty-box

(def pure-opp-zone-map
  {:goalkeeper :attack
   :defense :attack
   :attack :defense
   :midfield :midfield
   :penalty-box :goalkeeper})

(def zone-player-zone-mapper
  {:goalkeeper :goalkeeper
   :defense :defense
   :midfield :midfield
   :attack :attack
   :penalty-box :attack})

(defn next-zone
  [curr-zone]
  (get next-zone-map curr-zone))

(defn prev-zone
  [curr-zone]
  (some (fn [[k v]]
          (when (and (= v curr-zone) (not= k v))
            k))
        next-zone-map))

(def zones-ind
  {:goalkeeper 0
   :defense 1
   :midfield 2
   :attack 3
   :penalty-box 4})

(declare opposite-zone)
(defn forward?
  [zone-begin zone-end opposite?]
  (let [zone-end-nom (if opposite? (opposite-zone zone-end) zone-end)]
    (if (= zone-begin nil)
      (println "ZONE BEGIN JE NIL")
      (if (= zone-end nil)
        (println "ZONE END JE NIL")
        (>= (get zones-ind zone-end-nom) (get zones-ind zone-begin))))))


(defn last-zone?
  [zone is-forward opposite?]
  (if opposite?
    (if is-forward
      (= zone :goalkeeper)
      (= zone :penalty-box))
    (if is-forward
      (= zone :penalty-box)
      (= zone :goalkeeper))))


(defn opposite-zones
  [zone]
  (zone opposite-zones-map))

(defn opposite-zone
  [zone]
  (first (zone opposite-zones-map)))

(defn rand-zone
  []
  (rand-nth [:goalkeeper :defense :midfield :attack]))
;
;(defn new-ball-holder
;  [state]
;  (rand-nth (get-in state [(:possession state) :team :players (:zone state)])))

(defn resolve-player-zone
  [zone]
  (get zone-player-zone-mapper zone))

(defn players-in-zones
  [state team zones]
  (mapcat #(get-in state [team :team :players %]) (distinct (map resolve-player-zone zones))))

(defn remove-from-zone
  [state team zones player-id]
  (reduce (fn [st pos]
            (update-in st [team :team :players pos]
                       #(vec (remove (fn [p] (= (:id p) player-id)) %))))
          state
          zones))

(defn rand-opposite-player
  [state]
  (let [curr-team (:possession state)
        curr-zone (:zone state)
        team (opposite-team curr-team)
        zone (resolve-player-zone (rand-nth (get opposite-zones-map curr-zone)))]
  (rand-nth (get-in state [team :team :players zone]))))

(defn rand-player
  [state team zone]
  (let [ind (rand-int (+ 1 (get-in state [team :team :formation zone])))
        players-in-zone (get-in state [team :team :players (resolve-player-zone zone)])]
    (nth players-in-zone ind nil)))

(defn new-ball-holder-resume-game
  [state team zone]
  (let [zone (resolve-player-zone zone)
        players (filter #(not= % (:ball-holder state))
                        ;(distinct
                        ;  (players-in-zones
                        ;    state team (map resolve-player-zone zones-seq)))
                        (get-in state [team :team :players zone]))]
    (if (seq players)
      (rand-nth players)
      (new-ball-holder-resume-game state team (prev-zone zone)))))

;Problem je sto new-ball-holder-safe moze da izabere golmana ponovo - kako?
;Problem je sto moze da bude IndexOutOfBoundsException - kako?
  ;Na tragu: kada se doda golmanu? -> Ostaje samo ovo resiti jos
;Ovde je problem sa next-zone-temp iz koje se bira sledeci,
; a isto tako i u timu koji prosledjujemo za sledecu iteraciju
; rekurzije
(declare new-ball-holder)
(defn new-ball-holder-safe
  [state team zone]
  (prn "NBHS pocetak" team zone)
  (let [player (new-ball-holder state team zone)
        opposite? (not= (:possession state) team)
        is-forward (forward? (:zone state) zone opposite?)
        next-zone-temp
          (if opposite?
            (if is-forward
              (prev-zone zone)
              (next-zone zone))
            (if is-forward
              (next-zone zone)
              (prev-zone zone)))]
    ;(prn "NBHS: player" player)
    (prn "NBHS: is-forward" is-forward)
    ;(prn "NBHS: next-zone-temp" next-zone-temp)
    (if player
      {:team team :zone zone :player player :opposite? opposite?} ;PROMENJENO Umesto :opposite? false stavljeno :opposite? opposite?
      (let [opp-team (opposite-team team)
            opp-zone (resolve-player-zone (get pure-opp-zone-map zone))
            opp-player (rand-player state opp-team opp-zone)]
        ;(prn "NBHS: opp-team" opp-team)
        ;(prn "NBHS: opp-zone" opp-zone)
        ;(prn "NBHS: opp-player" opp-player)
        (if opp-player
          (do
            {:team opp-team :zone opp-zone :player opp-player :opposite? true})
        ;Mozda proslediti opp-zone a ne zone?
          ;Treba staviti guard-ove za end-zones da
          ; ne moze sledeca da bude nil, uvek
          ; mora da se zavrsi u :goalkeeper najdalje
          (if (last-zone? zone is-forward opposite?)
            ;FORCED PICK
            (let [from-team (if opposite?
                              (if is-forward
                                team
                                opp-team)
                              (if is-forward
                                opp-team
                                team))]
              (do
                (prn "Ulazi se u forced pick")
                (let [forced-opp-player (rand-nth (players-in-zones state from-team [:goalkeeper]))]
                  (prn "Odabrano je:" forced-opp-player)
                  {:team from-team :zone :goalkeeper :player forced-opp-player :opposite? true})))

            ;RADI
            ;(do
            ;  (prn "Ulazi se u forced pick")
            ;  (let [forced-opp-player (rand-nth (players-in-zones state opp-team [opp-zone]))]
            ;    (prn "Odabrano je:" forced-opp-player)
            ;    {:team opp-team :zone opp-zone :player forced-opp-player :opposite? true}))
            ;RADI
            (new-ball-holder-safe state team next-zone-temp)))))))

          ;(new-ball-holder-safe state team next-zone-temp)))))))

(defn new-ball-holder
  "Does not allow player to pass himself"
  [state team zones]
  (let [zones-seq (if (coll? zones) zones [zones])
        zone (resolve-player-zone (rand-nth zones-seq))
        players (filter #(not= (:id %) (:id (:ball-holder state)))
                        ;(distinct
                        ;  (players-in-zones
                        ;    state team (map resolve-player-zone zones-seq)))
                        (get-in state [team :team :players zone]))]
    (when (seq players)
      ;(rand-nth players))))
      (rand-nth (conj players nil)))))


(defn new-ball-holder-2
  "Give ball possession to another player"
  [state team zone]
  (if (= zone :offside)
    (rand-nth (get-in state [team :team :players :defense]))
    (rand-nth (get-in state [team :team :players zone]))))

(defn get-team-players
  [team]
    (apply concat (vals (:players (:team team)))))

(def realiz-possib-map
  {:goalkeeper {:pass 1}
   :defense {:pass 1}
   :midfield {:shot 0.3
              :pass 0.7}
   :attack {:pass 0.25
            :shot 0.75}
   :corner {:shot 0.05
            :pass 0.95}
   :penalty-box {:pass 0.01
                 :shot 0.99}})

(defn choose-pl-max-attr
  [players attr]
  (loop [max-attr 0
         max-player nil
         [p & rest] players]
    (if (nil? p)
      max-player
      (let [val-attr (attr p)]
        (if (> val-attr max-attr)
          (recur val-attr p rest)
          (recur max-attr max-player rest))))))
  ;(apply max-key attr players))

(defn choose-pl-for-event
  [state event]
  (let [curr-team (:possession state)
        curr-zone (:zone state)
        zone-before (prev-zone curr-zone)
        players (concat (get-in state [curr-team :team :players curr-zone])
                        (get-in state [curr-team :team :players zone-before]))]
  ;(if (= event :out)
  (if (= event :shot)
    (choose-pl-max-attr players :finishing)
    (choose-pl-max-attr players :passing))))
    ;(choose-pl-max-attr players :skill))))

(defn choose-resume-event
  [zone]
  (let [r (rand)
        actions-probs (zone realiz-possib-map)]
    (loop [acc 0
           [[action prob] & rest] actions-probs]
      (let [new-acc (+ prob acc)]
        (if (< r new-acc)
          action
          (recur new-acc rest))))))

;(def zone-event-probs
;  {:goalkeeper {:pass 0.9 :duel 0.1}
;   :defense {:pass 0.7 :duel 0.25 :shot 0.05}
;   :defense {:pass 0.7 :duel 0.25 :shot 0.05}})

(def pass-possibilities
  {:goalkeeper {:attack 0.1
                :defense 0.7
                :midfield 0.2}
   :defense {:goalkeeper 0.25
             :defense 0.30
             :midfield 0.35
             :attack 0.1}
   :midfield {:goalkeeper 0.2
              :defense 0.3
              :midfield 0.3
              :attack 0.15
              :penalty-box 0.05}
   :attack {:goalkeeper 0.02
            :defense 0.08
            :midfield 0.45
            :attack 0.25
            :penalty-box 0.2}
   :penalty-box {:attack 0.5
                 :penalty-box 0.5}})

(def good-pass-possibilities
  {:goalkeeper {:attack 0.2
                :midfield 0.75
                :defense 0.9}
   :defense {:attack 0.2
             :midfield 0.75
             :defense 0.9
             :goalkeeper 0.95}
   :midfield {:goalkeeper 0.9
              :defense 0.9
              :midfield 0.7
              :attack 0.4
              :penalty-box 0.05}
   :attack {:goalkeeper 0.7
            :defense 0.5
            :midfield 0.6
            :attack 0.33
            :penalty-box 0.15}
   :penalty-box {:attack 0.15
                 :penalty-box 0.15}})

(defn choose-pass-end-zone
  [from-zone]
  (let [targets (->> (from-zone pass-possibilities)
                     (sort-by val))
        r (rand)]
    (loop [acc 0
           [[zone prob] & rest] targets]
      (let [new-acc (+ acc prob)]
        (if (< r new-acc)
          zone
          (recur new-acc rest))))))

(defn get-goal-prob
  [delta]
  (if (> delta 20)
    0.9
    (if (> delta 0)
      0.6
      (if (> delta -20)
        0.3
        0.1))))

(defn shot-saved?
  [shooter goalkeeper]
  (let [shot-quality  (+ (* 0.5 (:finishing shooter))
                         (* 0.3 (:technique shooter))
                         (* 0.2 (:shot-power shooter)))
        save-quality  (+ (* 0.5 (:positioning goalkeeper))
                         (* 0.3 (:reflexes goalkeeper))
                         (* 0.2 (:handling goalkeeper)))
        save-prob  (- 1 (get-goal-prob (- shot-quality save-quality)))
        r (rand)]
    (< r save-prob)))

(defn goal?
  [shooter]
  (let [shot-quality  (+ (* 0.5 (:finishing shooter))
                         (* 0.3 (:technique shooter))
                         (* 0.2 (:shot-power shooter)))
        goal-prob (get-goal-prob (/ shot-quality 4))
        r (rand)]
    (< r goal-prob)))

(defn pass?
  [zone-begin zone-end]
  (let [r (rand)
        prob (get-in good-pass-possibilities [zone-begin zone-end])]
    (< r prob)))

(def offside-chances
  {:goalkeeper 0.2
   :defense 0.15
   :midfield 0.1
   :attack 0.2
   :penalty-box 0.3})

(defn offside?
  [zone-from]
  (let [r (rand)
        off-chance (get offside-chances zone-from)]
  (< r off-chance)))

(defn out?
  [ball-holder]
  (let [r (rand-int 100)
        pass-skill (:passing ball-holder)]
    (> r pass-skill)))

(defn duel-won?
  [ball-holder opp-player]
  (closer-value-to-first?
     (calc-avg (:strength ball-holder) (:speed ball-holder))
     (calc-avg (:strength opp-player) (:speed opp-player))))

(defn cross-next-zone?
  [ball-holder opp-player]
  (let [cross-quality  (+ (* 0.5 (:speed ball-holder))
                         (* 0.3 (:technique ball-holder))
                         (* 0.2 (:strength ball-holder)))
        defense-quality  (+ (* 0.5 (:speed opp-player))
                         (* 0.3 (:technique opp-player))
                         (* 0.2 (:strength opp-player)))
        corner-prob  (get-goal-prob (- cross-quality defense-quality))
        r (rand)]
    (< r corner-prob)))

(defn foul-attack?
  "Is attacking player made foul or it was defending player?"
  [ball-holder opp-player]
  (let [ball-holder-stgh (:strength ball-holder)
        opp-player-stgh (:strength opp-player)]
    (closer-value-to-first? ball-holder-stgh opp-player-stgh)))

(defn foul?
  "Greater the strength difference between players, greater the chance for foul."
  [ball-holder opp-player]
  (let [r (rand-int 101)
        ball-holder-stgh (:strength ball-holder)
        opp-player-stgh (:strength opp-player)]
    (if (>= ball-holder-stgh opp-player-stgh)
      (and (> r opp-player-stgh) (< r ball-holder-stgh))
      (and (< r opp-player-stgh) (> r ball-holder-stgh)))))

(defn penalty?
  [state]
  (= :penalty-box (get state :zone)))

(defn corner?
  [shooter goalkeeper]
  (let [shot-quality  (+ (* 0.5 (:finishing shooter))
                         (* 0.3 (:technique shooter))
                         (* 0.2 (:shot-power shooter)))
        save-quality  (+ (* 0.5 (:positioning goalkeeper))
                         (* 0.3 (:reflexes goalkeeper))
                         (* 0.2 (:handling goalkeeper)))
        corner-prob  (get-goal-prob (- shot-quality save-quality))
        r (rand)]
    (< r corner-prob)))

(defn catch?
  [shooter goalkeeper]
  (let [shot-quality  (+ (* 0.5 (:finishing shooter))
                         (* 0.3 (:technique shooter))
                         (* 0.2 (:shot-power shooter)))
        save-quality  (+ (* 0.5 (:positioning goalkeeper))
                         (* 0.3 (:reflexes goalkeeper))
                         (* 0.2 (:handling goalkeeper)))
        catch-prob  (- 1 (get-goal-prob (- shot-quality save-quality)))
        r (rand)]
    (< r catch-prob)))

(defn inc-events
  [state team player-id events]
  (update-in state [team :team :players]
             (fn [player-pos-map]
               (update-vals player-pos-map
                            (fn [players]
                              (map (fn [p]
                                     (if (= (:id p) player-id)
                                       (reduce (fn [updated-p event]
                                                 (update updated-p event inc))
                                               p events)
                                       p))
                                   players))))))

(defn get-card-prob
  [delta]
  (if (> delta 20)
    {:red 0.15
     :yellow 0.35}
    (if (> delta 0)
      {:red 0.07
       :yellow 0.15}
      (if (> delta -20)
        {:red 0.02
         :yellow 0.1}
        {:red 0.005
         :yellow 0.05}))))

(defn should-get-card?
  [fouled-player player]
  ;Vratiti i koji karton dobija!
  (let [foul-score (- (+ (* 0.6 (:strength player))
                         (* -0.25 (:speed player))
                         (* -0.15 (:technique player)))
                      (+ (* 0.6 (:strength fouled-player))
                         (* -0.25 (:speed fouled-player))
                         (* -0.15 (:technique fouled-player))))
        {red-card-prob :red yellow-card-prob :yellow} (get-card-prob foul-score)
        r (rand)]
    (if (<= r red-card-prob)
      {:get-card? true :card :red}
      (if (<= r yellow-card-prob)
        {:get-card? true :card :yellow}
        {:get-card? false :card nil}))))

(defn count-event
  [team event]
  (reduce + (map event (apply concat (vals (get-in team [:team :players]))))))

;(helpers/count-duels {:team {:name "Barcelona",
;                             :players {:goalkeeper [{:good-passes 0,
;                                                     :skill 87,
;                                                     :goals 0,
;                                                     :duels-won 0,
;                                                     :name "Victor Valdes",
;                                                     :passes 0,
;                                                     :shots 0,
;                                                     :id 12,
;                                                     :duels 0}],
;                                       :defense [{:good-passes 1,
;                                                  :skill 85,
;                                                  :goals 0,
;                                                  :duels-won 0,
;                                                  :name "Dani Alves",
;                                                  :passes 1,
;                                                  :shots 0,
;                                                  :id 13,
;                                                  :duels 3}
;                                                 {:good-passes 1,
;                                                  :skill 87,
;                                                  :goals 0,
;                                                  :duels-won 0,
;                                                  :name "Gerard Pique",
;                                                  :passes 1,
;                                                  :shots 0,
;                                                  :id 14,
;                                                  :duels 2}
;                                                 {:good-passes 0,
;                                                  :skill 85,
;                                                  :goals 0,
;                                                  :duels-won 0,
;                                                  :name "Javier Mascherano",
;                                                  :passes 0,
;                                                  :shots 0,
;                                                  :id 15,
;                                                  :duels 1}
;                                                 {:good-passes 1,
;                                                  :skill 83,
;                                                  :goals 0,
;                                                  :duels-won 0,
;                                                  :name "Jordi Alba",
;                                                  :passes 2,
;                                                  :shots 0,
;                                                  :id 16,
;                                                  :duels 0}],
;                                       :midfield [{:good-passes 0,
;                                                   :skill 87,
;                                                   :goals 0,
;                                                   :duels-won 2,
;                                                   :name "Sergio Busquets",
;                                                   :passes 0,
;                                                   :shots 0,
;                                                   :id 17,
;                                                   :duels 3}
;                                                  {:good-passes 2,
;                                                   :skill 90,
;                                                   :goals 0,
;                                                   :duels-won 4,
;                                                   :name "Xavi",
;                                                   :passes 2,
;                                                   :shots 0,
;                                                   :id 18,
;                                                   :duels 4}
;                                                  {:good-passes 1,
;                                                   :skill 91,
;                                                   :goals 0,
;                                                   :duels-won 0,
;                                                   :name "Andres Iniesta",
;                                                   :passes 2,
;                                                   :shots 0,
;                                                   :id 19,
;                                                   :duels 0}],
;                                       :attack [{:good-passes 0,
;                                                 :skill 94,
;                                                 :goals 1,
;                                                 :duels-won 1,
;                                                 :name "Lionel Messi",
;                                                 :passes 0,
;                                                 :shots 1,
;                                                 :id 20,
;                                                 :duels 1}
;                                                {:good-passes 0,
;                                                 :skill 85,
;                                                 :goals 1,
;                                                 :duels-won 0,
;                                                 :name "Pedro",
;                                                 :passes 0,
;                                                 :shots 1,
;                                                 :id 21,
;                                                 :duels 0}
;                                                {:good-passes 0,
;                                                 :skill 87,
;                                                 :goals 1,
;                                                 :duels-won 1,
;                                                 :name "Neymar",
;                                                 :passes 0,
;                                                 :shots 1,
;                                                 :id 22,
;                                                 :duels 1}]
;                                       }},
;                      :goals 3})