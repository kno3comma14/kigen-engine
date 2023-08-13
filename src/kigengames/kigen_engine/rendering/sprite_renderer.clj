(ns kigengames.kigen-engine.rendering.sprite-renderer
  (:require [kigengames.kigen-engine.data.component :as c]))

(defrecord SpriteRenderer [color init-fn update-fn]
  c/ComponentP
  (init [this]
    (init-fn this))
  (update [this dt]
    (update-fn this dt)))

(defn create[color init-fn update-fn]
  (let [sprite-renderer-instance (->SpriteRenderer color init-fn update-fn)]
    (c/create sprite-renderer-instance init-fn update-fn)))