(ns clojure-application-project.core
  (:gen-class) (:require [clojure-application-project.db :as db]))

(defn make-player  [name skill] {:name name :skill skill})

(defn make-team [name players-and-positions] {:name name :players players-and-positions})

(defn goal? [player]
  (> (:skill player) (rand-int 101)))

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
  (rand-nth (get-in state [(opposite-team state) :team :players (:zone state)])))

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
        (update-in [:log (opposite-team (:possession state))] conj :duel-lost))
    (let [new-possession (opposite-team (:possession state))
          players (get-in state [new-possession :team :players (:zone state)])
          new-ball-holder (rand-nth players)]
      (-> state
          (update-in [:log (:possession state)] conj :duel-lost)
          (update-in [:log (opposite-team (:possession state))] conj :duel-won)
          (assoc :possession new-possession :ball-holder new-ball-holder)))))

(defn offside
  [state]
  (-> state
      (assoc :ball-holder (new-ball-holder state))
      (assoc :zone :offside)
      (update :possession opposite-team)
      (update-in [:log (:possession state)] conj :offside)))

(defn finish-shot
  [state event zone is-goal]
  (let [team (:possession state)]
    (-> state
        (update-in [:log team] conj event)
        (update-in [team :goals] + is-goal)
        (assoc :zone zone)
        (assoc :ball-holder (new-ball-holder state))
        (update :possession opposite-team))))

(defn goal [state]
  (finish-shot state :goal :midfield 1))

(defn miss [state]
  (finish-shot state :miss :defense 0))

(defn shot
  [state]
  (if (goal? (:ball-holder state))
    (goal state)
    (miss state)))

(def zone-actions-controller
  {:defense [pass duel]
   :midfield [pass duel]
   :attack [pass duel shot offside]
   :offside [pass]})

(defn show-team-players [team]
  (println (map (fn [plName] (str "Igrac: " plName)) (map (fn [el]
         (:name el)) (:players team))
  )))

;(defn simulate-minute
  ;Minut utakmice kada neko ima sansu za gol
;  [match]
;  (let [attacking-key (if (zero? (rand-int 2))
;                         :home
;                         :away)
;        player (rand-nth (:players (:team (attacking-key match))))]
;    (if (goal? player)
;      (do
;        (update-in match [attacking-key :goals] inc))
;      match)))

(defn simulate-minute
  [match]
  (let [actions-allowed (get zone-actions-controller (:zone match))
        event (rand-nth actions-allowed)]
    (event match)))

(def el-classico (make-match (make-team "Real Madrid"
                                        {:goalkeeper [(make-player "Iker Casillas" 86)]
                                         :defense [(make-player "Dani Carvajal" 83)
                                                   (make-player "Pepe" 83)
                                                   (make-player "Sergio Ramos" 89)
                                                   (make-player "Marcelo" 85)]
                                         :midfield [(make-player "Sami Khedira" 86)
                                                    (make-player "Luka Modric" 88)
                                                    (make-player "Angel Di Maria" 88)]
                                         :attack [(make-player "Cristiano Ronaldo" 92)
                                                  (make-player "Karim Benzema" 87)
                                                  (make-player "Gareth Bale" 91)]})

                             (make-team "Barcelona"
                                        {:goalkeeper [(make-player "Victor Valdes" 87)]
                                         :defense [(make-player "Dani Alves" 85)
                                                   (make-player "Gerard Pique" 87)
                                                   (make-player "Javier Mascherano" 85)
                                                   (make-player "Jordi Alba" 83)]
                                         :midfield [(make-player "Sergio Busquets" 87)
                                                    (make-player "Xavi" 90)
                                                    (make-player "Andres Iniesta" 91)]
                                         :attack [(make-player "Lionel Messi" 94)
                                                  (make-player "Pedro" 85)
                                                  (make-player "Neymar" 87)]})))

;(simulate-minute el-classico)

(defn simulate-match
  [match]
  (loop [game-data match]
    (if (>= (:minute game-data) 90)
      game-data
      (let [updated-game-data (simulate-minute game-data)]
        (recur (update updated-game-data :minute inc))))))

(simulate-match el-classico)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [team_h (make-team "Real Madrid" [(make-player "Ronaldo" 97)
                                        (make-player "Ramos" 86)
                                        (make-player "Alonso" 93)])

        team_a (make-team "Barcelona" [(make-player "Messi" 97)
                                        (make-player "Alves" 86)
                                        (make-player "Villa" 93)])



        ;(make-team "Barcelona" [(make-player "Messi" 97) (make-player "Alves" 86) (make-player "Villa" 93)])

        el_classico (make-match team_h team_a)]

  (println team_h)
  (println team_a)
  (println el_classico)
  (println "Ispis tima preko metode: ")
  (show-team-players team_h)))

;(println (db/get-table-column "testtable" "cntVal")))