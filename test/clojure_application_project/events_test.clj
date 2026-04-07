(ns clojure-application-project.events-test
  (:require [clojure-application-project.events :as events]
            [clojure-application-project.helpers :as helpers]
            [midje.sweet :refer :all]))

(def mock-match-update-shot-test
  {:home {:team
          {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
  :away {:team
         {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
   :possession :home
   :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}})

(facts "testing events/update-shot function"
       (fact "If goal occurred, :shots, :shots-on-goal and :goals are incremented in
       player's statistic attributes, if not, only :shots is incremented"
             (events/update-shot mock-match-update-shot-test 1) =>
             {:home {:team {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 1 :shots 1 :shots-on-goal 1}]}} :goals 0}
              :away {:team {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :possession :home
              :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}}

             (events/update-shot mock-match-update-shot-test 0) =>
             {:home {:team {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 0}]}} :goals 0}
              :away {:team {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :possession :home
              :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}}))

(def mock-match-finish-shot
  {:home {:team
          {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
   :away {:team
          {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
   :possession :home
   :zone :attack
   :phase :attack
   :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
   :log {:home [] :away []}})

(facts "testing events/finish-shot function"
       (fact "Depending on if goal occurred, relevant attributes are updated and logs written. Shot isn't saved so
       it's eather goal or goal-out."
             (events/finish-shot mock-match-finish-shot :goal)
             => {:home {:team
                     {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 1 :shots 1 :shots-on-goal 1}]}} :goals 1}
              :away {:team
                     {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :possession :away
              :zone :attack
              :phase :resume
              :ball-holder {:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}
              :log {:home [:goal] :away [:conceded-goal]}}

             (events/finish-shot mock-match-finish-shot :miss)
             => {:home {:team
                     {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 0}]}} :goals 0}
              :away {:team
                     {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :possession :away
              :zone :goalkeeper
              :phase :goal-out
              :ball-holder {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}
              :log {:home [:miss] :away [:ball-won]}}))

(facts "testing events/goal function"
       (fact "This function only calls finish-shot function with :goal provided."
             (events/goal mock-match-finish-shot)
             => {:home {:team
                        {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 1 :shots 1 :shots-on-goal 1}]}} :goals 1}
                 :away {:team
                        {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :possession :away
                 :zone :attack
                 :phase :resume
                 :ball-holder {:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}
                 :log {:home [:goal] :away [:conceded-goal]}}))

(facts "testing events/miss function"
       (fact "This function only calls finish-shot function with :miss provided."
             (events/miss mock-match-finish-shot)
             => {:home {:team
                        {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 0}]}} :goals 0}
                 :away {:team
                        {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :possession :away
                 :zone :goalkeeper
                 :phase :goal-out
                 :ball-holder {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}
                 :log {:home [:miss] :away [:ball-won]}}))

