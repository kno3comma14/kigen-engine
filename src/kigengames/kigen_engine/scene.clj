(ns kigengames.kigen-engine.scene
  (:require [kigengames.kigen-engine.data.dominion-handler :as dh]
            [kigengames.kigen-engine.camera :as cam]))

(defprotocol SceneP
  (init [this])
  (process [this dt]))

(defrecord Scene [id name init-fn process-fn camera ctx]
  SceneP
  (init 
    [_]
    (let [_ (.createEntity ctx (java.util.HashMap {:bla "che"}))
          camera1 (dh/find-entities-by-type ctx (type (java.util.HashMap {:bla "che"})))]
      (prn camera1))
    (.init @camera)
    (init-fn))
  (process 
    [_ dt] 
    (reset! camera (.update-position @camera (:position @camera) dt))
    (process-fn dt)))
