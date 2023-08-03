(ns kigengames.kigen-engine.scene)

(defprotocol SceneP
  (process [this dt])
  (init [this]))

(defrecord Scene [id name init-fn process-fn]
  SceneP
  (init [_] (init-fn))
  (process [_ dt] (process-fn dt)))