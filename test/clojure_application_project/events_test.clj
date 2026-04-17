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
              :ball-holder {:id 11 :name "Gareth Bale" :goals 1 :shots 1 :shots-on-goal 1}}

             (events/update-shot mock-match-update-shot-test 0) =>
             {:home {:team {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 0}]}} :goals 0}
              :away {:team {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :possession :home
              :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 0}}))

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
              :log {:home [:miss] :away [:shot-ball-won]}}))

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
                 :log {:home [:miss] :away [:shot-ball-won]}}))

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
                   :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 1 :shots-on-goal 1}
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
                   :ball-holder {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 1}
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
       (fact "If shot is on target, function checks if it's saved or goal happened. First, goal happened and function
       calls events/goal function."
             (with-redefs [helpers/shot-on-goal? (fn [a] true)
                           helpers/shot-saved? (fn [a b] false)
                           events/get-shot-duration (fn [a] 0.5)
                           rand (fn [] 0.2)]
               (events/shot mock-match-finish-shot)
               => (helpers/wrap-return (events/goal mock-match-finish-shot) (+ (rand) (events/get-shot-duration 0.5)))))

       (fact "If shot is on target, function checks if it's saved or goal happened. Now, shot is missed, and function
       calls events/miss function."
             (with-redefs [helpers/shot-on-goal? (fn [a] false)
                           events/get-shot-duration (fn [a] 0.5)
                           rand (fn [] 0.2)]
               (events/shot mock-match-finish-shot)
               => (helpers/wrap-return (events/miss mock-match-finish-shot) (+ (rand) (events/get-shot-duration 0.5)))))

       (fact "If shot is saved, function calls events/shot-saved function."
             (with-redefs [helpers/shot-on-goal?(fn [a] true)
                           helpers/shot-saved? (fn [a b] true)
                           events/get-shot-duration (fn [a] 0.5)
                           rand (fn [] 0.2)
                           helpers/corner? (fn [a b] false)
                           helpers/catch? (fn [a b] true)]
               (events/shot mock-match-finish-shot)
               => (helpers/wrap-return (events/shot-saved mock-match-finish-shot) (+ (rand) (events/get-shot-duration 0.5))))))

(facts "Testing events/update-pass function"
       (fact "Used to update statistic attributes of player who played pass. If pass is good, :passes and :good and
       :good-passes are updated"
             (events/update-pass (-> mock-match-finish-shot
                                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) true)
             => (-> mock-match-finish-shot
                    (update-in [:home :team :players :attack 0] assoc :passes 1 :good-passes 1)
                    (update-in [:ball-holder] assoc :passes 1 :good-passes 1)))

       (fact "Used to update statistic attributes of player who played pass. If pass is bad, only :passes is updated"
             (events/update-pass (-> mock-match-finish-shot
                                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) false)
             => (-> mock-match-finish-shot
                    (update-in [:home :team :players :attack 0] assoc :passes 1 :good-passes 0)
                    (update-in [:ball-holder] assoc :passes 1 :good-passes 0))))

