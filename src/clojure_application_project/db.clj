(ns clojure-application-project.db
  (:require [next.jdbc :as jdbc]))

(def dbSpecs {
              :dbtype "mysql"
              :dbname "footballmatchsimulation"
              :host "localhost"
              :user "root"
              :password "root"
              :port 3306
              })

(defn initDb []
  (jdbc/execute! dbSpecs
                 ["CREATE TABLE IF NOT EXISTS teams
                 (idT INT AUTO_INCREMENT PRIMARY KEY,
                 name VARCHAR(50) NOT NULL)"])

  (jdbc/execute! dbSpecs
                 ["CREATE TABLE IF NOT EXISTS players
                 (idP INT AUTO_INCREMENT PRIMARY KEY,
                 name VARCHAR(50) NOT NULL,
                 skill INT NOT NULL,
                 idT INT,
                 FOREIGN KEY(idT) REFERENCES teams(idT))"]))
