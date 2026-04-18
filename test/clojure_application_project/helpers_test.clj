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
              :kicked-players []}))

(def mock-match-test
  {:home {:team {:name "Barcelona"
                 :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
                 :players {:goalkeeper [{:id 1 :a "a"}]
                           :defense [{:id 2 :b "b"} {:id 3 :b "b"} {:id 4 :b "b"} {:id 5 :b "b"}]
                           :midfield [{:id 6 :c "c"} {:id 7 :c "c"} {:id 8 :c "c"}]
                           :attack [{:id 9 :e "e"} {:id 10 :f "f"} {:id 11 :g "g"}]}
                 :kicked-players []}
          :goals 0}
   :away {:team {:name "Real Madrid"
                 :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
                 :players {:goalkeeper [{:id 12 :a "a"}]
                  :defense [{:id 13 :b "b"} {:id 14 :b "b"} {:id 15 :b "b"} {:id 16 :b "b"}]
                  :midfield [{:id 17 :c "c"} {:id 18 :c "c"} {:id 19 :c "c"}]
                  :attack [{:id 20 :d "d"} {:id 21 :d "d"} {:id 22 :d "d"}]}
                 :kicked-players []}
          :goals 0}
   :minute 0
   :time 0
   :possession :home
   :zone :midfield
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
                            :kicked-players []}
                     :goals 0}
              :away {:team {:name "Real Madrid"
                            :formation {:goalkeeper 1 :defense 4 :midfield 3 :attack 3}
                            :players {:goalkeeper [{:a "a"}]
                                      :defense [{:b "b"} {:b "b"} {:b "b"} {:b "b"}]
                                      :midfield [{:c "c"} {:c "c"} {:c "c"}]
                                      :attack [{:d "d"} {:d "d"} {:d "d"}]}
                            :kicked-players []}
                     :goals 0}
              :minute 0
              :time 0
              :possession :home
              :zone :midfield
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
             (helpers/calc-avg 1 3) => 2
             (helpers/calc-avg 1 0) => 1/2
             (helpers/calc-avg 50 100) => 75
             (helpers/calc-avg 12 15) => 27/2))

(facts "Testing helpers/closer-value-to-first? function"
       (fact "Return true if random generated number in function
       is closer to first element or false if not"
             (with-redefs [rand-int (fn [_] 85)]
               (helpers/closer-value-to-first? 92 84) => false
               (helpers/closer-value-to-first? 87 89) => true
               (helpers/closer-value-to-first? 86 70) => true
               (helpers/closer-value-to-first? 80 89) => false)))

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
             )) ;PROMENITI

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
             => {:finishing 88, :good-passes 0, :skill 87, :goals 0, :positioning 85, :speed 85,
              :duels-won 0, :shot-power 68, :name "Gerard Pique", :red-card 0, :crosses 0,
              :shots-on-goal 0, :passes 0, :shots 0, :passing 75, :reflexes 10, :defense 88,
              :strength 65, :offsides 0, :handling 10, :technique 72, :id 14, :yellow-cards 0,
              :goal-keeping 10, :duels 0, :fouls 0, :attack 70, :saves 0}

             (helpers/choose-pl-for-event mock-match-test-players :pass)
             =>
             {:finishing 65, :good-passes 0, :skill 90, :goals 0, :positioning 90, :speed 70,
              :duels-won 0, :shot-power 72, :name "Xavi", :red-card 0, :crosses 0,
              :shots-on-goal 0, :passes 0, :shots 0, :passing 94, :reflexes 10, :defense 68,
              :strength 65, :offsides 0, :handling 10, :technique 94, :id 18, :yellow-cards 0,
              :goal-keeping 10, :duels 0, :fouls 0, :attack 78, :saves 0}

             (helpers/choose-pl-for-event (assoc mock-match-test-players :possession :away) :shot)
             => {:finishing 90, :good-passes 0, :skill 83, :goals 0, :positioning 82, :speed 80,
                 :duels-won 0, :shot-power 55, :name "Pepe", :red-card 0, :crosses 0,
                 :shots-on-goal 0, :passes 0, :shots 0, :passing 65, :reflexes 10, :defense 86,
                 :strength 72, :offsides 0, :handling 10, :technique 60, :id 3, :yellow-cards 0,
                 :goal-keeping 10, :duels 0, :fouls 0, :attack 55, :saves 0}

             (helpers/choose-pl-for-event (assoc mock-match-test-players :possession :away) :pass)
             => {:finishing 65, :good-passes 0, :skill 88, :goals 0, :positioning 88, :speed 70,
                 :duels-won 0, :shot-power 72, :name "Luka Modric", :red-card 0, :crosses 0,
                 :shots-on-goal 0, :passes 0, :shots 0, :passing 90, :reflexes 10, :defense 70,
                 :strength 78, :offsides 0, :handling 10, :technique 92, :id 7, :yellow-cards 0,
                 :goal-keeping 10, :duels 0, :fouls 0, :attack 78, :saves 0}))