(facts "Testing events/finish-pass function"
       (fact "This function updates all of state data based on if pass is good or not. First, pass is good."
             (events/finish-pass (-> mock-match-finish-shot
                                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0))
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
             (events/finish-pass (-> mock-match-finish-shot
                                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0))
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
                 (-> mock-match-finish-shot
                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)
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
                 (-> mock-match-finish-shot
                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :penalty-box)
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
                 (-> mock-match-finish-shot
                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)
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
                 (-> mock-match-finish-shot
                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)
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
                 (-> mock-match-finish-shot
                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)
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
                 (-> mock-match-finish-shot
                     (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                     (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)
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
                                 (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
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
               (events/out (-> mock-match-finish-shot
                               (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                               (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack))
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
                                (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                (update-in [:home :team :players :attack] conj {:id 10 :name "Karim Benzema" :offsides 0})))
               => (helpers/wrap-return (events/offside (-> mock-match-finish-shot
                                        (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                        (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                        (update-in [:home :team :players :attack] conj {:id 10 :name "Karim Benzema" :offsides 0})
                                        (update-in [:ball-holder] assoc :offsides 0))) 0.5)))

       (fact "It's decided that out occured, events/pass function just calls events/out function"
             (with-redefs [helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] true)
                           rand (fn [] 0.2)
                           events/get-pass-duration (fn [a b] 0.3)
                           helpers/new-ball-holder-resume-game (fn [a b c] {:id 13 :name "Dani Alves"})]
               (events/pass (-> mock-match-finish-shot
                                (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                (update-in [:ball-holder] assoc :passes 0 :good-passes 0)))
               => (helpers/wrap-return (events/out (-> mock-match-finish-shot
                                                       (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                                       (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack) 0.5)))

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
                                (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                (update-in [:ball-holder] assoc :passes 0 :good-passes 0)))
               => (events/handle-good-pass (-> mock-match-finish-shot
                                               (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                               (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)))

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
                                (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                (update-in [:ball-holder] assoc :passes 0 :good-passes 0)))
               => (events/handle-bad-pass (-> mock-match-finish-shot
                                              (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                              (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack))))

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
               (events/pass-no-offside (-> mock-match-finish-shot
                                           (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                           (update-in [:ball-holder] assoc :passes 0 :good-passes 0)))
               => (events/handle-good-pass (-> mock-match-finish-shot
                                               (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                               (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)))

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
               (events/pass-no-offside (-> mock-match-finish-shot
                                           (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                           (update-in [:ball-holder] assoc :passes 0 :good-passes 0)))
               => (events/handle-bad-pass (-> mock-match-finish-shot
                                              (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                              (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack)))

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
                                           (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                           (assoc :phase :out)))
               => (helpers/wrap-return (events/out (-> mock-match-finish-shot
                                                       (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                                       (update-in [:ball-holder] assoc :passes 0 :good-passes 0)) :attack) 0.4))))

(facts "Testing events/set-new-goalkeeper function"
       (fact "After helper function chooses player with maximum :goal-keeping attribute, that player is set to be new
       goalkeeper. That player is also removed from it's previous zone. Note that in this test there is side-effect,
       in [:away :team :players] map are added :defense [] and :midfield [] key-value pairs - doesn't affect simulation."
             (with-redefs [helpers/choose-pl-max-attr (fn [a b] {:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :saves 0})]
               (events/set-new-goalkeeper
                 (assoc-in mock-match-finish-shot [:away :team :players :goalkeeper] []) :away)
               => {:home {:team
                          {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                   :away {:team
                          {:name "Barcelona" :players {:goalkeeper [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}]
                                                       :defense []
                                                       :midfield []
                                                       :attack []}} :goals 0}
                   :possession :home
                   :zone :attack
                   :phase :attack
                   :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
                   :log {:home [] :away []}})))

(facts "Testing events/kick-player function"
       (fact "Player with provided id, from provided team is kicked to [team :team :kicked-players]. Note the side effect
       for this particular test, in [:away :team :players] are added all other zones player wasn't found in, like
       :defense [] and :midfield [] key-value pairs. This doesn't affect simulation."
             (events/kick-player mock-match-finish-shot :away 22)
             => {:home {:team
                        {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :away {:team
                        {:name "Barcelona"
                         :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}]
                                                     :defense []
                                                     :midfield []
                                                     :attack []}
                         :kicked-players '({:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0})} :goals 0}
                 :possession :home
                 :zone :attack
                 :phase :attack
                 :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
                 :log {:home [] :away []}})
       (fact "If goalkeeper is player that is kicked, automatically is called events/set-new-goalkeeper because there
       must be one goalkeeper per team."
             (with-redefs [helpers/choose-pl-max-attr (fn [a b] {:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :saves 0})]
             (events/kick-player mock-match-finish-shot :away 12)
             => {:home {:team
                        {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                 :away {:team
                        {:name "Barcelona"
                         :players {:goalkeeper [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}]
                                   :defense []
                                   :midfield []
                                   :attack []}
                         :kicked-players '({:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0})} :goals 0}
                 :possession :home
                 :zone :attack
                 :phase :attack
                 :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
                 :log {:home [] :away []}})))

(facts "Testing events/get-card function"
       (fact "Player got red card. If player didn't have any yellow cards, only :red-card is updated and function calls
       events/kick-player function to kick player.If player had one yellow card, :yellow-cards :red-card are updated, and
       function calls events/kick-player function to kick player."
             (events/get-card (update-in mock-match-finish-shot [:away :team :players :attack 0] assoc :yellow-cards 0 :red-card 0) :away {:id 22 :name "Neymar" :yellow-cards 0 :red-card 0} :red)
             =>
             {:home {:team
                       {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                :away {:team
                       {:name "Barcelona"
                        :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}]
                                  :defense []
                                  :midfield []
                                  :attack []}
                        :kicked-players '({:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :yellow-cards 0 :red-card 1})} :goals 0}
                :possession :home
                :zone :attack
                :phase :attack
                :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
                :log {:home [] :away [:red-card]}}

             (events/get-card (update-in mock-match-finish-shot [:away :team :players :attack 0] assoc :yellow-cards 1 :red-card 0) :away {:id 22 :name "Neymar" :yellow-cards 1 :red-card 0} :red)
             =>
             {:home {:team
                     {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}]
                                :defense []
                                :midfield []
                                :attack []}
                      :kicked-players '({:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :yellow-cards 2 :red-card 1})} :goals 0}
              :possession :home
              :zone :attack
              :phase :attack
              :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
              :log {:home [] :away [:yellow-card :red-card]}})

       (fact "If player didn't have any yellow cards, and got yellow card, only :yellow-cards is updated."
             (events/get-card (update-in mock-match-finish-shot [:away :team :players :attack 0] assoc :yellow-cards 0 :red-card 0) :away {:id 22 :name "Neymar" :yellow-cards 0 :red-card 0} :yellow)
             =>
             {:home {:team
                     {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}]
                                :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :yellow-cards 1 :red-card 0}]}} :goals 0}
              :possession :home
              :zone :attack
              :phase :attack
              :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
              :log {:home [] :away [:yellow-card]}})

       (fact "If player had one yellow card, and got another one, :yellow-cards and :red-card are updated and function
       calls events/kick player to kick player. :ball-holder map isn't updated - doesn't affect simulation."
             (events/get-card (update-in mock-match-finish-shot [:away :team :players :attack 0] assoc :yellow-cards 1 :red-card 0) :away {:id 22 :name "Neymar" :yellow-cards 1 :red-card 0} :yellow)
             =>
             {:home {:team
                     {:name "Real madrid" :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}]
                                :defense []
                                :midfield []
                                :attack []}
                      :kicked-players '({:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0 :yellow-cards 2 :red-card 1})} :goals 0}
              :possession :home
              :zone :attack
              :phase :attack
              :ball-holder {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0}
              :log {:home [] :away [:yellow-card :red-card]}}))

(def mock-match-duel
  {:home {:team
          {:name "Real madrid"
           :players {:attack [{:id 11 :name "Gareth Bale" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
          :goals 0}
   :away {:team
          {:name "Barcelona"
           :players {:goalkeeper [{:id 12 :name "Victor Valdes" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]
                     :defense [{:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
          :goals 0}
   :possession :home
   :zone :attack
   :phase :attack
   :ball-holder {:id 11 :name "Gareth Bale" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}
   :log {:home [] :away []}})

(facts "Testing events/update-duel function"
       (fact "If ball-holder won duel, it's :duels and :duels-won attributes are updated. Opponent's :duels attribute is
       updated. This function doesn't update state map attributes, just player's."
             (events/update-duel mock-match-duel true {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
             =>
             ;(-> mock-match-duel
             ;    (update-in [:home :team :players :attack 0 :duels] inc)
             ;    (update-in [:home :team :players :attack 0 :duels-won] inc)
             ;    (update-in [:away :team :players :defense 0 :duels] inc)))
             {:home {:team
                     {:name "Real madrid"
                      :players {:attack [{:id 11 :name "Gareth Bale" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]
                                :defense [{:id 13 :name "Dani Alves" :duels 1 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :possession :home
              :zone :attack
              :phase :attack
              :ball-holder {:id 11 :name "Gareth Bale" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}
              :log {:home [] :away []}})
       (fact "If ball-holder lost duel, it's :duels attribute is updated. Opponent's :duels and :duels-won attributes are
       updated. This function doesn't update state map attributes, just player's."
             (events/update-duel mock-match-duel false {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
             =>
             ;(-> mock-match-duel
             ;    (update-in [:away :team :players :defense 0 :duels] inc)
             ;    (update-in [:away :team :players :defense 0 :duels-won] inc)
             ;    (update-in [:home :team :players :attack 0 :duels] inc))))
             {:home {:team
                     {:name "Real madrid"
                      :players {:attack [{:id 11 :name "Gareth Bale" :duels 1 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]
                                :defense [{:id 13 :name "Dani Alves" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :possession :home
              :zone :attack
              :phase :attack
              :ball-holder {:id 11 :name "Gareth Bale" :duels 1 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}
              :log {:home [] :away []}}))

(facts "Testing events/finish-duel function"
       (fact "If duel is lost, player's corresponding attributes are updated, and state map's as well."
             (events/finish-duel mock-match-duel false {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} false)
             =>
             ;(-> mock-match-duel
             ;    (update-in [:log :home] conj :duel-lost)
             ;    (update-in [:log :away] conj :duel-won)
             ;    (assoc :possession :away)
             ;    (assoc :phase :defense)
             ;    (assoc :zone :defense)
             ;    (helpers/inc-events :home 11 [:duels])
             ;    (helpers/inc-events :away 13 [:duels :duels-won])
             ;    (assoc :ball-holder {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})))
             {:home {:team
                     {:name "Real madrid"
                      :players {:attack [{:id 11 :name "Gareth Bale" :duels 1 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]
                                :defense [{:id 13 :name "Dani Alves" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :possession :away
              :zone :defense
              :phase :defense
              :ball-holder {:id 13 :name "Dani Alves" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}
              :log {:home [:duel-lost] :away [:duel-won]}})

       (fact "If duel is won, ball holder's corresponding attributes are updated, and state map's as well. Cross didn't happen."
             (events/finish-duel mock-match-duel true {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} false)
             =>
             ;(-> mock-match-duel
             ;    (update-in [:log :away] conj :duel-lost)
             ;    (update-in [:log :home] conj :duel-won)
             ;    (assoc :possession :home)
             ;    (assoc :phase :attack)
             ;    (assoc :zone :attack)
             ;    (helpers/inc-events :home 11 [:duels :duels-won])
             ;    (helpers/inc-events :away 13 [:duels])
             ;    (assoc :ball-holder {:id 11 :name "Gareth Bale" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}))))
             {:home {:team
                     {:name "Real madrid"
                      :players {:attack [{:id 11 :name "Gareth Bale" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]
                                :defense [{:id 13 :name "Dani Alves" :duels 1 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :possession :home
              :zone :attack
              :phase :attack
              :ball-holder {:id 11 :name "Gareth Bale" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}
              :log {:home [:duel-won] :away [:duel-lost]}})

       (fact "If duel is won, ball holder's corresponding attributes are updated, and state map's as well. Cross happened -
       :phase and :zone are set to next-zone in order. :ball-holder's attributes aren't updated - doesn't affect simulation."
             (events/finish-duel mock-match-duel true {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} true)
             =>
             {:home {:team
                     {:name "Real madrid"
                      :players {:attack [{:id 11 :name "Gareth Bale" :duels 1 :duels-won 1 :fouls 0 :crosses 1 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :away {:team
                     {:name "Barcelona"
                      :players {:goalkeeper [{:id 12 :name "Victor Valdes" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]
                                :defense [{:id 13 :name "Dani Alves" :duels 1 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                     :goals 0}
              :possession :home
              :zone :penalty-box
              :phase :penalty-box
              :ball-holder {:id 11 :name "Gareth Bale" :duels 1 :duels-won 1 :fouls 0 :crosses 1 :yellow-cards 0 :red-card 0}
              :log {:home [:duel-won :cross] :away [:duel-lost]}}))

(facts "Testing events/duel-won function"
       (fact "This function just calls events/finish-duel function with true value provided for
       is-duel-won attribute. Cross didn't happen."
             (events/duel-won mock-match-duel {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} false)
             => (events/finish-duel mock-match-duel true {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} false))

       (fact "This function just calls events/finish-duel function with true value provided for
       is-duel-won attribute. Cross happened."
       (events/duel-won mock-match-duel {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} true)
       => (events/finish-duel mock-match-duel true {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} true)))

(facts "Testing events/duel-lost function"
       (fact "This function just calls events/finish-duel function with false value provided for
       is-duel-won attribute. Cross can't happen."
             (events/duel-lost mock-match-duel {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
             => (events/finish-duel mock-match-duel false {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0} false)))

(facts "Testing events/resume-penalty function"
       (fact "This function decides which event will occur in order to take penalty. Now, event is shot, and player is
       Gareth Bale, who scores."
             (with-redefs [helpers/choose-resume-event (fn [a] :shot)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0})
                           helpers/shot-saved? (fn [a b] false)
                           helpers/shot-on-goal? (fn [a] true)
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]
               (events/resume-penalty (assoc mock-match-finish-shot :phase :penalty))
               => (events/shot mock-match-finish-shot)))

       (fact "This function decides which event will occur in order to take penalty. Now, event is shot, and player is
       Gareth Bale, who now misses."
             (with-redefs [helpers/choose-resume-event (fn [a] :shot)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0})
                           helpers/shot-saved? (fn [a b] false)
                           helpers/shot-on-goal? (fn [a] false)
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]
               (events/resume-penalty (assoc mock-match-finish-shot :phase :penalty))
               => (events/shot mock-match-finish-shot)))

       (fact "This function decides which event will occur in order to take penalty. Now, event is shot, shooter is
       Gareth Bale and goalkeeper saves the penalty."
             (with-redefs [helpers/choose-resume-event (fn [a] :shot)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0})
                           helpers/shot-saved? (fn [a b] true)
                           helpers/shot-on-goal? (fn [a] false)
                           helpers/corner? (fn [a b] true)
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]
               (events/resume-penalty (assoc mock-match-finish-shot :phase :penalty))
               => (events/shot mock-match-finish-shot)))

       (fact "This function decides which event will occur in order to take penalty. Now, event is pass, shooter is
       Gareth Bale."
             (with-redefs [helpers/choose-resume-event (fn [a] :pass)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0 :passes 0 :good-passes 0})
                           helpers/choose-pass-end-zone (fn [a] :attack)
                           helpers/pass? (fn [a b] true)
                           helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] false)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :attack
                                                           :player {:id 9 :name "Cristiano Ronaldo"}
                                                          :opposite? false})
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]
               (events/resume-penalty (-> mock-match-finish-shot
                                          (assoc :phase :penalty)
                                          (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                          (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                          (update-in [:home :team :players :attack] conj {:id 9 :name "Cristiano Ronaldo"})))
               => (events/pass (-> mock-match-finish-shot
                                   (update-in [:home :team :players :attack] conj {:id 9 :name "Cristiano Ronaldo"})
                                   (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                   (update-in [:ball-holder] assoc :passes 0 :good-passes 0))))))

(facts "Testing events/resume-corner function."
       (fact "In this function is chosen which player would take which event. After, that player is set to be new ball
       holder and event occur. Now pass occurs"
             (with-redefs [helpers/choose-resume-event (fn [a] :pass)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0 :passes 0 :good-passes 0})
                           helpers/choose-pass-end-zone (fn [a] :attack)
                           helpers/pass? (fn [a b] true)
                           helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] false)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :attack
                                                           :player {:id 9 :name "Cristiano Ronaldo"}
                                                           :opposite? false})
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]

               (events/resume-corner (-> mock-match-finish-shot
                                          (assoc :phase :corner)
                                          (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                          (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                          (update-in [:home :team :players :attack] conj {:id 9 :name "Cristiano Ronaldo"})))
               => (events/pass (-> mock-match-finish-shot
                                   (update-in [:home :team :players :attack] conj {:id 9 :name "Cristiano Ronaldo"})
                                   (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                   (update-in [:ball-holder] assoc :passes 0 :good-passes 0)))))

       (fact "In this function is chosen which player would take which event. After, that player is set to be new ball
       holder and event occur. Now shot occurs"
             (with-redefs [helpers/choose-resume-event (fn [a] :shot)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0})
                           helpers/shot-saved? (fn [a b] false)
                           helpers/shot-on-goal? (fn [a] true)
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]
               (events/resume-corner (assoc mock-match-finish-shot :phase :corner))
               => (events/shot mock-match-finish-shot))))

(facts "Testing events/resume-foul function"
       (fact "In this function is chosen which player would take which event. After, that player is set to be new ball
       holder and event occur. Now shot occurs"
             (with-redefs [helpers/choose-resume-event (fn [a] :shot)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0})
                           helpers/shot-saved? (fn [a b] false)
                           helpers/shot-on-goal? (fn [a] true)
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]
               (events/resume-foul (assoc mock-match-finish-shot :phase :foul))
               => (events/shot mock-match-finish-shot)))

       (fact "In this function is chosen which player would take which event. After, that player is set to be new ball
       holder and event occur. Now shot occurs"
             (with-redefs [helpers/choose-resume-event (fn [a] :pass)
                           helpers/choose-pl-max-attr (fn [a b] {:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0 :passes 0 :good-passes 0})
                           helpers/choose-pass-end-zone (fn [a] :attack)
                           helpers/pass? (fn [a b] true)
                           helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] false)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :attack
                                                           :player {:id 9 :name "Cristiano Ronaldo"}
                                                           :opposite? false})
                           rand (fn [] 0.18)
                           events/get-shot-duration (fn [a] 0.13)]

               (events/resume-corner (-> mock-match-finish-shot
                                         (assoc :phase :corner)
                                         (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                         (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                         (update-in [:home :team :players :attack] conj {:id 9 :name "Cristiano Ronaldo"})))
               => (events/pass (-> mock-match-finish-shot
                                   (update-in [:home :team :players :attack] conj {:id 9 :name "Cristiano Ronaldo"})
                                   (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                   (update-in [:ball-holder] assoc :passes 0 :good-passes 0))))))

;TESTIRATI FOUL, DUEL

(facts "Testing events/foul function"
       (fact "Attacking player made foul, didn't get any card"
             (with-redefs [helpers/foul-attack? (fn [a b] true)
                           helpers/should-get-card? (fn [a b] {:get-card? false :card nil})]
               (events/foul mock-match-duel {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
               =>
               {:home {:team
                       {:name "Real madrid"
                        :players {:attack [{:id 11 :name "Gareth Bale" :duels 1 :duels-won 0 :fouls 1 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                       :goals 0}
                :away {:team
                       {:name "Barcelona"
                        :players {:goalkeeper [{:id 12 :name "Victor Valdes" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]
                                  :defense [{:id 13 :name "Dani Alves" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}]}}
                       :goals 0}
                :possession :away
                :zone :defense
                :phase :foul
                :ball-holder {:id 13 :name "Dani Alves" :duels 1 :duels-won 1 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0}
                :log {:home [:foul] :away [:fouled]}}))

       (fact "Attacking player made foul, got yellow card"
             (with-redefs [helpers/foul-attack? (fn [a b] true)
                           helpers/should-get-card? (fn [a b] {:get-card? true :card :yellow})]
               (events/foul mock-match-duel {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
               =>
               (-> mock-match-duel
                   (assoc :phase :foul)
                   (assoc :possession :away)
                   (assoc :zone :defense)
                   (assoc :ball-holder {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
                   (update-in [:log :home] conj :yellow-card :foul)
                   (update-in [:log :away] conj :fouled)
                   (helpers/inc-events :home 11 [:duels :fouls :yellow-cards])
                   (helpers/inc-events :away 13 [:duels :duels-won]))))

       (fact "Defending player made foul, got yellow card and penalty happened"
             (with-redefs [helpers/foul-attack? (fn [a b] false)
                           helpers/should-get-card? (fn [a b] {:get-card? true :card :yellow})
                           helpers/penalty? (fn [a] true)]
               (events/foul (-> mock-match-duel (assoc :zone :penalty-box)) {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
               =>
               (-> mock-match-duel
                   (assoc :phase :penalty)
                   (assoc :possession :home)
                   (assoc :zone :penalty-box)
                   (update-in [:log :away] conj :yellow-card :foul-penalty)
                   (update-in [:log :home] conj :fouled-penalty)
                   (helpers/inc-events :away 13 [:duels :fouls :yellow-cards])
                   (helpers/inc-events :home 11 [:duels :duels-won]))))

       (fact "Defending player made foul, didn't get any card, penalty didn't occur"
             (with-redefs [helpers/foul-attack? (fn [a b] false)
                           helpers/should-get-card? (fn [a b] {:get-card? false :card nil})
                           helpers/penalty? (fn [a] false)]
               (events/foul mock-match-duel {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0 :crosses 0 :yellow-cards 0 :red-card 0})
               =>
               (-> mock-match-duel
                   (assoc :phase :foul)
                   (assoc :possession :home)
                   (assoc :zone :attack)
                   (update-in [:log :away] conj :foul)
                   (update-in [:log :home] conj :fouled)
                   (helpers/inc-events :away 13 [:duels :fouls])
                   (helpers/inc-events :home 11 [:duels :duels-won])))))

(facts "Testing events/duel function"
       (fact "If no opponent's player is picked, cross happens."
             (with-redefs [helpers/rand-player (fn [a b c] nil)
                           events/get-cross-duration (fn [a] 2.5)]
               (events/duel mock-match-duel) =>
               (helpers/wrap-return (-> mock-match-duel
                                        (assoc :zone :penalty-box)
                                        (assoc :phase :penalty-box)
                                        (helpers/inc-events :home 11 [:crosses])
                                        (update-in [:log :home] conj :cross)) (events/get-cross-duration :a))))

       (fact "If opponent's player is picked, function checks for foul, cross and is duel won. Now, just cross happens."
             (with-redefs [helpers/rand-player (fn [a b c] {:id 13 :name "Dani Alves" :duels 0 :duels-won 0 :fouls 0
                                                            :crosses 0 :yellow-cards 0 :red-card 0 :speed 72 :technique 84
                                                            :strength 88})
                           events/get-duel-duration (fn [a b] 2.8)
                           events/get-cross-duration (fn [a] 2)
                           rand (fn [a] 0.2)
                           helpers/cross-next-zone? (fn [a b] true)
                           helpers/foul? (fn [a b] false)
                           helpers/duel-won? (fn [a b] true)]
               (events/duel (-> mock-match-duel
                                (update-in [:home :team :players :attack 0] assoc :strength 94 :technique 88 :speed 82))) =>
               (helpers/wrap-return (-> mock-match-duel
                                        (update-in [:home :team :players :attack 0] assoc :strength 94 :technique 88 :speed 82)
                                        (assoc :zone :penalty-box)
                                        (assoc :phase :penalty-box)
                                        (helpers/inc-events :home 11 [:duels :duels-won :crosses])
                                        (helpers/inc-events :away 13 [:duels])
                                        (update-in [:log :home] conj :duel-won :cross)
                                        (update-in [:log :away] conj :duel-lost)) (+ (events/get-duel-duration :a :b)
                                                                                     (events/get-cross-duration :a)
                                                                                     (rand :a))))))

(facts "Testing events/resume-game function"
       (fact "This function just calls events/resume-good-pass function with chosen pass-end-zone from :midfield zone"
             (with-redefs [helpers/new-ball-holder-resume-game (fn [a b c] {:id 7 :name "Luka Modric"})
                           helpers/choose-pass-end-zone (fn [a] :midfield)
                           rand (fn [] 0.18)
                           events/get-pass-duration (fn [a b] 1.12)]
               (events/resume-game (-> mock-match-finish-shot
                                       (assoc :phase :resume)
                                       (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                       (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                       (update-in [:home :team :players] assoc :midfield [{:id 7 :name "Luka Modric"}])))
               =>
               {:new-state {:home {:team
                       {:name "Real madrid"
                        :players {:attack [{:id 11 :name "Gareth Bale" :goals 0 :shots 0 :shots-on-goal 0 :passes 1 :good-passes 1}]
                                  :midfield [{:id 7 :name "Luka Modric"}]}}
                       :goals 0}
                :away {:team
                       {:name "Barcelona" :players {:goalkeeper [{:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0}] :attack [{:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}]}} :goals 0}
                :possession :home
                :zone :midfield
                :phase :midfield
                :ball-holder {:id 7 :name "Luka Modric"}
                :log {:home [:pass] :away []}}
                :event-duration 1.3})))

(facts "Testing events/resume-goal-out"
       (fact "This function sets :phase to :goalkeeper and just calls events/pass function."
             (with-redefs [helpers/choose-pass-end-zone (fn [a] :attack)
                           helpers/pass? (fn [a b] true)
                           helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] false)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :away
                                                           :zone :attack
                                                           :player {:id 22 :name "Neymar" :goals 0 :shots 0 :shots-on-goal 0}
                                                           :opposite? false})
                           rand (fn [] 0.18)
                           events/get-pass-duration (fn [a b] 2.13)]

               (events/resume-goal-out (-> mock-match-finish-shot
                                           (assoc :possession :away)
                                           (assoc :phase :goal-out)
                                           (update-in [:away :team :players :goalkeeper 0] assoc :passes 0 :good-passes 0)
                                           (assoc :ball-holder {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0 :passes 0 :good-passes 0})))
               => (events/pass (-> mock-match-finish-shot
                                   (assoc :possession :away)
                                   (assoc :phase :goalkeeper)
                                   (update-in [:away :team :players :goalkeeper 0] assoc :passes 0 :good-passes 0)
                                   (assoc :ball-holder {:id 12 :name "Victor Valdes" :goals 0 :shots 0 :shots-on-goal 0 :saves 0 :passes 0 :good-passes 0}))))))

(facts "Testing events/resume-offside"
       (fact "This function just calls pass function, with pass-begin zone set to :defense."
             (with-redefs [helpers/choose-pass-end-zone (fn [a] :midfield)
                           helpers/pass? (fn [a b] true)
                           helpers/offside? (fn [a] false)
                           helpers/out? (fn [a] false)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :midfield
                                                           :player {:id 7 :name "Luka Modric"}
                                                           :opposite? false})
                           rand (fn [] 0.18)
                           events/get-pass-duration (fn [a b] 1.12)]

               (events/resume-offside (-> mock-match-finish-shot
                                          (update-in [:home :team :players] conj [:defense {:id 3 :name "Pepe" :passes 0 :good-passes 0}])
                                          (update-in [:home :team :players] conj [:midfield {:id 7 :name "Luka Modric"}])
                                          (assoc :zone :defense)
                                          (assoc :phase :offside)
                                          (assoc :ball-holder {:id 3 :name "Pepe" :passes 0 :good-passes 0})))
               => (events/pass (-> mock-match-finish-shot
                                   (update-in [:home :team :players] conj [:defense {:id 3 :name "Pepe" :passes 0 :good-passes 0}])
                                   (update-in [:home :team :players] conj [:midfield {:id 7 :name "Luka Modric"}])
                                   (assoc :possession :home)
                                   (assoc :phase :midfield)
                                   (assoc :ball-holder {:id 3 :name "Pepe" :passes 0 :good-passes 0}))))))

(facts "Testing events/resume-out function"
       (fact "This function just calls events/pass-no-offside funtion with :phase set to :out."
             (with-redefs [helpers/choose-pass-end-zone (fn [a] :midfield)
                           helpers/pass? (fn [a b] true)
                           helpers/out? (fn [a] false)
                           helpers/new-ball-holder-safe (fn [a b c]
                                                          {:team :home
                                                           :zone :midfield
                                                           :player {:id 7 :name "Luka Modric"}
                                                           :opposite? false})
                           rand (fn [] 0.18)
                           events/get-pass-duration (fn [a b] 1.12)]

               (events/resume-out (-> mock-match-finish-shot
                                          ;(update-in [:home :team :players] conj [:defense {:id 3 :name "Pepe" :passes 0 :good-passes 0}])
                                          (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                          (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                          (update-in [:home :team :players] conj [:midfield {:id 7 :name "Luka Modric"}])
                                          (assoc :zone :defense)
                                          (assoc :phase :out)))
                                          ;(assoc :ball-holder {:id 3 :name "Pepe" :passes 0 :good-passes 0})))
               => (events/pass-no-offside (-> mock-match-finish-shot
                                   ;(update-in [:home :team :players] conj [:defense {:id 3 :name "Pepe" :passes 0 :good-passes 0}])
                                   (update-in [:home :team :players :attack 0] assoc :passes 0 :good-passes 0)
                                   (update-in [:ball-holder] assoc :passes 0 :good-passes 0)
                                   (update-in [:home :team :players] conj [:midfield {:id 7 :name "Luka Modric"}])
                                   ;(assoc :possession :home)
                                   (assoc :phase :out))))))

(facts "Testing events/get-duel-duration function"
       (fact "Based on player's :strength and :speed attributes, duel duration is calculated with this math
       function: (* 3 (Math/pow 0.96656 diff)) where diff is gathered from this math function:
       (Math/sqrt (+ (* str-diff str-diff) (* sp-diff sp-diff))), str-diff is strength difference and sp-diff is
       speed difference of two player's attributes from duel."
             (events/get-duel-duration
               {:id 11 :name "Gareth Bale" :strength 94 :speed 82}
               {:id 13 :name "Dani Alves" :strength 88 :speed 72}) =>
             (* 3 (Math/pow 0.96656 (Math/sqrt (+ (* (- 94 88) (- 94 88)) (* (- 82 72) (- 82 72))))))))

(facts "Testing events/get-cross-duration function"
       (fact "Bigger the player's :speed attribute is, shorter the time cross takes. Cross is calculated by this function:
       (+ (* speed-factor base) (rand)), where speed-factor is calculated with (- 1 (/ speed 200)) and base is just scale
       coefficient set to 3."
             (with-redefs [rand (fn [] 0.25)]
               (events/get-cross-duration
                 {:id 11 :name "Gareth Bale" :speed 82}) =>
               (+ (* (- 1.0 (/ 82 200)) 3) 0.25))))

(facts "Testing events/get-pass-duration function"
       (fact "Depending of pass zone-begin and pass zone-end, pass duration is gathered from map events/pass-duration-map"
             (events/get-pass-duration :attack :midfield) => 1
             (events/get-pass-duration :goalkeeper :midfield) => 3.5
             (events/get-pass-duration :midfield :defense) => 1.5
             (events/get-pass-duration :defense :penalty-box) => 3.5
             (events/get-pass-duration :attack :goalkeeper) => 5
             (events/get-pass-duration :defense :defense) => 1
             (events/get-pass-duration :midfield :goalkeeper) => 3.5))

(facts "Testing events/get-shot-duration function"
       (fact "Depending of zone shot is taken from, shot duration is gathered from map events/shot-duration-map"
             (events/get-shot-duration :defense) => 3
             (events/get-shot-duration :midfield) => 1.8
             (events/get-shot-duration :attack) => 1
             (events/get-shot-duration :penalty-box) => 0.5))

(facts "Testing events/exp-rand function"
       (fact "Based on provided zone, this function calculates time that is used after for Poisson's distribution of
       events in simulation. Value for function is gathered from events/zone-lambda-map. With this function,
       time is calculated and returned: (/ (- (Math/log (- 1 (rand)))) zone-lambda), where zone-lambda is gathered value
       based on provided zone."
             (with-redefs [rand (fn [] 0.4)]
              (events/exp-rand :attack) => (/ (- (Math/log (- 1 (rand)))) (:attack events/zone-lambda-map)))))

(facts "Testing events/choose-event function"
       (fact "Based on provided :phase and events/phase-actions-controller map (event->probability map for all phases),
       this function chooses which event will occur in which phase."
             (with-redefs [rand (fn [] 0.4)]
               (= (events/choose-event :attack) (:pass events/event-mapper-2)) => true)

             (with-redefs [rand (fn [] 0.8)]
               (= (events/choose-event :attack) (:duel events/event-mapper-2)) => true)

             (with-redefs [rand (fn [] 0.95)]
               (= (events/choose-event :attack) (:shot events/event-mapper-2)) => true)

             (with-redefs [rand (fn [] 0.4)]
               (= (events/choose-event :offside) (:resume-offside events/event-mapper-2)) => true)

             (with-redefs [rand (fn [] 0.99)]
               (= (events/choose-event :offside) (:resume-offside events/event-mapper-2)) => true)))


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
