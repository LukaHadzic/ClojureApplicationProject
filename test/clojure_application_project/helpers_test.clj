(ns clojure-application-project.helpers-test
  (:require [midje.sweet :refer :all]
            [clojure-application-project.helpers :as helpers]))

(facts "Testing helpers/make-player function"
       (fact "Make player with provided data"
             (helpers/make-player 13 "Dani Alves"
                         85 10 80 82 75 10 10 78 84 72 70 88 72)
             =>
             {:finishing 70, :good-passes 0, :skill 85,
              :goals 0, :positioning 78, :speed 72, :duels-won 0,
              :shot-power 72, :name "Dani Alves", :red-card 0,
              :crosses 0, :shots-on-goal 0, :passes 0, :shots 0,
              :passing 82,
              :reflexes 10,
              :defense 80,
              :strength 88,
              :offsides 0,
              :handling 10,
              :technique 84,
              :id 13,
              :yellow-cards 0,
              :goal-keeping 10,
              :duels 0,
              :fouls 0,
              :attack 75,
              :saves 0}))

(facts "Testing helpers/make-team function"
       (fact "Make team with provided data"
             (helpers/make-team "Barcelona"
                                {:goalkeeper [{:a "a"}]
                                 :defense [{:b "b"} {:b "b"} {:b "b"} {:b "b"}]
                                 :midfield [{:c "c"} {:c "c"} {:c "c"}]
                                 :attack [{:d "d"} {:d "d"} {:d "d"}]})
             =>
             {:name "Barcelona"
              :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
              :players {:goalkeeper [{:a "a"}]
                        :defense [{:b "b"} {:b "b"} {:b "b"} {:b "b"}]
                        :midfield [{:c "c"} {:c "c"} {:c "c"}]
                        :attack [{:d "d"} {:d "d"} {:d "d"}]}
              :kicked-players {}}))

(def mock-match-test
  {:home {:team {:name "Barcelona"
                 :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
                 :players {:goalkeeper [{:id 1 :a "a"}]
                           :defense [{:id 2 :b "b"} {:id 3 :b "b"} {:id 4 :b "b"} {:id 5 :b "b"}]
                           :midfield [{:id 6 :c "c"} {:id 7 :c "c"} {:id 8 :c "c"}]
                           :attack [{:id 9 :e "e"} {:id 10 :f "f"} {:id 11 :g "g"}]}
                 :kicked-players {}}
          :goals 0}
   :away {:team {:name "Real Madrid"
                 :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
                 :players {:goalkeeper [{:id 12 :a "a"}]
                  :defense [{:id 13 :b "b"} {:id 14 :b "b"} {:id 15 :b "b"} {:id 16 :b "b"}]
                  :midfield [{:id 17 :c "c"} {:id 18 :c "c"} {:id 19 :c "c"}]
                  :attack [{:id 20 :d "d"} {:id 21 :d "d"} {:id 22 :d "d"}]}
                 :kicked-players {}}
          :goals 0}
   :minute 0
   :time 0
   :possession :home
   :zone :attack
   :phase :resume
   :ball-holder {:f "f"}
   :log {:home [] :away []}})

(def mock-match-test-players
  (helpers/make-match
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
                                                      87 10 50 84 88 10 10 84 94 86 60 90 65)]})
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
                                                      91 10 60 82 90 10 10 84 88 90 80 94 82)]})))

(facts "Testing helpers/make-match function"
       (fact "Make match with provided two team"
           (with-redefs [rand-nth (fn [coll] (second coll))]
             (helpers/make-match (helpers/make-team "Barcelona"
                                 {:goalkeeper [{:a "a"}]
                                  :defense [{:b "b"} {:b "b"} {:b "b"} {:b "b"}]
                                  :midfield [{:c "c"} {:c "c"} {:c "c"}]
                                  :attack [{:e "e"} {:f "f"} {:g "g"}]})
                                 (helpers/make-team "Real Madrid"
                                                    {:goalkeeper [{:a "a"}]
                                                     :defense [{:b "b"} {:b "b"} {:b "b"} {:b "b"}]
                                                     :midfield [{:c "c"} {:c "c"} {:c "c"}]
                                                     :attack [{:d "d"} {:d "d"} {:d "d"}]})))
             =>
             {:home {:team {:name "Barcelona"
                            :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
                            :players {:goalkeeper [{:a "a"}]
                                      :defense [{:b "b"} {:b "b"} {:b "b"} {:b "b"}]
                                      :midfield [{:c "c"} {:c "c"} {:c "c"}]
                                      :attack [{:e "e"} {:f "f"} {:g "g"}]}
                            :kicked-players {}}
                     :goals 0}
              :away {:team {:name "Real Madrid"
                            :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
                            :players {:goalkeeper [{:a "a"}]
                                      :defense [{:b "b"} {:b "b"} {:b "b"} {:b "b"}]
                                      :midfield [{:c "c"} {:c "c"} {:c "c"}]
                                      :attack [{:d "d"} {:d "d"} {:d "d"}]}
                            :kicked-players {}}
                     :goals 0}
              :minute 0
              :time 0
              :possession :home
              :zone :attack
              :phase :resume
              :ball-holder {:f "f"}
              :log {:home [] :away []}}))

