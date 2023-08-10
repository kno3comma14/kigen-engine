(ns kigengames.kigen-engine.scene
  (:require [kigengames.kigen-engine.camera :as cam]))

(defprotocol SceneP
  (init [this])
  (process [this dt]))

(defrecord Scene [id name init-fn process-fn camera ctx]
  SceneP
  (init 
    [_]
    (let [_ (.createEntity ctx (java.util.HashMap {:bla "che"}))]
      )
    (.init @camera)
    (init-fn))
  (process 
    [_ dt] 
    (reset! camera (.update-position @camera (:position @camera) dt))
    (process-fn dt)))
