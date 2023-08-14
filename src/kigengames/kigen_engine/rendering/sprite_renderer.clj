(ns kigengames.kigen-engine.rendering.sprite-renderer
  (:require [kigengames.kigen-engine.data.component :as c]))

(defrecord SpriteRenderer [color transform init-fn update-fn]
  c/ComponentP
  (init [this]
    (init-fn this))
  (process [this dt]
    (update-fn this dt)))

(defn create[color transform init-fn update-fn]
  (let [sprite-renderer-instance (->SpriteRenderer color transform init-fn update-fn)]
    (c/create sprite-renderer-instance init-fn update-fn)))