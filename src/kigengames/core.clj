(ns kigengames.core
  (:gen-class)
  (:require [kigengames.kigen-engine.window :as w]
            [kigengames.kigen-engine.scene :as scene]))

(def scene0 (scene/->Scene 0 "bla0" (fn [dt] (prn (str "dt: " dt)))))

(defn -main
  [& _args]
  (w/run 300 300 "Sup!!!" scene0))
