(defproject getinvolved
  "0.1.0-SNAPSHOT"
  :repl-options {:init-ns getinvolved.repl}
  :dependencies [[ring-server "0.3.1"]
                 [domina "1.0.2"]
                 [ragtime "0.3.4"]
                 [environ "0.4.0"]
                 [markdown-clj "0.9.41"]
                 [com.taoensso/timbre "3.1.6"]
                 [prismatic/dommy "0.1.2"]
                 [korma "0.3.0-RC6"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/clojure "1.6.0"]
                 [com.taoensso/tower "2.0.2"]
                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
                 [cljs-ajax "0.2.3"]
                 [selmer "0.6.5"]
                 [lib-noir "0.8.1"]
                 [compojure "1.1.6"]
                 [postgresql/postgresql "9.1-901.jdbc4"]]
  :cljsbuild {:builds
               [{:source-paths ["src-cljs"],
                 :compiler
                               {:pretty-print  false,
                                :output-to     "resources/public/js/site.js",
                                :optimizations :advanced}}]}
  :ring
  {:handler getinvolved.handler/app,
   :init    getinvolved.handler/init,
   :destroy getinvolved.handler/destroy}
  :ragtime {:migrations ragtime.sql.files/migrations,
            :database   "jdbc:postgresql://localhost/getinvolved?user=getinvolved&password=getinvolved"}
  :profiles {:uberjar    {:aot :all},
             :production {:ring
                           {:open-browser? false, :stacktraces? false, :auto-reload? false}},
             :dev        {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.2"]],
                          :env          {:dev true}}}
  :url "http://example.com/FIXME"
  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.4.0"]
            [ragtime/ragtime.lein "0.3.4"]
            [lein-cljsbuild "0.3.3"]]
  :description "FIXME: write description"
  :min-lein-version "2.0.0")