(facts "Testing helpers/choose-resume-event function"
       (fact "Used to find event that will occur in order to resume foul, corner or penalty"
             (with-redefs [rand (fn [] 0.3)] (helpers/choose-resume-event :goalkeeper) => :pass)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-resume-event :goalkeeper) => :pass)

             (with-redefs [rand (fn [] 0.3)] (helpers/choose-resume-event :defense) => :pass)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-resume-event :defense) => :pass)

             (with-redefs [rand (fn [] 0.2)] (helpers/choose-resume-event :midfield) => :shot)

             (with-redefs [rand (fn [] 0.4)] (helpers/choose-resume-event :midfield) => :pass)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-resume-event :midfield) => :pass)

             (with-redefs [rand (fn [] 0.2)] (helpers/choose-resume-event :attack) => :pass)

             (with-redefs [rand (fn [] 0.3)] (helpers/choose-resume-event :attack) => :shot)

             (with-redefs [rand (fn [] 0.9)] (helpers/choose-resume-event :attack) => :shot)

             (with-redefs [rand (fn [] 0.01)] (helpers/choose-resume-event :corner) => :shot)

             (with-redefs [rand (fn [] 0.1)] (helpers/choose-resume-event :corner) => :pass)

             (with-redefs [rand (fn [] 0.97)] (helpers/choose-resume-event :corner) => :pass)

             (with-redefs [rand (fn [] 0.001)] (helpers/choose-resume-event :penalty-box) => :pass)

             (with-redefs [rand (fn [] 0.1)] (helpers/choose-resume-event :penalty-box) => :shot)

             (with-redefs [rand (fn [] 0.99)] (helpers/choose-resume-event :penalty-box) => :shot)))

(facts "Testing helpers/choose-pass-end-zone function"
       (fact "Used to randomly pick zone that pass should end in. First key which value from (zone pass-possibilities) map
       is bigger than random generated value is returned."
             (with-redefs [rand (fn [] 0.66)] (helpers/choose-pass-end-zone :defense)) => :midfield

             (with-redefs [rand (fn [] 0.7)] (helpers/choose-pass-end-zone :defense)) => :midfield

             (with-redefs [rand (fn [] 0.36)] (helpers/choose-pass-end-zone :defense))=> :defense

             (with-redefs [rand (fn [] 0.25)] (helpers/choose-pass-end-zone :defense))=> :goalkeeper

             (with-redefs [rand (fn [] 0.05)] (helpers/choose-pass-end-zone :defense))=> :attack))

(facts "Testing helpers/pass? function"
       (fact "If random generated value is smaller than probability from (zone-begin, zone-end) pair, true is returned."
             (with-redefs [rand (fn [] 0.1)] (helpers/pass? :midfield :penalty-box)) => false

             (with-redefs [rand (fn [] 0.1)] (helpers/pass? :midfield :goalkeeper)) => true

             (with-redefs [rand (fn [] 0.3)] (helpers/pass? :midfield :attack)) => true

             (with-redefs [rand (fn [] 0.5)] (helpers/pass? :midfield :attack)) => false

             (with-redefs [rand (fn [] 0.9)] (helpers/pass? :midfield :defense)) => false

             (with-redefs [rand (fn [] 0.8)] (helpers/pass? :midfield :midfield)) => false

             (with-redefs [rand (fn [] 0.6)] (helpers/pass? :midfield :midfield)) => true))

