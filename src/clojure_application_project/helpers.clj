(ns clojure-application-project.helpers)

(defn make-player
  [id name skill]
  {:id id :name name :skill skill
   :goal-keeping 96 :defense 96 :passing 96 :strength 96 :finishing 96 :attack 96 :speed 96
   :saves 0 :passes 0 :good-passes 0
   :shots 0 :shots-on-goal 0 :goals 0 :duels 0 :duels-won 0 :offsides 0})

(defn make-team
  [name players-and-positions]
  {:name name :players players-and-positions})

(defn make-match
  [home away]
  (let [state {:home {:team home :goals 0}
               :away {:team away :goals 0}
               :minute 0
               :possession :home
               :zone :attack
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
               :possession :home
               :zone :attack
               :ball-holder {}
               :log {:home [] :away []}}
        state (assoc state :ball-holder (rand-nth (get-in state [(:possession state) :team :players :attack])))]
    state))

(defn calc-avg
  [val-1 val-2]
  (/ (+ val-1 val-2) 2))

(defn opposite-team
  [team]
  (if (= team :home) :away :home))

(def opposite-zone-map
  {:goalkeeper :attack
   :defense :attack
   :attack :defense
   :midfield :midfield})

(def next-zone-map
  {:goalkeeper :defense
   :defense :midfield
   :midfield :attack
   :attack :attack})

(defn next-zone
  [curr-zone]
  (get next-zone-map curr-zone))

(defn opposite-zone
  [zone]
  (zone opposite-zone-map))

(defn rand-zone
  []
  (rand-nth [:goalkeeper :defense :midfield :attack]))
;
;(defn new-ball-holder
;  [state]
;  (rand-nth (get-in state [(:possession state) :team :players (:zone state)])))

(defn rand-opposite-player
  [state]
  (let [curr-team (:possession state)
        curr-zone (:zone state)]
  (rand-nth (get-in state [(opposite-team curr-team) :team :players (opposite-zone curr-zone)]))))

(defn new-ball-holder
  "Does not allow player to pass himself"
  [state team zone]
  (rand-nth (filter #(not= % (:ball-holder state))
                    (get-in state [team :team :players zone]))))

(defn new-ball-holder-2
  "Give ball possession to another player"
  [state team zone]
  (if (= zone :offside)
    (rand-nth (get-in state [team :team :players :defense]))
    (rand-nth (get-in state [team :team :players zone]))))

(defn get-team-players
  [team]
    (apply concat (vals (:players (:team team)))))

(defn goal?
  [player]
  (> (:skill player) (rand-int 101)))

(def pass-possibilities
  {:goalkeeper {:defense 0.7
                :midfield 0.2
                :attack 0.1}
   :defense {:goalkeeper 0.25
             :defense 0.30
             :midfield 0.35
             :attack 0.1}
   :midfield {:goalkeeper 0.2
              :defense 0.3
              :midfield 0.3
              :attack 0.2}
   :attack {:goalkeeper 0.02
            :defense 0.08
            :midfield 0.45
            :attack 0.45}})

(def good-pass-possibilities
  {:goalkeeper {:defense 0.9
                :midfield 0.75
                :attack 0.2}
   :defense {:goalkeeper 0.95
             :defense 0.9
             :midfield 0.75
             :attack 0.2}
   :midfield {:goalkeeper 0.9
              :defense 0.9
              :midfield 0.7
              :attack 0.4}
   :attack {:goalkeeper 0.7
            :defense 0.5
            :midfield 0.6
            :attack 0.33}})

(defn choose-pass-end-zone
  [from-zone]
  (let [targets (from-zone pass-possibilities)
        r (rand)]
    (loop [acc 0
           [[zone prob] & rest] targets]
      (let [new-acc (+ acc prob)]
        (if (< r new-acc)
          zone
          (recur new-acc rest))))))

(defn pass?
  [zone-begin zone-end]
  (let [r (rand)
        prob (get-in good-pass-possibilities [zone-begin zone-end])]
    (< r prob)))

(defn offside?
  []
  (>= (rand-int 10) 9))

(defn out?
  [ball-holder]
  (let [r (rand-int 100)
        pass-skill (:passing ball-holder)]
    (> r pass-skill)))

(def foul-realiz-possib-map
  {:goalkeeper {:shot 1}
   :defense {:pass 1}
   :midfield {:pass 0.7
              :shot 0.3}
   :attack {:pass 0.25
            :shot 0.75}})

(defn duel-won?
  [ball-holder opp-player]
  (closer-value-to-first?
     (calc-avg (:strength ball-holder) (:speed ball-holder))
     (calc-avg (:strength opp-player) (:speed opp-player))))

(defn foul?
  "Greater the strength difference between players, greater the chance for foul."
  [ball-holder opp-player]
  (let [r (rand-int 100)
        ball-holder-stgh (:strength ball-holder)
        opp-player-stgh (:strength opp-player)]
    (if (> ball-holder-stgh opp-player-stgh)
      (and (> r opp-player-stgh) (< r ball-holder-stgh))
      (and (< r opp-player-stgh) (> r ball-holder-stgh)))))

(defn closer-value-to-first?
  [value-1 value-2]
  (let [total (+ value-1 value-2)
        r (rand-int total)]
    (if (< r value-1)
      true
      (if (> r value-2)
        false
        (if (< (- r value-1) (- value-2 r))
          true
          false)))))

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