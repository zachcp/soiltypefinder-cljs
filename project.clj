(defproject soiltypefinder "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
				 [org.clojure/clojure "1.6.0"]
				 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
				 [com.cognitect/transit-cljs "0.8.192"]
				 [org.clojure/clojurescript "0.0-2261"]
				 [com.cemerick/url "0.1.1"]
                 [prismatic/dommy "0.1.1"]
				 [cljs-ajax "0.3.3"]
				 ]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {
    :builds [{:source-paths ["src-cljs"]
              :compiler {:output-to "resources/js/main.js"
                         :optimizations :whitespace
                         :pretty-print true}}]}
  :main ^:skip-aot soiltypefinder.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