(facts "testing events/shot-saved function"
       (fact "If corner happened, relevant match map attributes are updated. :possession opposite-team; :zone :attack; :phase :corner;
       logs -> :corner+:shot-saved-corner; :shots :shots-on-goal and :saves are incremented"
             (with-redefs [helpers/corner? (fn [a b] true)]
               (events/shot-saved mock-match-finish-shot)
               => {:home {:team
                          {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 1}]}} :goals 0}
                   :away {:team
                          {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 1}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                   :possession :home
                   :zone :attack
                   :phase :corner
                   :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
                   :log {:home [:corner] :away [:shot-saved-corner]}}))

       (fact "If corner didn't happened, goalkeeper saved the shot and ball is still on the pitch. Relevant match map
       attributes are updated, goalkeeper caught the ball. :possession opposite-team; :zone :goalkeeper; :phase :goalkeeper;
       logs -> :miss+:shot-saved; :ball-holder goalkeeper-player; :shots :shots-on-goal and :saves are incremented"
             (with-redefs [helpers/corner? (fn [a b] false)
                           helpers/catch? (fn [a b] true)]
               (events/shot-saved mock-match-finish-shot)
               => {:home {:team
                          {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 1}]}} :goals 0}
                   :away {:team
                          {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 1}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                   :possession :away
                   :zone :goalkeeper
                   :phase :goalkeeper
                   :ball-holder {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}
                   :log {:home [:miss] :away [:shot-saved]}}))


       (fact "If corner didn't happened, goalkeeper saved the shot and ball is still on the pitch. Goalkeeper didn't
       catch the ball. If any player from opposite team's defense is picked to be next ball holder, relevant attributes
       are updated: :possession opposite-team; :zone :defense; :phase :defense; logs -> :miss+:shot-saved;
       :ball-holder picked-player-from-opposite-defense; :shots :shots-on-goal and :saves are incremented"
             (with-redefs [helpers/corner? (fn [a b] false)
                           helpers/catch? (fn [a b] false)
                           ;helpers/new-ball-holder-safe (fn [state team zone]
                           ;                               {:team team :zone zone :player (first (get-in state [team :team :players zone])) :opposite? true})]
                           ;helpers/new-ball-holder (fn [_ _ _] {:id 14 :name "Gerard Pique"})]
                           helpers/new-ball-holder-safe (fn [_ _ _] {:team :away :zone :defense :player {:id 14 :name "Gerard Pique"} :opposite? true})]
               (events/shot-saved (update-in mock-match-finish-shot [:away :team :players] conj [:defense [{:id 14 :name "Gerard Pique"}]]))
               => {:home {:team
                          {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 1}]}} :goals 0}
                   :away {:team
                          {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 1}]
                                                       :defense [{:id 14 :name "Gerard Pique"}]
                                                       :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                   :possession :away
                   :zone :defense
                   :phase :defense
                   :ball-holder {:id 14 :name "Gerard Pique"}
                   :log {:home [:miss] :away [:shot-saved]}})))

(facts "Testing events/shot function"
       (fact "If shot is not saved, function checks if goal happened or shot is missed. First, goal happened and function
       calls events/goal function."
             (with-redefs [helpers/shot-saved? (fn [a b] false)
                           helpers/goal? (fn [a] true)
                           events/get-shot-duration (fn [a] 0.5)
                           rand (fn [] 0.2)]
               (events/shot mock-match-finish-shot)
               => (helpers/wrap-return (events/goal mock-match-finish-shot) (+ (rand) (events/get-shot-duration 0.5)))))

       (fact "If shot is not saved, function checks if goal happened or shot is missed. Now, shot is missed, and function
       calls events/miss function."
             (with-redefs [helpers/shot-saved? (fn [a b] false)
                           helpers/goal? (fn [a] false)
                           events/get-shot-duration (fn [a] 0.5)
                           rand (fn [] 0.2)]
               (events/shot mock-match-finish-shot)
               => (helpers/wrap-return (events/miss mock-match-finish-shot) (+ (rand) (events/get-shot-duration 0.5)))))

       (fact "If shot is saved, function calls events/shot-saved function."
             (with-redefs [helpers/shot-saved? (fn [a b] true)
                           events/get-shot-duration (fn [a] 0.5)
                           rand (fn [] 0.2)
                           helpers/corner? (fn [a b] false)
                           helpers/catch? (fn [a b] true)]
               (events/shot mock-match-finish-shot)
               => (helpers/wrap-return (events/shot-saved mock-match-finish-shot) (+ (rand) (events/get-shot-duration 0.5))))))

(facts "Testing events/update-pass function"
       (fact "Used to update statistic attributes of player who played pass. If pass is good, :passes and :good and
       :good-passes are updated"
             (events/update-pass (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) true)
             => (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 1 :good-passes 1))

       (fact "Used to update statistic attributes of player who played pass. If pass is bad, only :passes is updated"
             (events/update-pass (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) false)
             => (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 1 :good-passes 0)))

