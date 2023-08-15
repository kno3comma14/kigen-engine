(ns kigengames.core
  (:gen-class)
  (:require [kigengames.kigen-engine.window :as w]
            [kigengames.sample-scenes-container :as sc]))

(defn -main
  [& _args]
  (w/run 1920 1080 "Sup!!!" sc/scene1))
