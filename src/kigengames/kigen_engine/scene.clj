(ns kigengames.kigen-engine.scene
  (:require [kigengames.kigen-engine.data.default :as d]))

(defprotocol SceneP
  (init [this])
  (process [this dt]))

(defrecord Scene [id name init-fn init-drawable-elements-fn process-fn camera]
  SceneP
  (init
   [_]
   (d/init-default-context init-drawable-elements-fn)
   (.init @camera)
   (init-fn))
  (process 
    [_ dt] 
    (reset! camera (.update-position @camera (:transform @camera) dt))
    (d/process-default-context camera)
    (process-fn dt)))
