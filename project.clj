(defproject clojure-application-project "0.1.0-SNAPSHOT"
  :description "Football match simulation in Clojure"
  :url "https://github.com/LukaHadzic/ClojureApplicationProject"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [midje "1.10.10"]
                 [com.github.seancorfield/next.jdbc "1.3.894"]
                 [com.mysql/mysql-connector-j "9.5.0"]]
  :plugins [[lein-midje "3.2.2"]]
  :main ^:skip-aot clojure-application-project.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
