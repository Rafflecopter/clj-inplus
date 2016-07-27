(defproject inplus "0.1.1"
  :description "Extended functionality for *-in functions"
  :url "http://github.com/Rafflecopter/clj-inplus"
  :license {:name "MIT"
            :url "http://github.com/Rafflecopter/clj-inplus/blob/master/LICENSE"}

  :source-paths ["src"]
  :test-paths ["test"]


  :deploy-repositories [["clojars" {:creds :gpg}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version"
                   "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy" "clojars"]]


  :dependencies [[org.clojure/clojure "1.7.0"]]

  :aliases {"ctest" ["do" "test," "cljsbuild" "test"]}

  :profiles
    {:dev  {:dependencies [[org.clojure/clojurescript "0.0-3308"]]
            :plugins [[lein-cljsbuild "1.0.6"]
                      [com.cemerick/clojurescript.test "0.3.3"]]}}

   :cljsbuild {:test-commands {"unit" ["phantomjs" :runner
                                       "this.literal_js_was_evaluated=true"
                                       "target/js/unit-test.js"]}
               :builds
               [{:source-paths ["src" "test"]
                 :compiler {:output-to "target/js/unit-test.js"
                            :optimizations :whitespace
                            :pretty-print true}}]})
