(ns clojure-application-project.statistics)

(defn calc_accuracy [num_succ num_all] (* (/ num_succ num_all) 100))