(facts "Testing helpers/wrap-return function"
       (fact "Wrap provided arguments into return map"
             (helpers/wrap-return "New-state-map" 1.87)
             =>
             {:new-state "New-state-map"
              :event-duration 1.87}))

(facts "Testing helpers/calc-avg function"
       (fact "Return average value of two provided values"
             (helpers/calc-avg 1 3) => 2))

(facts "Testing helpers/closer-value-to-first? function"
       (fact "Return true if random generated number in function
       is closer to first element or false if not"
             (with-redefs [rand-int (fn [_] 85)]
               (helpers/closer-value-to-first? 70 89) => false
               (helpers/closer-value-to-first? 87 89) => true
               (helpers/closer-value-to-first? 55 70) => false
               (helpers/closer-value-to-first? 80 91) => true)))

(facts "Testing helpers/opposite-team function"
       (fact "Return opposite team of provided team"
             (helpers/opposite-team :home) => :away
             (helpers/opposite-team :away) => :home))

(facts "Testing helpers/next-zone function"
       (fact "Return next zone of provided zone"
             (helpers/next-zone :goalkeeper) => :defense
             (helpers/next-zone :defense) => :midfield
             (helpers/next-zone :midfield) => :attack
             (helpers/next-zone :attack) => :penalty-box
             (helpers/next-zone :penalty-box) => :penalty-box))

(facts "Testing helpers/prev-zone function"
       (fact "Return previous zone of provided zone"
             (helpers/prev-zone :goalkeeper) => nil
             (helpers/prev-zone :defense) => :goalkeeper
             (helpers/prev-zone :midfield) => :defense
             (helpers/prev-zone :attack) => :midfield
             (helpers/prev-zone :penalty-box) => :attack))

(facts "Testing helpers/forward? function"
       (fact "Return true if ball is headed in forward
       direction from team in possession or false if not"
             (helpers/forward? :attack :defense false) => false
             (helpers/forward? :attack :penalty-box false) => true
             (helpers/forward? :attack :defense true) => true
             (helpers/forward? :attack :penalty-box true) => false))

(facts "Testing helpers/last-zone? function"
       (fact "Return true if zone is last in order in provided
       direction, or false if it's not"
             (helpers/last-zone? :attack true false) => false
             (helpers/last-zone? :penalty-box true false) => true
             (helpers/last-zone? :goalkeeper false false) => true
             (helpers/last-zone? :defense false true) => false
             (helpers/last-zone? :penalty-box false true) => true))

(facts "Testing helpers/opposite-zone function"
       (fact "Return keyword of zone that is equivalent to
       provided zone, but in perspective of opposite team"
             (helpers/opposite-zone :attack) => :defense
             (helpers/opposite-zone :penalty-box) => :defense
             (helpers/opposite-zone :goalkeeper) => :attack
             (helpers/opposite-zone :defense) => :attack
             (helpers/opposite-zone :midfield) => :midfield))

(facts "Testing helpers/resolve-player-zone function"
       (fact "Return keyword of zone that can be found in
       players map - contained in team map"
             (helpers/resolve-player-zone :attack) => :attack
             (helpers/resolve-player-zone :penalty-box) => :attack
             (helpers/resolve-player-zone :goalkeeper) => :goalkeeper
             (helpers/resolve-player-zone :defense) => :defense
             (helpers/resolve-player-zone :midfield) => :midfield))

