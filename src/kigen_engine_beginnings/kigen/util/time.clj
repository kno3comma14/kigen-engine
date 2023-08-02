(ns kigen-engine-beginnings.kigen.util.time)

(def nanotime-value 1E9)
(def start-time (System/nanoTime))

(defn get-time
  []
  (* (- (System/nanoTime) start-time) nanotime-value))