(facts "Testing helpers/offside? function"
       (fact "If random generated value is lower than chance that offside occurred from zone pass is sent from,
       offside occurs and true is returned."
             (with-redefs [rand (fn [] 0.3)] (helpers/offside? :goalkeeper)) => false
             (with-redefs [rand (fn [] 0.01)] (helpers/offside? :goalkeeper)) => true
             (with-redefs [rand (fn [] 0.2)] (helpers/offside? :defense)) => false
             (with-redefs [rand (fn [] 0.02)] (helpers/offside? :defense)) => true
             (with-redefs [rand (fn [] 0.2)] (helpers/offside? :midfield)) => false
             (with-redefs [rand (fn [] 0.01)] (helpers/offside? :midfield)) => true
             (with-redefs [rand (fn [] 0.3)] (helpers/offside? :attack)) => false
             (with-redefs [rand (fn [] 0.01)] (helpers/offside? :attack)) => true
             (with-redefs [rand (fn [] 0.35)] (helpers/offside? :penalty-box)) => false
             (with-redefs [rand (fn [] 0.05)] (helpers/offside? :penalty-box)) => true))

             (facts "Testing helpers/out? function"
       (fact "If random generated number is greater than :passing attribute of player, ball will be sent out of the pitch (returned true)."
             (with-redefs [rand-int (fn [a] 83)] (helpers/out? {:id 22 :name "Neymar" :passing 84})) => false

             (with-redefs [rand-int (fn [a] 84)] (helpers/out? {:id 22 :name "Neymar" :passing 84})) => false

             (with-redefs [rand-int (fn [a] 85)] (helpers/out? {:id 22 :name "Neymar" :passing 84})) => true))

(facts "Testing helpers/get-goal-prob function"
       (fact "For different x value, depending of interval it belongs to, different value is returned
       x > 20 -> 0.9 ; 0 < x < 20 -> 0.6 ; -20 < x < 0 -> 0.3 x < -20 -> 0.1."
             (helpers/get-goal-prob 21) => 0.9
             (helpers/get-goal-prob 20) => 0.6
             (helpers/get-goal-prob 19) => 0.6
             (helpers/get-goal-prob 0) => 0.3
             (helpers/get-goal-prob -1) => 0.3
             (helpers/get-goal-prob -19) => 0.3
             (helpers/get-goal-prob -20) => 0.1
             (helpers/get-goal-prob -21) => 0.1))

