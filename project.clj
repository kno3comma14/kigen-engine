(defproject kigen-engine "0.1.0-SNAPSHOT"
  :description "Kigen Engine"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.taoensso/timbre "6.2.2"]
                 [nrepl "1.0.0"]]
  :main ^:skip-aot kigengames.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :shared {:dependencies [[org.lwjgl/lwjgl "3.3.2"]
                                     [org.lwjgl/lwjgl-opengl "3.3.2"]
                                     [org.lwjgl/lwjgl-glfw "3.3.2"]]}
             ;; macos-arm64 support
             :natives-macos-arm64 {:dependencies [[org.lwjgl/lwjgl "3.3.2" :classifier "natives-macos-arm64"]
                                                  [org.lwjgl/lwjgl-opengl "3.3.2" :classifier "natives-macos-arm64"]
                                                  [org.lwjgl/lwjgl-glfw "3.3.2" :classifier "natives-macos-arm64"]]
                                   :jvm-opts ["-XstartOnFirstThread"]}
             :macos-arm64 [:shared :natives-macos-arm64]
             ;; windows-x86 support
             :natives-windows-x86 {:dependencies [[org.lwjgl/lwjgl "3.3.2" :classifier "natives-windows-x86"]
                                                  [org.lwjgl/lwjgl-opengl "3.3.2" :classifier "natives-windows-x86"]
                                                  [org.lwjgl/lwjgl-glfw "3.3.2" :classifier "natives-windows-x86"]]
                                   :jvm-opts ["-XstartOnFirstThread"]}
             :windows-x86 [:shared :natives-windows-x86]
             ;; windows-amd64 support 
             :natives-windows-amd64 {:dependencies [[org.lwjgl/lwjgl "3.3.2" :classifier "natives-windows"]
                                                  [org.lwjgl/lwjgl-opengl "3.3.2" :classifier "natives-windows"]
                                                  [org.lwjgl/lwjgl-glfw "3.3.2" :classifier "natives-windows"]]
                                   :jvm-opts ["-XstartOnFirstThread"]}
             :windows-amd64 [:shared :natives-windows-amd64]})
