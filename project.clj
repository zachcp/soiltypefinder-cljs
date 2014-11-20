(defproject soiltypefinder "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
				 [org.clojure/clojure "1.6.0"]
				 [org.clojure/clojurescript "0.0-2261"]
				 [com.cemerick/url "0.1.1"]
				 [cljs-ajax "0.3.3"]
         [reagent "0.4.3"]
         [reagent-forms "0.2.6"]
				 ]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :hooks [leiningen.cljsbuild]
  :profiles {:prod {:cljsbuild
                  {:builds
                   {:client {:compiler
                             {:optimizations :advanced
                              :preamble ^:replace ["reagent/react.min.js"]
                              :pretty-print false}}}}}
           :srcmap {:cljsbuild
                    {:builds
                     {:client {:compiler
                               {:source-map "target/client.js.map"
                                :source-map-path "client"}}}}}}
  :source-paths ["src"]
  :cljsbuild
  {:builds
   {:client {:source-paths ["src"]
             :compiler
             {:preamble ["reagent/react.js"]
              :output-dir "target/client"
              :output-to "target/client.js"
              :pretty-print true}}}})
