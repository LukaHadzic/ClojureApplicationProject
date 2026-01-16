(ns clojure-application-project.helpers-test
  (:require [clojure.test :refer :all]
            [clojure-application-project.helpers :as helpers]))

(deftest make-player-test
  (testing "Make player"
    (is (= {:name "Ronaldo" :skill 97}
           (helpers/make-player "Ronaldo" 97)))))

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

(deftest make-team-test
  (testing "Make team"
    (is (= barcelona
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
                                        (helpers/make-player "Neymar" 87)]})))))

(def match-exmpl (helpers/make-match (helpers/make-team "Real Madrid"
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

(def match-exmpl-2 {:home {:team {:name "Real Madrid",
                                  :players {:goalkeeper [{:name "Iker Casillas", :skill 86}],
                                            :defense [{:name "Dani Carvajal", :skill 83}
                                                      {:name "Pepe", :skill 83}
                                                      {:name "Sergio Ramos", :skill 89}
                                                      {:name "Marcelo", :skill 85}],
                                            :midfield [{:name "Sami Khedira", :skill 86}
                                                       {:name "Luka Modric", :skill 88}
                                                       {:name "Angel Di Maria", :skill 88}],
                                            :attack [{:name "Cristiano Ronaldo", :skill 92}
                                                     {:name "Karim Benzema", :skill 87}
                                                     {:name "Gareth Bale", :skill 91}]}},
                           :goals 0},
                    :away {:team {:name "Barcelona",
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
                                                     {:name "Neymar", :skill 87}]}},
                           :goals 0},
                    :minute 0,
                    :possession :home,
                    :zone :midfield,
                    :ball-holder {:name "Karim Benzema", :skill 87},
                    :log {:home [], :away []}})

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