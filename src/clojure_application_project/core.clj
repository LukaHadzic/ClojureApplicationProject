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
        return-map (event state)
        event-duration (:event-duration return-map)
        new-state (:new-state return-map)]
    (if (nil? new-state)
      (println "new state je nil")
      (do
        ;(println (str "Ball holder: " (:name (:ball-holder state))))
        ;(println (str "Possession: " (:possession state)))
        ;(println (str "Time: " (:time state)))
        ;(println (str "Zone: " (:zone state)))
        ;(println (str "Phase: " (:phase state)))
        ;(println (str "Event that occured: " event))
        ;(println (str "New Ball holder: " (:name (:ball-holder new-state))))
        ;(println (str "New Possession: " (:possession new-state)))
        ;(println (str "New Time: " (+ (:time state) duration event-duration)))
        ;(println (str "New Zone: " (:zone new-state)))
        ;(println (str "New Phase: " (:phase new-state)))
        ;(println "")
        ;(if (= (:phase state) :penalty) (println "Penalty occured!"))
        ;(println (str "Shots: " (helpers/count-event new-state :away :shots)))
        ;(println (str "Shots on goal: " (helpers/count-event new-state :away :shots-on-goal)))
        ;(println (str "Saves: " (helpers/count-event new-state :home :saves)))
        ;(println (str "Goals: " (helpers/count-event new-state :away :goals)))
        (update new-state :time + duration event-duration))))) ;   goal-keeping defense passing attack
                                                                    ;handling reflexes positioning
                                                                    ;technique shot-power finishing
                                                                    ;strength speed
      ;(update new-state :time + duration event-duration))))
;#object[clojure_application_project.events$duel 0x4aea9887 clojure_application_project.events$
(def el-classico
  (helpers/make-match
    (helpers/make-team "Real Madrid"
                       {:goalkeeper [(helpers/make-player 1 "Iker Casillas"
                                                          86 95 40 50 20 92 95 90 30 40 60 55 60)]

                        :defense [(helpers/make-player 2 "Dani Carvajal"
                                                       83 10 82 78 65 10 10 75 78 70 75 84 75)

                                  (helpers/make-player 3 "Pepe"
                                                       83 10 86 65 55 10 10 82 60 55 90 72 80)

                                  (helpers/make-player 4 "Sergio Ramos"
                                                       89 10 88 70 72 10 10 85 70 65 88 78 85)

                                  (helpers/make-player 5 "Marcelo"
                                                       85 10 80 84 78 10 10 80 86 72 72 84 78)]

                        :midfield [(helpers/make-player 6 "Sami Khedira"
                                                        86 10 84 78 75 10 10 80 74 70 86 72 82)

                                   (helpers/make-player 7 "Luka Modric"
                                                        88 10 70 90 78 10 10 88 92 72 65 78 70)

                                   (helpers/make-player 8 "Angel Di Maria"
                                                        88 10 68 86 82 10 10 82 90 78 70 90 72)]

                        :attack [(helpers/make-player 9 "Cristiano Ronaldo"
                                                      92 10 55 82 94 10 10 88 92 93 85 92 80)

                                 (helpers/make-player 10 "Karim Benzema"
                                                      87 10 45 78 88 10 10 85 88 90 82 82 78)

                                 (helpers/make-player 11 "Gareth Bale"
                                                      91 10 60 82 90 10 10 84 88 90 80 94 82)]})

    (helpers/make-team "Barcelona"
                       {:goalkeeper [(helpers/make-player 12 "Victor Valdes"
                                                          87 94 40 55 20 90 92 88 30 40 60 55 60)]

                        :defense [(helpers/make-player 13 "Dani Alves"
                                                       85 10 80 82 75 10 10 78 84 72 70 88 72)

                                  (helpers/make-player 14 "Gerard Pique"
                                                       87 10 88 75 70 10 10 85 72 68 88 65 85)

                                  (helpers/make-player 15 "Javier Mascherano"
                                                       85 10 86 78 70 10 10 84 74 65 85 72 82)

                                  (helpers/make-player 16 "Jordi Alba"
                                                       83 10 78 80 70 10 10 75 82 65 68 90 70)]

                        :midfield [(helpers/make-player 17 "Sergio Busquets"
                                                        87 10 85 88 72 10 10 88 86 68 82 60 85)

                                   (helpers/make-player 18 "Xavi"
                                                        90 10 68 94 78 10 10 90 94 72 65 65 70)

                                   (helpers/make-player 19 "Andres Iniesta"
                                                        91 10 70 92 84 10 10 88 96 78 60 82 65)]

                        :attack [(helpers/make-player 20 "Lionel Messi"
                                                      94 10 55 88 95 10 10 90 96 96 70 92 75)

                                 (helpers/make-player 21 "Pedro"
                                                      85 10 55 80 84 10 10 82 86 85 70 88 70)

                                 (helpers/make-player 22 "Neymar"
                                                      87 10 50 84 88 10 10 84 94 86 60 90 65)]})))

(defn max-20-chars
  [s]
  (let [s (str s)]
    (if (> (count s) 20)
      (subs s 0 20)
      s)))

(defn padd-with-spaces
  [team-name-len]
  (let [n (- (/ team-name-len 2) 2)]
    (apply str (repeat (max 0 n) " "))))

