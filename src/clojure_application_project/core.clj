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

;DOBRO
;(defn simulate-minute
;  [match]
;  (let [actions-allowed (get events/phase-actions-controller (:phase match))
;        event (rand-nth actions-allowed)]
;    ;(do
;    ;  (println (str "Minute: " (:minute match)))
;    ;  (println (str "Ball holder: " (:ball-holder match)))
;    ;  (println (str "Possession: " (:possession match)))
;    ;  (println (str "Result: "
;    ;                "home: " (:goals (:home match))
;    ;                " away: " (:goals (:away match))))
;    ;  (println (str (last ((:possession match) (:log match)))))
;    ;  (event match))))
;    ;(do
;      ;(println (str (:zone match) " \n" (:minute match) " " event))
;      ;(event match))))
;      (event match)))

(defn simulate-event
  [state]
  (let [zone (:zone state)
        duration (events/exp-rand zone)
        event (events/choose-event (:phase state))
        ;POPRAVITI ;Ako je event out, foul, corner ili penalty, duration se ne uzima u obzir!
        return-map (event state)
        event-duration (:event-duration return-map)
        new-state (:new-state return-map)]
    (if (nil? new-state)
      (println "new state je nil")
      (do
        (println (str "Event: " event))
        (println (str "Ball holder: " (:ball-holder state)))
        (println (str "Possession: " (:possession state)))
        (println (str "Time: " (:time state)))
        (println (str "Zone: " (:zone state)))
        (println (str "Phase: " (:phase state)))
        (println "")
        (update new-state :time + duration event-duration)))))
      ;(update new-state :time + duration event-duration))))
;#object[clojure_application_project.events$duel 0x4aea9887 clojure_application_project.events$
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

;DOBRO
;(defn simulate-match
;  [match]
;  (loop [game-data match]
;    (if (>= (:minute game-data) 90)
;      game-data
;      (let [updated-game-data (simulate-minute game-data)]
;        (recur (update updated-game-data :minute inc))))))

(defn simulate-match
  [state]
  (loop [curr-state state]
    (let [time (:time curr-state)]
      (if (>= time 5400)
        curr-state
        (recur (simulate-event curr-state))))))

;(helpers/count-event (:home el-classico-fin) :duels)

;(def el-classico-fin (simulate-match el-classico))

;(dotimes [cnt 1000]
;  (simulate-match el-classico));

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

    (events/resume-foul (assoc (assoc (assoc el-classico :ball-holder {:id 1 :name "Iker" :skill 86
                                                                            :goal-keeping 96 :defense 96 :passing 96 :strength 96 :finishing 96 :attack 96 :speed 96
                                                                            :saves 0 :passes 0 :good-passes 0
                                                                            :shots 0 :shots-on-goal 0 :goals 0 :duels 0 :duels-won 0 :offsides 0 :fouls 0}
                                                                           ) :zone :goalkeeper) :phase :goalkeeper))

                  ;{:id 21 :name "Pedro" :skill 85
                 ; :goal-keeping 96 :defense 96 :passing 96 :strength 96 :finishing 96 :attack 96 :speed 96
                 ; :saves 0 :passes 0 :good-passes 0
                 ; :shots 0 :shots-on-goal 0 :goals 0 :duels 0 :duels-won 0 :offsides 0 :fouls 0})

    (dotimes [cnt 1000]
      (events/duel (assoc (assoc el-classico :ball-holder
                                             {:id 1 :name "Iker" :skill 86
                                              :goal-keeping 96 :defense 96 :passing 96 :strength 96 :finishing 96 :attack 96 :speed 96
                                              :saves 0 :passes 0 :good-passes 0
                                              :shots 0 :shots-on-goal 0 :goals 0 :duels 0 :duels-won 0 :offsides 0 :fouls 0}
                                             ) :zone :goalkeeper)))

    (simulate-match el-classico)
    ;(simulate-debug-game el-classico)
    ))

;(println (db/get-table-column "testtable" "cntVal")))