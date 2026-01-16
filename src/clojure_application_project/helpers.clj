(ns clojure-application-project.helpers)

(defn make-player
  [name skill]
  {:name name :skill skill})

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

(defn opposite-team
  [team]
  (if (= team :home) :away :home))

(def opposite-zone-map
  {:defense :attack
   :attack :defense
   :midfield :midfield})

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
    (apply concat (vals (:players team))))

(defn goal?
  [player]
  (> (:skill player) (rand-int 101)))

(defn pass?
  [player]
  (> (:skill player) (rand-int 101)))