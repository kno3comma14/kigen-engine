(ns kigengames.core
  (:gen-class)
  (:require [kigengames.kigen-engine.rendering.window :as w] 
            [kigen-engine-samples.basic-samples :as bs]))

(defn -main
  [& _args]
  (w/run 1920 1080 "Sup!!!" bs/scene0))
