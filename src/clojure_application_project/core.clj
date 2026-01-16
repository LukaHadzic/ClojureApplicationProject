(ns clojure-application-project.core
  (:gen-class) (:require [clojure-application-project.db :as db]
                         [clojure-application-project.events :as events]
                         [clojure-application-project.helpers :as helpers]))

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
  (let [actions-allowed (get events/zone-actions-controller (:zone match))
        event (rand-nth actions-allowed)]
    (do
      (println (str "Minute: " (:minute match)))
      (println (str "Ball holder: " (:ball-holder match)))
      (println (str "Possession: " (:possession match)))
      (println (str "Result: "
                    "home: " (:goals (:home match))
                    " away: " (:goals (:away match))))
      (println (str (last ((:possession match) (:log match)))))
      (event match))))
    ;(event match)))

(def el-classico (helpers/make-match (helpers/make-team "Real Madrid"
                                        {:goalkeeper [(helpers/make-player "Iker Casillas" 86)]
                                         :defense [(helpers/make-player "Dani Carvajal" 83)
                                                   (helpers/make-player "Pepe" 83)
                                                   (helpers/make-player "Sergio Ramos" 89)
                                                   (helpers/make-player "Marcelo" 85)]
                                         :midfield [(helpers/make-player "Sami Khedira" 86)
                                                    (helpers/make-player "Luka Modric" 88)
                                                    (helpers/make-player "Angel Di Maria" 88)]
                                         :attack [(helpers/make-player "Cristiano Ronaldo" 92)
                                                  (helpers/make-player "Karim Benzema" 87)
                                                  (helpers/make-player "Gareth Bale" 91)]})

                             (helpers/make-team "Barcelona"
                                        {:goalkeeper [(helpers/make-player "Victor Valdes" 87)]
                                         :defense [(helpers/make-player "Dani Alves" 85)
                                                   (helpers/make-player "Gerard Pique" 87)
                                                   (helpers/make-player "Javier Mascherano" 85)
                                                   (helpers/make-player "Jordi Alba" 83)]
                                         :midfield [(helpers/make-player "Sergio Busquets" 87)
                                                    (helpers/make-player "Xavi" 90)
                                                    (helpers/make-player "Andres Iniesta" 91)]
                                         :attack [(helpers/make-player "Lionel Messi" 94)
                                                  (helpers/make-player "Pedro" 85)
                                                  (helpers/make-player "Neymar" 87)]})))

(simulate-minute el-classico)

(defn simulate-match
  [match]
  (loop [game-data match]
    (if (>= (:minute game-data) 90)
      game-data
      (let [updated-game-data (simulate-minute game-data)]
        (recur (update updated-game-data :minute inc))))))

(simulate-match el-classico)

;Napraviti -> out, goal-out, foul, penalty

;Ubaciti golmana - odbrana suta

;Ubaciti golmana u igru, pasevi i dueli

;poasonova raspodela

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [el-classico (helpers/make-match (helpers/make-team "Real Madrid"
                                                          {:goalkeeper [(helpers/make-player "Iker Casillas" 86)]
                                                           :defense [(helpers/make-player "Dani Carvajal" 83)
                                                                     (helpers/make-player "Pepe" 83)
                                                                     (helpers/make-player "Sergio Ramos" 89)
                                                                     (helpers/make-player "Marcelo" 85)]
                                                           :midfield [(helpers/make-player "Sami Khedira" 86)
                                                                      (helpers/make-player "Luka Modric" 88)
                                                                      (helpers/make-player "Angel Di Maria" 88)]
                                                           :attack [(helpers/make-player "Cristiano Ronaldo" 92)
                                                                    (helpers/make-player "Karim Benzema" 87)
                                                                    (helpers/make-player "Gareth Bale" 91)]})

                                       (helpers/make-team "Barcelona"
                                                          {:goalkeeper [(helpers/make-player "Victor Valdes" 87)]
                                                           :defense [(helpers/make-player "Dani Alves" 85)
                                                                     (helpers/make-player "Gerard Pique" 87)
                                                                     (helpers/make-player "Javier Mascherano" 85)
                                                                     (helpers/make-player "Jordi Alba" 83)]
                                                           :midfield [(helpers/make-player "Sergio Busquets" 87)
                                                                      (helpers/make-player "Xavi" 90)
                                                                      (helpers/make-player "Andres Iniesta" 91)]
                                                           :attack [(helpers/make-player "Lionel Messi" 94)
                                                                    (helpers/make-player "Pedro" 85)
                                                                    (helpers/make-player "Neymar" 87)]}))]
    (simulate-match el-classico)))

;(println (db/get-table-column "testtable" "cntVal")))