(facts "Testing helpers/players-in-zones function"
       (fact "Return list of all players contained in provided zones
       for provided team"
             (helpers/players-in-zones mock-match-test :home [:midfield :attack])
             => '({:id 6 :c "c"} {:id 7 :c "c"} {:id 8 :c "c"}
                 {:id 9 :e "e"} {:id 10 :f "f"} {:id 11 :g "g"})

             (helpers/players-in-zones mock-match-test :home [:attack :penalty-box])
             => '({:id 9 :e "e"} {:id 10 :f "f"} {:id 11 :g "g"})

             (helpers/players-in-zones mock-match-test :away [:goalkeeper :defense])
             => '({:id 12 :a "a"} {:id 13 :b "b"} {:id 14 :b "b"} {:id 15 :b "b"} {:id 16 :b "b"})

             (helpers/players-in-zones mock-match-test :away [:goalkeeper :penalty-box])
             => '({:id 12 :a "a"} {:id 20 :d "d"} {:id 21 :d "d"} {:id 22 :d "d"})))

(facts "Testing helpers/remove-from-zone function"
       (fact "Remove player with provided id from zone"
             (helpers/remove-from-zone mock-match-test :home [:attack] 11)
             => (assoc-in mock-match-test [:home :team :players :attack] [{:id 9 :e "e"} {:id 10 :f "f"}])

             (helpers/remove-from-zone mock-match-test :away [:goalkeeper] 12)
             => (assoc-in mock-match-test [:away :team :players :goalkeeper] [])

             (helpers/remove-from-zone mock-match-test :home [:goalkeeper :defense] 2)
             => (-> mock-match-test
                    (assoc-in [:home :team :players :goalkeeper] [{:id 1 :a "a"}])
                    (assoc-in [:home :team :players :defense] [{:id 3 :b "b"} {:id 4 :b "b"} {:id 5 :b "b"}]))))

(facts "Testing helpers/rand-player function"
       (fact "Return randomly picked player from provided team and zone"
             (with-redefs [rand-int (fn [_] 2)]
               (helpers/rand-player mock-match-test :home :attack)
               => {:id 11 :g "g"}

               (helpers/rand-player mock-match-test :home :goalkeeper)
               => nil

               (helpers/rand-player (assoc-in mock-match-test [:home :team :players :goalkeeper] []) :home :goalkeeper)
               => nil

               (helpers/rand-player mock-match-test :away :penalty-box)
               => (throws NullPointerException))))

(facts "Testing helpers/new-ball-holder-resume-game function"
       (fact "Used to return randomly picked player from provided team and zone.
       If there are no players in zone, player wil be chosen from previous zone in order."
             (with-redefs [rand-nth (fn [coll] (first coll))]
               (helpers/new-ball-holder-resume-game mock-match-test :home :attack)
               => {:id 9 :e "e"}

               (helpers/new-ball-holder-resume-game
                 (assoc-in mock-match-test [:home :team :players :attack] []) :home :attack)
               => {:id 6 :c "c"}

               (helpers/new-ball-holder-resume-game
                 (assoc-in mock-match-test [:away :team :players :midfield] []) :away :midfield)
               => {:id 13 :b "b"}

               (helpers/new-ball-holder-resume-game mock-match-test :home :penalty-box)
               => {:id 9 :e "e"})))

(facts "Testing helpers/new-ball-holder function"
       (fact "Used to return randomly picked player, or nil value, from provided team and zones.
       If there are no players in picked zone from provided zones, function will return nil for sure. If there are players
       in picked zone, function will randomly return either player or nil value."
             (with-redefs [rand-nth (fn [coll] (second coll))]
               (helpers/new-ball-holder mock-match-test :home [:midfield :attack])
               => {:id 9 :e "e"}

               (helpers/new-ball-holder mock-match-test :home [:goalkeeper :midfield])
               => {:id 6 :c "c"}

               (helpers/new-ball-holder mock-match-test :home [:defense :penalty-box])
               => {:id 9 :e "e"})

             (with-redefs [rand-nth (fn [coll] (first coll))]
               (helpers/new-ball-holder mock-match-test :home [:goalkeeper :midfield])
               => nil)))

(facts "Testing helpers/new-ball-holder-safe function"
       (fact "Used to return randomly picked player, or nil value, from provided team and zone.
       If there are no players in zone, function will try to return player from opposite team, from opposite zone. If there are no players
       in that case, function will try to return player from provided team from next zone in ball direction."
             (with-redefs [helpers/new-ball-holder (fn [a b c] {:id 9 :e "e"})
                           helpers/rand-player (fn [a b c] {:id 13 :b "b"})]
               (helpers/new-ball-holder-safe
                 mock-match-test :home :attack))
             => {:team :home :zone :attack :player {:id 9 :e "e"} :opposite? false}

             (with-redefs [rand-nth (fn [coll] (last coll))]
               (helpers/new-ball-holder-safe
                 (-> mock-match-test
                     (assoc-in [:home :team :players :defense] [])
                     (assoc-in [:away :team :players :attack] [])) :home :defense))
             => {:team :home :zone :goalkeeper :player {:id 1 :a "a"} :opposite? false}

             (with-redefs [rand-nth (fn [coll] (last coll))]
               (helpers/new-ball-holder-safe
                 (-> mock-match-test
                     (assoc-in [:home :team :players :attack] [])) :home :penalty-box))
             => {:team :away :zone :goalkeeper :player {:id 12 :a "a"} :opposite? true}

             (with-redefs [rand-nth (fn [coll] (last coll))]
               (helpers/new-ball-holder-safe
                 (-> mock-match-test
                     (assoc-in [:home :team :players :attack] [])
                     (assoc-in [:away :team :players :defense] [])) :home :penalty-box))
             => {:team :away :zone :goalkeeper :player {:id 12 :a "a"} :opposite? true}

             ;(with-redefs [rand-nth (fn [coll] (last coll))]
             ;  (helpers/new-ball-holder-safe
             ;    (-> mock-match-test
             ;        (assoc-in [:home :team :players :attack] [])
             ;        (assoc-in [:away :team :players :defense] [])) :away :defense))
             ;=> {:team :away :zone :goalkeeper :player {:id 12 :a "a"} :opposite? true}
             ))

(facts "Testing helpers/choose-pl-max-attr function"
       (fact "Used to find player with maximum value of provided attribute from provided collection of players."
             (helpers/choose-pl-max-attr [{:id 10 :finishing 87 :passing 83 :shot-power 92}
                                          {:id 11 :finishing 92 :passing 93 :shot-power 84}
                                          {:id 9 :finishing 91 :passing 95 :shot-power 87}] :finishing)
                                         => {:id 11 :finishing 92 :passing 93 :shot-power 84}

             (helpers/choose-pl-max-attr [{:id 10 :finishing 87 :passing 83 :shot-power 92}
                                          {:id 11 :finishing 92 :passing 93 :shot-power 84}
                                          {:id 9 :finishing 91 :passing 95 :shot-power 87}] :shot-power)
             => {:id 10 :finishing 87 :passing 83 :shot-power 92}

             (helpers/choose-pl-max-attr [{:id 10 :finishing 87 :passing 83 :shot-power 92}
                                          {:id 11 :finishing 92 :passing 93 :shot-power 84}
                                          {:id 9 :finishing 91 :passing 95 :shot-power 87}] :passing)
             => {:id 9 :finishing 91 :passing 95 :shot-power 87}

             (helpers/choose-pl-max-attr [{:id 10 :finishing 87 :passing 83 :shot-power 92}
                                          {:id 11 :finishing 92 :passing 93 :shot-power 84}
                                          {:id 9 :finishing 91 :passing 95 :shot-power 87}] :anger)
             => (throws Exception)))

(facts "Testing helpers/choose-pl-for-event function"
       (fact "Used to find best player from team in possession to take event. If provided event is :shot, player with
       highest :finishing attribute value is picked, else player with highest :passing attribute value is picked."
             (helpers/choose-pl-for-event mock-match-test-players :shot)
             => {:finishing 82, :good-passes 0, :skill 87, :goals 0, :positioning 88, :speed 85,
                 :duels-won 0, :shot-power 68, :name "Sergio Busquets", :red-card 0,
                 :crosses 0, :shots-on-goal 0, :passes 0, :shots 0, :passing 88, :reflexes 10,
                 :defense 85, :strength 60, :offsides 0, :handling 10, :technique 86, :id 17,
                 :yellow-cards 0, :goal-keeping 10, :duels 0, :fouls 0, :attack 72, :saves 0}

             (helpers/choose-pl-for-event mock-match-test-players :pass)
             =>
             {:finishing 65, :good-passes 0, :skill 90, :goals 0, :positioning 90, :speed 70,
              :duels-won 0, :shot-power 72, :name "Xavi", :red-card 0, :crosses 0,
              :shots-on-goal 0, :passes 0, :shots 0, :passing 94, :reflexes 10, :defense 68,
              :strength 65, :offsides 0, :handling 10, :technique 94, :id 18, :yellow-cards 0,
              :goal-keeping 10, :duels 0, :fouls 0, :attack 78, :saves 0}

             (helpers/choose-pl-for-event (assoc mock-match-test-players :possession :away) :shot)
             => {:finishing 86, :good-passes 0, :skill 86, :goals 0, :positioning 80, :speed 82,
                 :duels-won 0, :shot-power 70, :name "Sami Khedira", :red-card 0, :crosses 0,
                 :shots-on-goal 0, :passes 0, :shots 0, :passing 78, :reflexes 10, :defense 84,
                 :strength 72, :offsides 0, :handling 10, :technique 74, :id 6, :yellow-cards 0,
                 :goal-keeping 10, :duels 0, :fouls 0, :attack 75, :saves 0}

             (helpers/choose-pl-for-event (assoc mock-match-test-players :possession :away) :pass)
             => {:finishing 65, :good-passes 0, :skill 88, :goals 0, :positioning 88, :speed 70,
                 :duels-won 0, :shot-power 72, :name "Luka Modric", :red-card 0, :crosses 0,
                 :shots-on-goal 0, :passes 0, :shots 0, :passing 90, :reflexes 10, :defense 70,
                 :strength 78, :offsides 0, :handling 10, :technique 92, :id 7, :yellow-cards 0,
                 :goal-keeping 10, :duels 0, :fouls 0, :attack 78, :saves 0}))

(facts "Testing helpers/choose-foul-event function"
       (fact "Used to find event that will occur in order to resume foul, corner or penalty"
             (with-redefs [rand (fn [] 0.3)] (helpers/choose-foul-event :goalkeeper) => :pass)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-foul-event :goalkeeper) => :pass)

             (with-redefs [rand (fn [] 0.3)] (helpers/choose-foul-event :defense) => :pass)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-foul-event :defense) => :pass)

             (with-redefs [rand (fn [] 0.2)] (helpers/choose-foul-event :midfield) => :shot)

             (with-redefs [rand (fn [] 0.4)] (helpers/choose-foul-event :midfield) => :pass)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-foul-event :midfield) => :pass)

             (with-redefs [rand (fn [] 0.2)] (helpers/choose-foul-event :attack) => :pass)

             (with-redefs [rand (fn [] 0.3)] (helpers/choose-foul-event :attack) => :shot)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-foul-event :attack) => :shot)

             (with-redefs [rand (fn [] 0.01)] (helpers/choose-foul-event :corner) => :shot)

             (with-redefs [rand (fn [] 0.1)] (helpers/choose-foul-event :corner) => :pass)

             (with-redefs [rand (fn [] 0.97)] (helpers/choose-foul-event :corner) => :pass)

             (with-redefs [rand (fn [] 0.001)] (helpers/choose-foul-event :penalty-box) => :pass)

             (with-redefs [rand (fn [] 0.1)] (helpers/choose-foul-event :penalty-box) => :shot)

             (with-redefs [rand (fn [] 0.99)] (helpers/choose-foul-event :penalty-box) => :shot)))

