(ns kigengames.kigen-engine.rendering.scene)

(defprotocol SceneP
  (init [this])
  (process [this dt]))

(defrecord Scene [id name init-fn process-fn camera renderer]
  SceneP
  (init
    [this] 
    (.init @camera)
    (init-fn this))
  (process
    [this dt]
    (reset! camera (.update-position @camera (:transform @camera) dt)) 
    (process-fn this dt)))
