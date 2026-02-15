(ns clojure-application-project.core
  (:gen-class) (:require [clojure-application-project.db :as db]
                         [clojure-application-project.events :as events]
                         [clojure-application-project.helpers :as helpers]))

;(defn simulate-minute
;  ;Minut utakmice kada neko ima sansu za gol
;  [match]
;  (let [attacking-key (if (zero? (rand-int 2))
;                         :home
;                         :away)
;        player (rand-nth (:players (:team (attacking-key match))))]
;    (if (goal? player)
;      (do
;        (update-in match [attacking-key :goals] inc))
;      match)))

;(defn simulate-event
;  [state]
;  (let [lambda (helpers/get-lambda (:zone state))
;        due-next-event (exp-rand lambda)
;        event (events/choose-event (:phase state))
;        new-state (event state)]
;    {:state new-state
;     :}))

(defn simulate-minute
  [match]
  (let [actions-allowed (get events/phase-actions-controller (:phase match))
        event (rand-nth actions-allowed)]
    ;(do
    ;  (println (str "Minute: " (:minute match)))
    ;  (println (str "Ball holder: " (:ball-holder match)))
    ;  (println (str "Possession: " (:possession match)))
    ;  (println (str "Result: "
    ;                "home: " (:goals (:home match))
    ;                " away: " (:goals (:away match))))
    ;  (println (str (last ((:possession match) (:log match)))))
    ;  (event match))))
    ;(do
      ;(println (str (:zone match) " \n" (:minute match) " " event))
      ;(event match))))
      (event match)))

(def el-classico (helpers/make-match (helpers/make-team "Real Madrid"
                                        {:goalkeeper [(helpers/make-player 1 "Iker Casillas" 86)]
                                         :defense [(helpers/make-player 2 "Dani Carvajal" 83)
                                                   (helpers/make-player 3 "Pepe" 83)
                                                   (helpers/make-player 4 "Sergio Ramos" 89)
                                                   (helpers/make-player 5 "Marcelo" 85)]
                                         :midfield [(helpers/make-player 6 "Sami Khedira" 86)
                                                    (helpers/make-player 7 "Luka Modric" 88)
                                                    (helpers/make-player 8 "Angel Di Maria" 88)]
                                         :attack [(helpers/make-player 9 "Cristiano Ronaldo" 92)
                                                  (helpers/make-player 10 "Karim Benzema" 87)
                                                  (helpers/make-player 11 "Gareth Bale" 91)]})

                             (helpers/make-team "Barcelona"
                                        {:goalkeeper [(helpers/make-player 12 "Victor Valdes" 87)]
                                         :defense [(helpers/make-player 13 "Dani Alves" 85)
                                                   (helpers/make-player 14 "Gerard Pique" 87)
                                                   (helpers/make-player 15 "Javier Mascherano" 85)
                                                   (helpers/make-player 16 "Jordi Alba" 83)]
                                         :midfield [(helpers/make-player 17 "Sergio Busquets" 87)
                                                    (helpers/make-player 18 "Xavi" 90)
                                                    (helpers/make-player 19 "Andres Iniesta" 91)]
                                         :attack [(helpers/make-player 20 "Lionel Messi" 94)
                                                  (helpers/make-player 21 "Pedro" 85)
                                                  (helpers/make-player 22 "Neymar" 87)]})))

;(simulate-minute el-classico)

(defn simulate-match
  [match]
  (loop [game-data match]
    (if (>= (:minute game-data) 90)
      game-data
      (let [updated-game-data (simulate-minute game-data)]
        (recur (update updated-game-data :minute inc))))))

;(helpers/count-event (:home el-classico-fin) :duels)

(def el-classico-fin (simulate-match el-classico))

(dotimes [cnt 1000]
  (simulate-match el-classico))

;URADITI ALI PRED KRAJ
;Napraviti -> out, goal-out, foul, penalty
;poasonova raspodela

;URADITI
;Ubaciti golmana - odbrana suta -> ZAVRSENO
;Ubaciti golmana u igru, pasevi i dueli -> ZAVRSENO
; samo ostaviti :goalkeeper u svakom timu
; za duele se biraju protivnicki igraci iz attack

(defn simulate-debug-game [state]
  (loop [i 1
         s state]
    (if (> i 90)
      s
      (recur (inc i)
             (events/update-duel s 1 (helpers/rand-opposite-player state))))))
             ;(events/update-duel s (rand-int 2) (helpers/rand-opposite-player state))))))
             ;(events/offside s)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [el-classico (helpers/make-match (helpers/make-team "Real Madrid"
                                                           {:goalkeeper [(helpers/make-player 1 "Iker Casillas" 86)]
                                                            :defense [(helpers/make-player 2 "Dani Carvajal" 83)
                                                                      (helpers/make-player 3 "Pepe" 83)
                                                                      (helpers/make-player 4 "Sergio Ramos" 89)
                                                                      (helpers/make-player 5 "Marcelo" 85)]
                                                            :midfield [(helpers/make-player 6 "Sami Khedira" 86)
                                                                       (helpers/make-player 7 "Luka Modric" 88)
                                                                       (helpers/make-player 8 "Angel Di Maria" 88)]
                                                            :attack [(helpers/make-player 9 "Cristiano Ronaldo" 92)
                                                                     (helpers/make-player 10 "Karim Benzema" 87)
                                                                     (helpers/make-player 11 "Gareth Bale" 93)]})

                                       (helpers/make-team "Barcelona"
                                                          {:goalkeeper [(helpers/make-player 12 "Victor Valdes" 87)]
                                                           :defense [(helpers/make-player 13 "Dani Alves" 85)
                                                                     (helpers/make-player 14 "Gerard Pique" 87)
                                                                     (helpers/make-player 15 "Javier Mascherano" 85)
                                                                     (helpers/make-player 16 "Jordi Alba" 83)]
                                                           :midfield [(helpers/make-player 17 "Sergio Busquets" 87)
                                                                      (helpers/make-player 18 "Xavi" 90)
                                                                      (helpers/make-player 19 "Andres Iniesta" 91)]
                                                           :attack [(helpers/make-player 20 "Lionel Messi" 94)
                                                                    (helpers/make-player 21 "Pedro" 85)
                                                                    (helpers/make-player 22 "Neymar" 87)]}))]
    (def el-classico-deb (events/resume-foul (events/duel (events/resume-game el-classico))))

    (simulate-match el-classico)
    ;(simulate-debug-game el-classico)
    ))

;(println (db/get-table-column "testtable" "cntVal")))