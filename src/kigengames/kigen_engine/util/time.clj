(ns kigengames.kigen-engine.util.time)

(def nanotime-value 1E-9)
(def start-time (System/nanoTime))

(defn get-time
  []
  (float (* (- (System/nanoTime) start-time) nanotime-value)))