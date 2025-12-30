(ns clojure-application-project.core
  (:gen-class) (:require [clojure-application-project.db :as db]))

(defn make-player  [name skill] {:name name :skill skill})

(defn make-team [name players] {:name name :players players})

(defn goal? [player]
  (> (:skill player) (rand-int 101)))

(defn make-match
  [home away]
  {:home {:team home :goals 0}
   :away {:team away :goals 0}
   :minute 0
   :possession :home
   :zone :midfield
   :ball-holder 7
   :log []})

(defn show-team-players [team]
  (println (map (fn [plName] (str "Igrac: " plName)) (map (fn [el]
         (:name el)) (:players team))
  )))

(defn simulate-minute
  ;Minut utakmice kada neko ima sansu za gol
  [match]
  (let [attacking-key (if (zero? (rand-int 2))
                         :home
                         :away)
        player (rand-nth (:players (:team (attacking-key match))))]
    (if (goal? player)
      (do
        (update-in match [attacking-key :goals] inc))
      match)))

(def el-classico (make-match (make-team "Real Madrid" [(make-player "Ronaldo" 97)
                                                       (make-player "Ramos" 86)
                                                       (make-player "Alonso" 93)])
                             (make-team "Barcelona" [(make-player "Messi" 97)
                                                     (make-player "Alves" 86)
                                                     (make-player "Villa" 93)])))

(simulate-minute el-classico)

(defn simulate-match
  [match]
  (loop [minute 0
         game-data match]
    (if (> minute 90)
      game-data
      (let [updated-game-data (simulate-minute game-data)]
        (recur (inc minute) updated-game-data)))))

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