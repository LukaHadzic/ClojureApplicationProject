(ns clojure-application-project.db
  (:require [next.jdbc :as jdbc] ))

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

(defn initTestTable []
  (jdbc/execute! dbSpecs
                 ["CREATE TABLE IF NOT EXISTS testTable
                 (idTt INT AUTO_INCREMENT PRIMARY KEY,
                 cntVal INT,
                 sumVal INT)"]))

(defn alterTestTable []
  (jdbc/execute! dbSpecs
                 ["ALTER TABLE testTable
                 ADD COLUMN idT INT"]))

(defn get-table-column [table_name col_name]
  (jdbc/execute! dbSpecs
                 [(str "SELECT " col_name " FROM " table_name)]))


;(defn insertIntoTestTable []
;
;  (let [rows [[1 3 2 1]
;              [2 4 3 2]
;              [3 7 1 1]]]

   ;(jdbc/execute! dbSpecs
   ;               ["INSERT INTO testtable (idTt, cntVal, sumVal, idT)
   ;              VALUES(?, ?, ?, ?)"] {:multi? true :param rows})))

;(defn insertIntoTestTable []
;  (let [rows [{:inTt 1 :cntVal 3 :sumVal 2 :idT 1}
;              {:inTt 2 :cntVal 4 :sumVal 3 :idT 2}
;              {:inTt 3 :cntVal 7 :sumVal 1 :idT 1}]]
;    (jdbc/execute! dbSpecs
;                   ["INSERT INTO testTable (idTt, cntVal, sumVal, idT) VALUES (:idTt, :cntVal, :sumVal, :idT)"]
;                   {:multi? true
;                    :params rows})))



;(defn insertIntoTestTable []
;
;  (let [rows [[1 3 2 1]
;              [2 4 3 2]
;              [3 7 1 1]]]
;
;(batch/execute-batch! (jdbc/get-connection dbSpecs)
;               ["INSERT INTO testtable (idTt, cntVal, sumVal, idT)
;              VALUES(?, ?, ?, ?)"] {:multi? true :param rows})))

;(defn insertIntoTestTable []
;  (jdbc/execute! dbSpecs ["INSERT INTO testTable (idTt, cntVal, sumVal, idT)
;                        VALUES(?, ?, ?, ?)"][1 3 2 1])
;  )


