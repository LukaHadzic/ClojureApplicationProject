(ns clojure-application-project.helpers)

(defn make-player
  [id name skill]
  {:id id :name name :skill skill :passes 0 :good-passes 0 :shots 0 :goals 0 :duels 0 :duels-won 0 :offsides 0})

(defn make-team
  [name players-and-positions]
  {:name name :players players-and-positions})

(defn make-match
  [home away]
  (let [state {:home {:team home :goals 0}
               :away {:team away :goals 0}
               :minute 0
               :possession :home
               :zone :resume
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

(defn opposite-team
  [team]
  (if (= team :home) :away :home))

(def opposite-zone-map
  {:defense :attack
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
  [state]
  ((:zone state) opposite-zone-map))

(defn rand-zone
  []
  (rand-nth [:defense :midfield :attack]))
;
;(defn new-ball-holder
;  [state]
;  (rand-nth (get-in state [(:possession state) :team :players (:zone state)])))

(defn rand-opposite-player
  [state]
  (rand-nth (get-in state [(opposite-team (:possession state))  :team :players (opposite-zone state)])))

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

(defn pass?
  [player]
  (> (:skill player) (rand-int 101)))

(defn offside?
  []
  (>= (rand-int 5) 4))

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