(facts "Testing helpers/choose-pass-end-zone function"
       (fact "Used to randomly pick zone that pass should end in."
             (with-redefs [rand (fn [] 0.66)] (helpers/choose-pass-end-zone :defense)) => :midfield

             (with-redefs [rand (fn [] 0.7)] (helpers/choose-pass-end-zone :defense)) => :midfield

             (with-redefs [rand (fn [] 0.36)] (helpers/choose-pass-end-zone :defense))=> :defense

             (with-redefs [rand (fn [] 0.25)] (helpers/choose-pass-end-zone :defense))=> :goalkeeper

             (with-redefs [rand (fn [] 0.05)] (helpers/choose-pass-end-zone :defense))=> :attack))

(facts "Testing helpers/pass? function"
       (fact "Used to randomly decide if pass should be good or ball should be taken by opposite team."
             (with-redefs [rand (fn [] 0.1)] (helpers/pass? :midfield :penalty-box)) => false

             (with-redefs [rand (fn [] 0.1)] (helpers/pass? :midfield :goalkeeper)) => true

             (with-redefs [rand (fn [] 0.3)] (helpers/pass? :midfield :attack)) => true

             (with-redefs [rand (fn [] 0.5)] (helpers/pass? :midfield :attack)) => false

             (with-redefs [rand (fn [] 0.9)] (helpers/pass? :midfield :defense)) => false

             (with-redefs [rand (fn [] 0.8)] (helpers/pass? :midfield :midfield)) => false

             (with-redefs [rand (fn [] 0.6)] (helpers/pass? :midfield :midfield)) => true))

