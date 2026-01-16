(ns clojure-application-project.events-test
  (:require [clojure.test :refer :all]))

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

;NPE and nil state are fixed. Added one more new-ball-holder function for pass event. Added opposite field zone pairs - e.g. :attack-:defense. Added new state - :resume, at start of match and after every goal. Fixed duel, finish-shot and finish-pass functions logic. Added tests for new helper functions.