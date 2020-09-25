(defproject functional-domain-driven-design "0.0.0"
  :description "FIXME: write description"
  :url "https://github.com/naveen/functional-domain-driven-design"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/spec.alpha "0.2.187"]
                 [funcool/cats "2.3.6"]
                 [mock-clj "0.2.1"]
                 [org.clojure/core.match "1.0.0"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "1.1.0"]]
                   :source-paths ["dev"]}}
  :repl-options {:init-ns user})
