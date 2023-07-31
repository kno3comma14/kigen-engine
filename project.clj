(defproject kigen-engine-beginnings "0.1.0-SNAPSHOT"
  :description "Kigen Engine beginnigs"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [nrepl "1.0.0"]
                 [org.lwjgl/lwjgl "3.3.2"]
                 [org.lwjgl/lwjgl-opengl "3.3.2"]
                 [org.lwjgl/lwjgl-glfw "3.3.2"]]
  :main ^:skip-aot kigen-engine-beginnings.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :shared {:dependencies [[org.clojure/clojure "1.11.1"]]}
             :natives-macos-arm64 {:dependencies [[org.lwjgl/lwjgl "3.3.2" :classifier "natives-macos-arm64"]
                                                  [org.lwjgl/lwjgl-opengl "3.3.2" :classifier "natives-macos-arm64"]
                                                  [org.lwjgl/lwjgl-glfw "3.3.2" :classifier "natives-macos-arm64"]]
                                   :jvm-opts ["-XstartOnFirstThread"]}
             :macos-arm64 [:shared :natives-macos-arm64]})
