(ns clojure-application-project.db
  (:require [next.jdbc :as jdbc]))

(def dbSpecs {
              :dbType "mysql"
              :dbName "footballmatchsimulation"
              :host "localhost"
              :user "root"
              :password "root"
              :port 3306
              })