(facts "Testing helpers/out? function"
       (fact "Used to randomly decide if pass is sent out of the pitch."
             (with-redefs [rand-int (fn [a] 83)] (helpers/out? {:id 22 :name "Neymar" :passing 84})) => false

             (with-redefs [rand-int (fn [a] 84)] (helpers/out? {:id 22 :name "Neymar" :passing 84})) => false

             (with-redefs [rand-int (fn [a] 85)] (helpers/out? {:id 22 :name "Neymar" :passing 84})) => true))

(facts "Testing helpers/get-goal-prob function"
       (fact "Used to return goal probability value based on provided value."
             (helpers/get-goal-prob 21) => 0.9
             (helpers/get-goal-prob 20) => 0.6
             (helpers/get-goal-prob 19) => 0.6
             (helpers/get-goal-prob 0) => 0.3
             (helpers/get-goal-prob -1) => 0.3
             (helpers/get-goal-prob -19) => 0.3
             (helpers/get-goal-prob -20) => 0.1
             (helpers/get-goal-prob -21) => 0.1))

             (def barcelona {:name "Barcelona",
                 :players {:goalkeeper [{:name "Victor Valdes", :skill 87}],
                           :defense [{:name "Dani Alves", :skill 85}
                                     {:name "Gerard Pique", :skill 87}
                                     {:name "Javier Mascherano", :skill 85}
                                     {:name "Jordi Alba", :skill 83}],
                           :midfield [{:name "Sergio Busquets", :skill 87}
                                      {:name "Xavi", :skill 90}
                                      {:name "Andres Iniesta", :skill 91}],
                           :attack [{:name "Lionel Messi", :skill 94}
                                    {:name "Pedro", :skill 85}
                                    {:name "Neymar", :skill 87}]}})

