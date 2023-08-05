(ns kigengames.kigen-engine.scene)

(defprotocol SceneP
  (init [this])
  (process [this dt]))

(defrecord Scene [id name init-fn process-fn]
  SceneP
  (init [_] (init-fn))
  (process [_ dt] (process-fn dt)))