(facts "Testing events/finish-pass function"
       (fact "This function updates all of state data based on if pass is good or not. First, pass is good."
             (events/finish-pass (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                 :home :attack {:id 10 :name "Karim Benzema"} true)
             => {:home {:team
                        {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 1 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :away {:team
                        {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :possession :home
                 :zone :attack
                 :phase :attack
                 :ball-holder {:id 10 :name "Karim Benzema"}
                 :log {:home [:pass] :away []}})

       (fact "This function updates all of state data based on if pass is good or not. Now, pass is not good."
             (events/finish-pass (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                 :away :goalkeeper {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0} false)
             => {:home {:team
                        {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 0 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :away {:team
                        {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :possession :away
                 :zone :goalkeeper
                 :phase :goalkeeper
                 :ball-holder {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}
                 :log {:home [:pass-ball-lost] :away [:pass-ball-won]}}))

(facts "Testing events/resume-good-pass function"
       (fact "This function forces good pass - it's used to resume game, on kick off"
             (with-redefs [rand (fn [] 0.40)
                           events/get-pass-duration (fn [a b] 0.80)
                           helpers/new-ball-holder-resume-game (fn [a b c] {:id 9 :name "Cristiano Ronaldo"})]
               (events/resume-good-pass
                 (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack)
               => (just {:new-state {:home {:team
                          {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 1 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                   :away {:team
                          {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                   :possession :home
                   :zone :attack
                   :phase :attack
                   :ball-holder {:id 9 :name "Cristiano Ronaldo"}
                   :log {:home [:pass] :away []}}
                   :event-duration (roughly 1.2 1e-9)})))

       (fact "This function forces good pass - it's used to resume game, on kick off. If :penalty-box is provided as zone,
       it's the same as :attack zone is provided."
             (with-redefs [rand (fn [] 0.40)
                           events/get-pass-duration (fn [a b] 0.80)
                           helpers/new-ball-holder-resume-game (fn [a b c] {:id 9 :name "Cristiano Ronaldo"})]
               (events/resume-good-pass
                 (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :penalty-box)
               => (just {:new-state {:home {:team
                                      {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 1 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                               :away {:team
                                      {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                               :possession :home
                               :zone :attack
                               :phase :attack
                               :ball-holder {:id 9 :name "Cristiano Ronaldo"}
                               :log {:home [:pass] :away []}}
                   :event-duration (roughly 1.2 1e-9)}))))

(facts "Testing events/handle-good-pass function"
       (fact "If player from team in possession is picked for new ball holder it means that pass was good - finish-pass with true is called"
             (with-redefs [helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :attack
                                                           :player {:id 9 :name "Cristiano Ronaldo"}
                                                           :opposite? false})
                           events/get-pass-duration (fn [a b] 0.7)]
               (events/handle-good-pass
                 (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack)
               => {:new-state {:home {:team
                                            {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 1 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                                     :away {:team
                                            {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                                     :possession :home
                                     :zone :attack
                                     :phase :attack
                                     :ball-holder {:id 9 :name "Cristiano Ronaldo"}
                                     :log {:home [:pass] :away []}}
                         :event-duration 0.7}))
       (fact "If player from opposite team from team in possession is picked for new ball holder it means that pass was bad - finish-pass with false is called"
             (with-redefs [helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :away
                                                           :zone :defense
                                                           :player {:id 13 :name "Dani Alves"}
                                                           :opposite? true})
                           events/get-pass-duration (fn [a b] 1.5)]
               (events/handle-good-pass
                 (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack)
               => {:new-state {:home {:team
                                            {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 0 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                                     :away {:team
                                            {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                                     :possession :away
                                     :zone :defense
                                     :phase :defense
                                     :ball-holder {:id 13 :name "Dani Alves"}
                                     :log {:home [:pass-ball-lost] :away [:pass-ball-won]}}
                         :event-duration 1.5})))

(facts "Testing events/handle-bad-pass function"
       (fact "If player from provided team is picked for new ball holder, it means that pass was bad for sure, and
       finish-pass with false is called, with provided team's data"
             (with-redefs [helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :away
                                                           :zone :defense
                                                           :player {:id 13 :name "Dani Alves"}
                                                           :opposite? false})
                           events/get-pass-duration (fn [a b] 1.8)]
               (events/handle-bad-pass
                 (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack)
               => {:new-state {:home {:team
                                            {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 0 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                                     :away {:team
                                            {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                                     :possession :away
                                     :zone :defense
                                     :phase :defense
                                     :ball-holder {:id 13 :name "Dani Alves"}
                                     :log {:home [:pass-ball-lost] :away [:pass-ball-won]}}
                         :event-duration 1.8}))
       (fact "If player from provided team is not picked for new ball holder, it means that pass was not bad, and
       finish-pass with true is called, with data of team in possession."
             (with-redefs [helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :attack
                                                           :player {:id 9 :name "Cristiano Ronaldo"}
                                                           :opposite? true})
                           events/get-pass-duration (fn [a b] 0.7)]
               (events/handle-bad-pass
                 (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack)
               => {:new-state {:home {:team
                                      {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 1 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                               :away {:team
                                      {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                               :possession :home
                               :zone :attack
                               :phase :attack
                               :ball-holder {:id 9 :name "Cristiano Ronaldo"}
                               :log {:home [:pass] :away []}}
                   :event-duration 0.7})))

(facts "Testing events/offside function"
       (fact "When offside occurr, pass was bad, possession is changed to opposite team from team in possession,
       zone where offside happen is opponent's defense, phase is set to offside, and new ball holder is picked from
       opposite team's defense. In log of team which lost posession is written :offside. :offsides attribute is updated to
       player who was in offside"
             (with-redefs [helpers/new-ball-holder-resume-game (fn [a b c]
                                                                 (if (= c :attack)
                                                                   {:id 10 :name "Karim Benzema"}
                                                                   {:id 13 :name "Dani Alves"}))]
             (events/offside (-> mock-match-finish-shot
                                 (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                 (update-in [:home :team :players :attack] conj {:id 10 :name "Karim Benzema" :offsides 0}))))
             => {:home {:team
                        {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 0 :goals 0 :shots 0 :shots-on-goal 0}
                                                                {:id 10 :name "Karim Benzema" :offsides 1}]}} :goals 0}
                 :away {:team
                        {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :possession :away
                 :zone :defense
                 :phase :offside
                 :ball-holder {:id 13 :name "Dani Alves"}
                 :log {:home [:offside] :away []}}))

(facts "Testing events/out function"
       (fact "Pass was bad and possession is changed to opposite team from team in possession. :zone is set to opposite
       zone from provided zone-end (zone where out happened, in perspective of team in possession) and :phase is set
       to :out. New ball holder should be picked from opposite zone, :passess of previous ball holder is updated.
       :out-ball-lost and :out-ball-won logs are written to corresponding teams"
             (with-redefs [helpers/new-ball-holder-resume-game (fn [a b c] {:id 13 :name "Dani Alves"})]
               (events/out (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack))
               => {:home {:team
                         {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :passes 1 :good-passes 0 :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                  :away {:team
                         {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                  :possession :away
                  :zone :defense
                  :phase :out
                  :ball-holder {:id 13 :name "Dani Alves"}
                  :log {:home [:out-ball-lost] :away [:out-ball-won]}}))

(facts "Testing events/pass function"
       (fact "It's decided that offside occured, pass function just calls offside function"
             (with-redefs [helpers/offside? (fn [a] true)
                           rand (fn [] 0.2)
                           events/get-pass-duration (fn [a b] 0.3)
                           helpers/new-ball-holder-resume-game (fn [a b c]
                                                                 (if (= c :attack)
                                                                   {:id 10 :name "Karim Benzema"}
                                                                   {:id 13 :name "Dani Alves"}))]
               (events/pass (-> mock-match-finish-shot
                                   (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                   (update-in [:home :team :players :attack] conj {:id 10 :name "Karim Benzema" :offsides 0})))
               => (helpers/wrap-return (events/offside (-> mock-match-finish-shot
                                        (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                        (update-in [:home :team :players :attack] conj {:id 10 :name "Karim Benzema" :offsides 0}))) 0.5)))

       (fact "It's decided that out occured, events/pass function just calls events/out function"
             (with-redefs [helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] true)
                           rand (fn [] 0.2)
                           events/get-pass-duration (fn [a b] 0.3)
                           helpers/new-ball-holder-resume-game (fn [a b c] {:id 13 :name "Dani Alves"})]
               (events/pass (-> mock-match-finish-shot
                                (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)))
               => (helpers/wrap-return (events/out (-> mock-match-finish-shot
                                                           (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)) :attack) 0.5)))

       (fact "If neither offside or out happened, this function checks if pass should be good. Now, pass should be good
       so events/pass calls events/handle-good-pass function."
             (with-redefs [helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] false)
                           helpers/pass? (fn [a b] true)
                           rand (fn [] 0.2)
                           events/get-pass-duration (fn [a b] 0.4)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :attack
                                                           :player {:id 10 :name "Karim Benzema"}
                                                           :opposite? false})]
               (events/pass (-> mock-match-finish-shot
                                (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)))
               => (events/handle-good-pass (-> mock-match-finish-shot
                                                       (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)) :attack)))

       (fact "If neither offside or out happened, this function checks if pass should be good. Now, pass should be bad so
       events/pass calls events/handle-bad-pass function."
             (with-redefs [helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] false)
                           helpers/pass? (fn [a b] false)
                           rand (fn [] 0.2)
                           events/get-pass-duration (fn [a b] 0.5)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :away
                                                           :zone :defense
                                                           :player {:id 13 :name "Dani Alves"}
                                                           :opposite? false})]
               (events/pass (-> mock-match-finish-shot
                                (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)))
               => (events/handle-bad-pass (-> mock-match-finish-shot
                                               (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)) :attack))))

(facts "Testing events/pass-no-offside function. It should behave the same as the events/pass function, just without
chance for offside to happen."
       (fact "It's decided that out didn't occur and that pass should be good, events/pass-no-offside function just
       calls events/handle-good-pass function"
             (with-redefs [helpers/out? (fn [a] false)
                           helpers/pass? (fn [a b] true)
                           helpers/choose-pass-end-zone (fn [a] :attack)
                           rand (fn [] 0.1)
                           events/get-pass-duration (fn [a b] 0.3)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :attack
                                                           :player {:id 9 :name "Cristiano Ronaldo"}
                                                           :opposite? false})]
               (events/pass-no-offside (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0))
               => (events/handle-good-pass (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack)))

       (fact "It's decided that out didn't occur and that pass should be bad, events/pass-no-offside function just
       calls events/handle-bad-pass function"
             (with-redefs [helpers/out? (fn [a] false)
                           helpers/pass? (fn [a b] false)
                           helpers/choose-pass-end-zone (fn [a] :attack)
                           rand (fn [] 0.1)
                           events/get-pass-duration (fn [a b] 0.3)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :away
                                                           :zone :defense
                                                           :player {:id 13 :name "Dani Alves"}
                                                           :opposite? false})]
               (events/pass-no-offside (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0))
               => (events/handle-bad-pass (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack)))

       (fact "It's decided that out occurred when resuming out, events/pass-no-offside function just
       calls events/out function"
             (with-redefs [helpers/out? (fn [a] true)
                           ;helpers/pass? (fn [a b] true)
                           helpers/choose-pass-end-zone (fn [a] :attack)
                           rand (fn [] 0.1)
                           events/get-pass-duration (fn [a b] 0.3)
                           helpers/new-ball-holder-resume-game (fn [a b c] {:id 13 :name "Dani Alves"})]
                           ;helpers/new-ball-holder-safe (fn [a b c]
                           ;                               {:team :home
                           ;                                :zone :attack
                           ;                                :player {:id 9 :name "Cristiano Ronaldo"}
                           ;                                :opposite? false})]
               (events/pass-no-offside (-> mock-match-finish-shot
                                           (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                           (assoc :phase :out)))
               => (helpers/wrap-return (events/out (update-in mock-match-finish-shot [:home :team :players :attack 0] assoc :passes 0 :good-passes 0) :attack) 0.4))))

       ;duel
;offside
;pass
;shot
;finish-pass
;finish-shot
;good-pass
;bad-pass
;goal
;miss
