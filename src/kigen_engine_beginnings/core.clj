(ns kigen-engine-beginnings.core
  (:gen-class)
  (:require [kigen-engine-beginnings.kigen.window :as w]
            [kigen-engine-beginnings.kigen.scene :as scene]))

(def scene0 (scene/->Scene 0 "bla0" (fn [dt] (prn (str "dt: " dt)))))

(defn -main
  [& _args]
  (w/run 300 300 "Sup!!!" scene0))
