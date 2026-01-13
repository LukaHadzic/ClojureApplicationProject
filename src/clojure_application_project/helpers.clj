(ns clojure-application-project.helpers)

(defn make-player  [name skill] {:name name :skill skill})

(defn make-team [name players-and-positions] {:name name :players players-and-positions})

(defn make-match
  [home away]
  (let [state {:home {:team home :goals 0}
               :away {:team away :goals 0}
               :minute 0
               :possession :home
               :zone :midfield
               :ball-holder {}
               :log {:home [] :away []}}
        state (assoc state :ball-holder (rand-nth (get-in state [(:possession state) :team :players :attack])))]
    state))

(defn opposite-team [team]
  (if (= team :home) :away :home))

(defn new-ball-holder
  [state]
  (rand-nth (get-in state [(:possession state) :team :players (:zone state)])))

(defn new-ball-holder-2
  [state team zone]
  (rand-nth (get-in state [team :team :players zone])))

(defn get-team-players
  [team]
    (apply concat (vals (:players team))))

(defn goal? [player]
  (> (:skill player) (rand-int 101)))