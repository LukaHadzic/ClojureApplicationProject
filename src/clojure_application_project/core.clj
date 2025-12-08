(ns clojure-application-project.core
  (:gen-class) (:require [clojure-application-project.db :as db]))

(defn make-player  [name skill] {:name name :skill skill})

(defn make-team [name players] {:name name :players players})

(defn make-match [home away]
  {:home home :away away
   :home_goals 0 :away_goals 0})

(defn goal? [player]
  (> (:skill player) (rand-int 101))
  )


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [team_h (make-team "Real Madrid" [(make-player "Ronaldo" 97)
                                        (make-player "Ramos" 86)
                                        (make-player "Alonso" 93)])

        team_a (make-team "Barcelona" [(make-player "Messi" 97)
                                        (make-player "Alves" 86)
                                        (make-player "Villa" 93)])

        el_classico (make-match team_h team_a)]

  (println team_h)
  (println team_a)
  (println el_classico)))

;(println (db/get-table-column "testtable" "cntVal")))