(deftest make-match-test
  (testing "Make match"
    (with-redefs [rand-nth (fn [_] {:name "Karim Benzema", :skill 87})]
    (is (= match-exmpl-2 match-exmpl)))))

(deftest opposite-team-test
  (testing "Returns opposite team"
    (is (= :away (helpers/opposite-team :home)))
    (is (= :home (helpers/opposite-team :away)))))

(deftest new-ball-holder-2-test
  (testing "Pick and return new ball holder"
    (with-redefs [rand-nth (fn [coll] (first coll))]
      (is (= {:name "Dani Alves", :skill 85}
             (helpers/new-ball-holder-2 match-exmpl-2 :away :defense)))
      (is (= {:name "Sami Khedira", :skill 86}
             (helpers/new-ball-holder-2 match-exmpl-2 :home :midfield))))))

(deftest get-team-players-test
  (testing "Are team players returned correctly"
      (let [result (helpers/get-team-players barcelona)
            expected [{:name "Victor Valdes" :skill 87}
                      {:name "Dani Alves" :skill 85}
                      {:name "Gerard Pique" :skill 87}]]
        (is (= expected (take 3 result))))))

(deftest goal?-test
  (testing "Is goal logic good"
    (with-redefs [rand-int (fn [_] 90)]
      (is (= true (helpers/goal? {:name "Lionel Messi", :skill 94})))
      (is (= false (helpers/goal?  {:name "Neymar", :skill 87}))))))

(deftest pass?-test
  (testing "Is pass logic good"
    (with-redefs [rand-int (fn [_] 90)]
      (is (= true (helpers/pass? {:name "Lionel Messi", :skill 94})))
      (is (= false (helpers/pass?  {:name "Neymar", :skill 87}))))))

(deftest new-ball-holder-test
  (testing "Returns new ball holder after passing - must be
  different player than current ball-holder, cannot be the same"
    (with-redefs [rand-nth (fn [coll] (second coll))]
      (is (not= {:name "Karim Benzema", :skill 87}
                (helpers/new-ball-holder match-exmpl-2 :home :attack))))))

(deftest rand-zone-test
  (testing "Returns rand-zone from list of zones in rand-zone"
    (with-redefs [rand-nth (fn [coll] (second coll))]
      (is (= :midfield (helpers/rand-zone))))))

(def opp-zone-match-1 {:zone :attack})
(def opp-zone-match-2 {:zone :midfield})
(def opp-zone-match-3 {:zone :defense})

(deftest opposite-zone-test
  (testing "Returns zone from opposite-zone-map"
    (is (= :defense (helpers/opposite-zone opp-zone-match-1)))
    (is (= :midfield (helpers/opposite-zone opp-zone-match-2)))
    (is (= :attack (helpers/opposite-zone opp-zone-match-3)))))