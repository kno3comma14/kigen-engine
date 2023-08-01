(ns kigen-engine-beginnings.core
  (:gen-class)
  (:require [kigen-engine-beginnings.kigen.window :as w]))

(defn -main
  [& _args]
  (w/run 300 300 "Sup!!!"))
