(ns kigengames.kigen-engine.scene)

(defprotocol SceneP
  (init [this])
  (process [this dt]))

(defrecord Scene [id name init-fn process-fn camera]
  SceneP
  (init 
    [_] 
    (.init @camera)
    (init-fn))
  (process 
    [_ dt] 
    (reset! camera (.update-position @camera (:position @camera) dt))
    (process-fn dt)))
