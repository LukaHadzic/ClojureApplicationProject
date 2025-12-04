(ns clojure-application-project.core
  (:gen-class) (:require [clojure-application-project.db :as db]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (db/insertIntoTestTable))
