(ns kigengames.kigen-engine.rendering.sprite-renderer
  (:require [kigengames.kigen-engine.data.component :as c]
            [kigengames.kigen-engine.rendering.sprite :as spr])
  (:import (org.joml Vector4f Vector2f)))

(defprotocol SpriteRendererP
  (get-texture-coords [this])
  (process [this sr dt]))

(defrecord SpriteRenderer [color sprite transform init-fn update-fn is-dirty?]
  SpriteRendererP
  (get-texture-coords [_] ;; I will remove this
    (:texture-coords sprite))
  (process [_ sr dt]
    (let [new-spr (update-fn sr dt)
          changed? (or (not= (:color new-spr) color) (not (.equals? (:transform new-spr) transform)))]
      (update-fn (update new-spr :is-dirty? (fn [_] changed?)) dt))))

(defn create 
  [color sprite transform init-fn update-fn]
  (if (not color)
    (c/create (->SpriteRenderer (Vector4f. 1.0 1.0 1.0 1.0)
                      sprite
                      transform
                      init-fn
                      update-fn
                      false)
                      init-fn
                      update-fn)
    (c/create (->SpriteRenderer color
                      (spr/create nil) 
                      transform
                      init-fn
                      update-fn
                      false)
                      init-fn
                      update-fn)))