(ns kigen-engine-beginnings.kigen.scene)

(defprotocol SceneP
 (process [this dt]))

(defrecord Scene [id name process-fn]
 SceneP
 (process [_ dt] (process-fn dt)))