(defn show-match-end
  [state]
  (let [home (get-in state [:home])
        away (get-in state [:away])
        home-name-length (count (:name (:team home)))]

    (println "\n==============================")
    (println "        MATCH FINISHED       ")
    (println "==============================\n")

    (println (format "%s        :        %-15s"
                     (max-20-chars (:name (:team home)))
                     (:name (:team away))))

    (println (format "%s%4d          Goals        %4d"
                     (padd-with-spaces home-name-length)
                     (:goals home)
                     (:goals away)))

    (println (format "%s%4d          Shots        %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :shots)
                     (helpers/count-event state :away :shots)))

    (println (format "%s%4d     Shots on target   %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :shots-on-goal)
                     (helpers/count-event state :away :shots-on-goal)))

    (println (format "%s%4d          Passes       %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :passes)
                     (helpers/count-event state :away :passes)))

    (println (format "%s%4d       Good passes     %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :good-passes)
                     (helpers/count-event state :away :good-passes)))

    (println (format "%s%4d         Offsides      %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :offsides)
                     (helpers/count-event state :away :offsides)))

    (println (format "%s%4d          Duels        %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :duels)
                     (helpers/count-event state :away :duels)))

    (println (format "%s%4d        Duels won      %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :duels-won)
                     (helpers/count-event state :away :duels-won)))

    (println (format "%s%4d        Driblings      %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :crosses)
                     (helpers/count-event state :away :crosses)))

    (println (format "%s%4d          Fouls        %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :fouls)
                     (helpers/count-event state :away :fouls)))

    (println (format "%s%4d       Yellow cards    %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :yellow-cards)
                     (helpers/count-event state :away :yellow-cards)))

    (println (format "%s%4d        Red cards      %4d"
                     (padd-with-spaces home-name-length)
                     (helpers/count-event state :home :red-card)
                     (helpers/count-event state :away :red-card)))

    (println "    ______________________________\n")))

(defn simulate-match
  [state]
  (loop [curr-state state]
    (let [time (:time curr-state)]
      (if (>= time 5400)
        (do
          (show-match-end curr-state)
          curr-state)
        (recur (simulate-event curr-state))))))

(def el-class-fin
  (simulate-match el-classico))

(do
  (println (str "Shots: " (helpers/count-event el-class-fin :away :shots)))
  (println (str "Shots on goal: " (helpers/count-event el-class-fin :away :shots-on-goal)))
  (println (str "Saves: " (helpers/count-event el-class-fin :home :saves)))
  (println (str "Goals: " (helpers/count-event el-class-fin :away :goals)))
  (println (str "Red cards: " (helpers/count-event el-class-fin :away :red-card))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [el-classico (helpers/make-match
                      (helpers/make-team "Real Madrid"
                                         {:goalkeeper [(helpers/make-player 1 "Iker Casillas"
                                                                            86 95 40 50 20 92 95 90 30 40 60 55 60)]

                                          :defense [(helpers/make-player 2 "Dani Carvajal"
                                                                         83 10 82 78 65 10 10 75 78 70 75 84 75)

                                                    (helpers/make-player 3 "Pepe"
                                                                         83 10 86 65 55 10 10 82 60 55 90 72 80)

                                                    (helpers/make-player 4 "Sergio Ramos"
                                                                         89 10 88 70 72 10 10 85 70 65 88 78 85)

                                                    (helpers/make-player 5 "Marcelo"
                                                                         85 10 80 84 78 10 10 80 86 72 72 84 78)]

                                          :midfield [(helpers/make-player 6 "Sami Khedira"
                                                                          86 10 84 78 75 10 10 80 74 70 86 72 82)

                                                     (helpers/make-player 7 "Luka Modric"
                                                                          88 10 70 90 78 10 10 88 92 72 65 78 70)

                                                     (helpers/make-player 8 "Angel Di Maria"
                                                                          88 10 68 86 82 10 10 82 90 78 70 90 72)]

                                          :attack [(helpers/make-player 9 "Cristiano Ronaldo"
                                                                        92 10 55 82 94 10 10 88 92 93 85 92 80)

                                                   (helpers/make-player 10 "Karim Benzema"
                                                                        87 10 45 78 88 10 10 85 88 90 82 82 78)

                                                   (helpers/make-player 11 "Gareth Bale"
                                                                        91 10 60 82 90 10 10 84 88 90 80 94 82)]})

                      (helpers/make-team "Barcelona"
                                         {:goalkeeper [(helpers/make-player 12 "Victor Valdes"
                                                                            87 94 40 55 20 90 92 88 30 40 60 55 60)]

                                          :defense [(helpers/make-player 13 "Dani Alves"
                                                                         85 10 80 82 75 10 10 78 84 72 70 88 72)

                                                    (helpers/make-player 14 "Gerard Pique"
                                                                         87 10 88 75 70 10 10 85 72 68 88 65 85)

                                                    (helpers/make-player 15 "Javier Mascherano"
                                                                         85 10 86 78 70 10 10 84 74 65 85 72 82)

                                                    (helpers/make-player 16 "Jordi Alba"
                                                                         83 10 78 80 70 10 10 75 82 65 68 90 70)]

                                          :midfield [(helpers/make-player 17 "Sergio Busquets"
                                                                          87 10 85 88 72 10 10 88 86 68 82 60 85)

                                                     (helpers/make-player 18 "Xavi"
                                                                          90 10 68 94 78 10 10 90 94 72 65 65 70)

                                                     (helpers/make-player 19 "Andres Iniesta"
                                                                          91 10 70 92 84 10 10 88 96 78 60 82 65)]

                                          :attack [(helpers/make-player 20 "Lionel Messi"
                                                                        94 10 55 88 95 10 10 90 96 96 70 92 75)

                                                   (helpers/make-player 21 "Pedro"
                                                                        85 10 55 80 84 10 10 82 86 85 70 88 70)

                                                   (helpers/make-player 22 "Neymar"
                                                                        87 10 50 84 88 10 10 84 94 86 60 90 65)]}))]

    (simulate-match el-classico)))