(facts "Testing helpers/shot-saved? function"
       (fact "Used to decide if shot is saved by goalkeeper or not."
             (with-redefs [rand (fn [] 0.3)]
               (helpers/shot-saved? {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                                    {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92})
               => true)
             (with-redefs [rand (fn [] 0.6)]
               (helpers/shot-saved? {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                                    {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92})
               => true)
             (with-redefs [rand (fn [] 0.8)]
               (helpers/shot-saved? {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                                    {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92})
               => false)))

(facts "Testing helpers/goal? function"
       (fact "Used to decide if taken shot was on target or not."
             (with-redefs [rand (fn [] 0.3)]
               (helpers/shot-on-goal? {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86})
               => true)
             (with-redefs [rand (fn [] 0.5)]
               (helpers/shot-on-goal? {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86})
               => true)
             (with-redefs [rand (fn [] 0.7)]
               (helpers/shot-on-goal? {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86})
               => false)))

(facts "Testing helpers/duel-won? function"
       (fact "Used to decide if pass ball holder won duel against opposite player or not. It calls
       calc-avg helper function to calculate average value of :strength and :speed attribute. Wich avg value is rand-int
       closer to that player won duel"
             (with-redefs [rand-int (fn [_] 76)]
               (helpers/duel-won?
                  {:id 22 :name "Neymar" :strength 90 :speed 65} ;77.5
                  {:id 4 :name "Ramos" :strength 78 :speed 85}) => true) ;81.5)
             (with-redefs [rand-int (fn [_] 79.5)]
               (helpers/duel-won?
                 {:id 22 :name "Neymar" :strength 90 :speed 65} ;77.5
                 {:id 4 :name "Ramos" :strength 78 :speed 85}) => false) ;81.5
             (with-redefs [rand-int (fn [_] 82)]
               (helpers/duel-won?
                 {:id 22 :name "Neymar" :strength 90 :speed 65} ;77.5
                 {:id 4 :name "Ramos" :strength 78 :speed 85}) => false)))

(facts "Testing helpers/foul? function"
       (fact "Used to decide if foul happened or not. If rand-int value is between two values of strength attributes,
       foul occures."
             (with-redefs [rand-int (fn [_] 82)]
               (helpers/foul?
                 {:id 22 :name "Neymar" :strength 90}
                 {:id 4 :name "Ramos" :strength 78}) => true)
             (with-redefs [rand-int (fn [_] 75)]
               (helpers/foul?
                 {:id 22 :name "Neymar" :strength 90}
                 {:id 4 :name "Ramos" :strength 78}) => false)
             (with-redefs [rand-int (fn [_] 91)]
               (helpers/foul?
                 {:id 22 :name "Neymar" :strength 90}
                 {:id 4 :name "Ramos" :strength 78}) => false)
             (with-redefs [rand-int (fn [_] 90)]
               (helpers/foul?
                 {:id 22 :name "Neymar" :strength 90}
                 {:id 4 :name "Ramos" :strength 78}) => false)))

(facts "Testing helpers/penalty? function"
       (fact "Used to decide if penalty happened or not. If zone is set to penalty-box when this function checks it,
       return value is true."
             (helpers/penalty? mock-match-test) => false
             (helpers/penalty? (assoc mock-match-test :zone :penalty-box)) => true))

(facts "Testing helpers/foul-attack? function"
       (fact "Used to decide if ball holder is the one who made foul or not. If rand-int value is closer to ball-holder's
       strength attribute value, ball holder made foul."
             (with-redefs [rand-int (fn [_] 85)]
               (helpers/foul-attack?
                 {:id 22 :name "Neymar" :strength 90}
                 {:id 4 :name "Ramos" :strength 78}) => true)
             (with-redefs [rand-int (fn [_] 70)]
               (helpers/foul-attack?
                 {:id 22 :name "Neymar" :strength 90}
                 {:id 4 :name "Ramos" :strength 78}) => false)
             (with-redefs [rand-int (fn [_] 92)]
               (helpers/foul-attack?
                 {:id 22 :name "Neymar" :strength 90}
                 {:id 4 :name "Ramos" :strength 78}) => true)))

(facts "Testing helpers/corner? function"
       (fact "Used to decide if corner occured after save or not."
             (with-redefs [rand (fn [] 0.2)]
               (helpers/corner? ;-16.5 -> 0.3
                 {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                 {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92}) => true)
             (with-redefs [rand (fn [] 0.4)]
               (helpers/corner? ;-16.5 -> 0.3
                 {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                 {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92}) => false)
             (with-redefs [rand (fn [] 0.3)]
               (helpers/corner? ;-16.5 -> 0.3
                 {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                 {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92}) => false)))

(facts "Testing helpers/catch? function"
       (fact "Used to decide if goalkeeper cought ball on save or not."
             (with-redefs [rand (fn [] 0.6)]
               (helpers/catch? ;-16.5 -> 0.3
                 {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                 {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92}) => true)
             (with-redefs [rand (fn [] 0.8)]
               (helpers/catch? ;-16.5 -> 0.3
                 {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                 {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92}) => false)
             (with-redefs [rand (fn [] 0.65)]
               (helpers/catch? ;-16.5 -> 0.3
                 {:id 22 :name "Neymar" :finishing 60 :technique 94 :shot-power 86}
                 {:id 1 :name "Casillas" :positioning 90 :reflexes 95 :handling 92}) => true)))

(def mock-match-test-min-players
  {:home {:team {:players {:goalkeeper [{:id 1 :passes 0 :duels 0}] :defense [{:id 2 :passes 0 :duels 0} {:id 3 :passes 0 :duels 0}]}}}
   :away {:team {:players {:goalkeeper [{:id 12 :passes 0 :duels 0}] :defense [{:id 13 :passes 0 :duels 0} {:id 14 :passes 0 :duels 0}]}}}
   :minute 0 :time 0 :possession :home :zone :defense :ball-holder {:id 2 :passes 0 :duels 0} :log {:home [] :away []}})

(facts "Testing helpers/inc-events function"
       (fact "Function should increment value of corresponding key provided that represents some of player's statistic
       parameters."
             (helpers/inc-events mock-match-test-min-players :home 2 [:passes :duels]) =>
                                 {:home {:team {:players {:goalkeeper [{:id 1 :passes 0 :duels 0}] :defense [{:id 2 :passes 1 :duels 1} {:id 3 :passes 0 :duels 0}]}}}
                                  :away {:team {:players {:goalkeeper [{:id 12 :passes 0 :duels 0}] :defense [{:id 13 :passes 0 :duels 0} {:id 14 :passes 0 :duels 0}]}}}
                                  :minute 0 :time 0 :possession :home :zone :defense :ball-holder {:id 2 :passes 1 :duels 1} :log {:home [] :away []}}
             (helpers/inc-events mock-match-test-min-players :away 13 [:duels]) =>
             {:home {:team {:players {:goalkeeper [{:id 1 :passes 0 :duels 0}] :defense [{:id 2 :passes 0 :duels 0} {:id 3 :passes 0 :duels 0}]}}}
              :away {:team {:players {:goalkeeper [{:id 12 :passes 0 :duels 0}] :defense [{:id 13 :passes 0 :duels 1} {:id 14 :passes 0 :duels 0}]}}}
              :minute 0 :time 0 :possession :home :zone :defense :ball-holder {:id 2 :passes 0 :duels 0} :log {:home [] :away []}}))

(facts "Testing helpers/get-card-prob function"
       (fact "Based on which interval provided value belongs to, function returns corresponding map of
       probabilities for red and yellow card"
             (helpers/get-card-prob 21) => {:red 0.15
                                            :yellow 0.35}
             (helpers/get-card-prob 20) => {:red 0.07
                                            :yellow 0.15}
             (helpers/get-card-prob 19) => {:red 0.07
                                            :yellow 0.15}
             (helpers/get-card-prob 0) => {:red 0.02
                                           :yellow 0.1}
             (helpers/get-card-prob -1) => {:red 0.02
                                            :yellow 0.1}
             (helpers/get-card-prob -19) => {:red 0.02
                                             :yellow 0.1}
             (helpers/get-card-prob -20) => {:red 0.005
                                             :yellow 0.05}
             (helpers/get-card-prob -21) => {:red 0.005
                                             :yellow 0.05}))

(facts "Testing helpers/should-get-card? function"
       (fact "Used to decide if player should get card and which."
             (with-redefs [rand (fn [] 0.5)]
               (helpers/should-get-card? ;-8.6 -> {:red 0.02 :yellow 0.1}
                 {:id 22 :name "Neymar" :strength 90 :speed 65 :technique 94} ;-> 23.65
                 {:id 4 :name "Ramos" :strength 78 :speed 85 :technique 70}) => {:get-card? false :card nil}) ;->  15.05
             (with-redefs [rand (fn [] 0.01)]
               (helpers/should-get-card? ;-8.6 -> {:red 0.02 :yellow 0.1}
                 {:id 22 :name "Neymar" :strength 90 :speed 65 :technique 94} ;-> 23.65
                 {:id 4 :name "Ramos" :strength 78 :speed 85 :technique 70}) => {:get-card? true :card :red}) ;->  15.05
             (with-redefs [rand (fn [] 0.08)]
               (helpers/should-get-card? ;-8.6 -> {:red 0.02 :yellow 0.1}
                 {:id 22 :name "Neymar" :strength 90 :speed 65 :technique 94} ;-> 23.65
                 {:id 4 :name "Ramos" :strength 78 :speed 85 :technique 70}) => {:get-card? true :card :yellow}))) ;->  15.05

(facts "Testing helpers/cross-next-zone? function"
       (fact "With helpers/get-goal-prob function are gathered probabilities for cross to happen,
       based on differences of :strength :speed and :technique attributes. Randomly, it's decided if cross happens."
        (with-redefs [rand (fn [] 0.2)]
                       (helpers/cross-next-zone? ; -0.4 -> 0.3
                         {:id 22 :name "Neymar" :strength 90 :speed 65 :technique 94}
                         {:id 4 :name "Ramos" :strength 78 :speed 85 :technique 70}) => true)
        (with-redefs [rand (fn [] 0.35)]
          (helpers/cross-next-zone? ; -0.4 -> 0.3
            {:id 22 :name "Neymar" :strength 90 :speed 65 :technique 94} ;78.7
            {:id 4 :name "Ramos" :strength 78 :speed 85 :technique 70}) => false) ;79.1
        (with-redefs [rand (fn [] 0.7)]
          (helpers/cross-next-zone? ; -0.4 -> 0.3
            {:id 22 :name "Neymar" :strength 90 :speed 65 :technique 94}
            {:id 4 :name "Ramos" :strength 78 :speed 85 :technique 70}) => false)))


(facts "Testing helpers/count-event function - For provided event keyword, this function counts how many times that
event happened, including events in kicked players."
       (fact "Provided event is :duel - no kicked players"
             (helpers/count-event (-> mock-match-test-min-players
                                      (assoc-in [:home :team :players :goalkeeper 0 :duels] 1)
                                      (assoc-in [:home :team :players :defense 0 :duels] 10)
                                      (assoc-in [:home :team :players :defense 1 :duels] 3)) :home :duels)
             => 14)

       (fact "Provided event is :passes - no kicked players"
             (helpers/count-event (-> mock-match-test-min-players
                                      (assoc-in [:home :team :players :defense 0 :passes] 1)
                                      (assoc-in [:home :team :players :defense 0 :duels] 3)
                                      (assoc-in [:home :team :players :defense 1 :passes] 5)) :home :passes)
             => 6)

       (fact "Provided event is :duel - there is kicked players"
             (helpers/count-event (-> mock-match-test-min-players
                                      (assoc-in [:home :team :players :goalkeeper 0 :duels] 1)
                                      (assoc-in [:home :team :players :defense 0 :duels] 10)
                                      (update-in [:home :team :kicked-players] conj {:id 4 :passes 1 :duels 2})
                                      (assoc-in [:home :team :players :defense 1 :duels] 3)) :home :duels)
             => 16)

       (fact "Provided event is :passes - there is kicked players"
             (helpers/count-event (-> mock-match-test-min-players
                                      (assoc-in [:home :team :players :goalkeeper 0 :passes] 1)
                                      (assoc-in [:home :team :players :defense 0 :passes] 10)
                                      (update-in [:home :team :kicked-players] conj {:id 4 :passes 1 :duels 2})
                                      (update-in [:home :team :kicked-players] conj {:id 5 :passes 11 :duels 0})
                                      (update-in [:home :team :kicked-players] conj {:id 6 :passes 0 :duels 0})
                                      (assoc-in [:home :team :players :defense 1 :passes] 3)) :home :passes)
             => 26))

(facts "Testing helpers/max-20-chars - Used to trim down strings longer than 20 chars to exactly 20 chars.
Strings with less than 20 chars are not affected."
       (fact "Provided string is 15 chars long - nothing should happen"
             (helpers/max-20-chars "0123456789abcde") => "0123456789abcde")
       (fact "Provided string is 20 chars long - nothing should happen"
             (helpers/max-20-chars "0123456789abcdefghij") => "0123456789abcdefghij")
       (fact "Provided string is 23 chars long - should be trimmed to 20 chars"
             (helpers/max-20-chars "0123456789abcdefghijklm") => "0123456789abcdefghij"))


(facts "Testing helpers/padd-with-spaces function. This function should return string contained as much blank spaces as
provided number divided by 2 and then subtracted by 2"
       (fact "Provided number is 10 and string should be 3 blank spaces"
             (helpers/padd-with-spaces 10) => "   ")
       (fact "Provided number is 11 and string should be 3 blank spaces"
             (helpers/padd-with-spaces 11) => "   ")
       (fact "Provided number is 12 and string should be 4 blank spaces"
             (helpers/padd-with-spaces 12) => "    "))